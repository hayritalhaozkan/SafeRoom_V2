package com.saferoom.p2p;

import com.saferoom.db.DBManager;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bitlet.weupnp.GatewayDevice;         // UPnP kütüphanesi (weupnp)
import org.bitlet.weupnp.GatewayDiscover;

/**
 * Gelişmiş Hole Puncher:
 *  1) UPnP ile port açma (IGD)
 *  2) Dinamik port aralığı üzerinden Hole Punching
 *  3) Symmetric NAT durumunda TURN fallback
 *  4) AES-GCM ile paket şifreleme
 *  5) Çoklu deneme ve daha sağlam hata yönetimi
 */
public class AdvancedHolePuncher {

    // Dinamik port aralığı
    private static final int PORT_START = 45000;
    private static final int PORT_END   = 45010;

    // Deneme sayıları, zaman aşımı, vs.
    private static final int MAX_TRIES_PER_PORT = 3;
    private static final int SOCKET_TIMEOUT_MS  = 2000;   // Her deneme 2sn
    private static final int TOTAL_MAX_DURATION_MS = 30000; // Tüm süreç max 30sn
    private static final long SLEEP_BETWEEN_PORTS_MS = 1000;

    // Şifreleme parametreleri
    private static final int AES_KEY_BITS = 128;
    private static final int GCM_TAG_BITS = 128;
    private static final int NONCE_BYTES  = 12;   // AES-GCM nonce boyutu

    // TURN sunucu bilgisi (örnek)
    private static final String TURN_SERVER_HOST = "turn.myserver.com";
    private static final int    TURN_SERVER_PORT = 3478;

    /**
     * startHolePunching:
     * 1) Veritabanından ip/port bilgisi alınır (kendi ve hedefin)
     * 2) UPnP ile port açma (isteğe bağlı) 
     * 3) Dinamik port aralığı tarama
     * 4) Eğer başarısızsa TURN fallback
     * 5) AES-GCM şifreleme
     */
    public static void startHolePunching(String myUsername, String targetUser) {
        long startTime = System.currentTimeMillis();

        try {
            // 1) Veritabanından kendi ve hedefin STUN bilgisi
            String[] myStun    = DBManager.getSTUN_INFO(myUsername);  // me[0] = IP, me[1] = port
            String[] targetStun= DBManager.getSTUN_INFO(targetUser);  // target[0], target[1]

            if (myStun[0] == null || targetStun[0] == null) {
                System.err.println("[Error] Gerekli STUN verileri eksik. Hole Punching yapılamaz.");
                return;
            }

            String myIP   = myStun[0];
            String peerIP = targetStun[0];
            int peerPort  = Integer.parseInt(targetStun[1]);

            System.out.println("\n=== Gelişmiş Hole Punching Başlıyor ===");
            System.out.println("Kendi IP: " + myIP + " | Hedef IP: " + peerIP + ":" + peerPort);

            // 2) AES Key oluştur
            SecretKey aesKey = generateAESKey(AES_KEY_BITS);

            // 3) UPnP ile port açma denemesi
            int upnpPort = upnpPortMappingAttempt();
            if (upnpPort != -1) {
                System.out.println("[UPnP] Otomatik port açma başarılı, port: " + upnpPort);
                // Dilerseniz NAT delme çok kolaylaşır
            } else {
                System.out.println("[UPnP] Router port açma desteklemiyor veya başarısız oldu.");
            }

            // 4) Dinamik port aralığı üzerinden Hole Punching
            boolean success = false;
            for (int p = PORT_START; p <= PORT_END && !success; p++) {
                // Zaman doldu mu?
                if ((System.currentTimeMillis() - startTime) > TOTAL_MAX_DURATION_MS) {
                    break;
                }
                success = tryHolePunchPort(myIP, p, peerIP, peerPort, aesKey);
                if (!success) {
                    Thread.sleep(SLEEP_BETWEEN_PORTS_MS);
                }
            }

            // 5) Sonuç
            if (success) {
                System.out.println("[HolePunch] NAT delindi, P2P iletişim mümkün!");
            } else {
                System.out.println("[HolePunch] Başarısız. Symmetric NAT veya engel olabilir. TURN fallback devreye giriyor...");
                doTurnFallback(myIP, peerIP, peerPort, aesKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * UPnP ile port açma (IGD) denemesi.
     * weupnp kütüphanesi örneği. Gerçek projede try-catch ekleyin, 
     * router destekliyorsa NAT port mapping başarı dönebilir.
     * 
     * @return port (örnek: 45000) veya -1
     */
    private static int upnpPortMappingAttempt() {
        int desiredPort = 45000;
        try {
            GatewayDiscover discover = new GatewayDiscover();
            discover.discover();
            GatewayDevice gd = discover.getValidGateway();

            if(gd == null) {
                return -1;
            }
            // local IP
            String localHostIP = InetAddress.getLocalHost().getHostAddress();

            boolean mapped = gd.addPortMapping(
                desiredPort,         // external port
                desiredPort,         // internal port
                localHostIP,         // local IP
                "UDP",               // protocol
                "HolePunchMapping"   // description
            );
            if (mapped) {
                return desiredPort;
            } else {
                return -1;
            }

        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Tek bir port üzerinden hole punching. 
     * Belirli sayıda deneme => "MAX_TRIES_PER_PORT"
     * AES-GCM ile şifrelenmiş paket gönderir/alır.
     */
    private static boolean tryHolePunchPort(String myIP, int myPort, 
                                            String peerIP, int peerPort,
                                            SecretKey aesKey) 
    {
        System.out.println("[HolePunch] Port denemesi: " + myPort);
        try (DatagramSocket ds = new DatagramSocket(myPort)) {
            ds.setSoTimeout(SOCKET_TIMEOUT_MS);
            ds.setReuseAddress(true);

            // Her denemede 1-2 kez paket gönderip yanıt bekle
            for (int i = 1; i <= MAX_TRIES_PER_PORT; i++) {
                // Paket içeriği
                String plainMsg = "HolePunch Attempt " + i + " at port " + myPort;
                byte[] cipherData = encryptAES_GCM(plainMsg, aesKey);

                DatagramPacket dp = new DatagramPacket(cipherData, cipherData.length,
                                        InetAddress.getByName(peerIP), peerPort);
                ds.send(dp);
                System.out.println("[send] " + plainMsg);

                // Karşıdan yanıt
                try {
                    byte[] buf = new byte[2048];
                    DatagramPacket recv = new DatagramPacket(buf, buf.length);
                    ds.receive(recv);

                    // decrypt
                    byte[] incoming = Arrays.copyOfRange(recv.getData(), 0, recv.getLength());
                    String incPlain = decryptAES_GCM(incoming, aesKey);
                    System.out.println("[receive] " + incPlain);

                    // NAT delindi
                    return true;
                } catch (SocketTimeoutException ste) {
                    System.out.println("[timeout] deneme = " + i + ", port=" + myPort);
                }
            }
            return false;

        } catch (Exception e) {
            System.err.println("[HolePunch] port=" + myPort + " hata: " + e.getMessage());
            return false;
        }
    }

    /**
     * TURN fallback: 
     * Gerçek projede ICE4J veya benzeri ile TURN allocate / createPermission yaparak 
     * relay address üzerinden peer'e bağlantı sağlanır.
     */
    private static void doTurnFallback(String myIP, String peerIP, int peerPort, SecretKey aesKey) {
        try {
            System.out.println("[TURN] Bağlanılıyor => " + TURN_SERVER_HOST + ":" + TURN_SERVER_PORT);
            // 1) allocate
            // 2) relay address al
            // 3) peer'e relay üzerinden paket yolla

            // Örnek: 
            //   IceAgent agent = new IceAgent();
            //   agent.createStream("data");
            //   StunMappingCandidateHarvester turnHarvester = new TurnCandidateHarvester(...);
            //   agent.addCandidateHarvester(turnHarvester);
            //   ...
            
            System.out.println("[TURN] Relay üzerinden iletişim sağlanabilir.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * AES-GCM ile şifreleme, 
     *  - random 12 byte nonce 
     *  - 128 bit tag
     */
    private static byte[] encryptAES_GCM(String plain, SecretKey aesKey) throws Exception {
        byte[] plaintext = plain.getBytes("UTF-8");

        // Random 12 byte nonce
        byte[] nonce = new byte[NONCE_BYTES];
        new SecureRandom().nextBytes(nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_BITS, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);

        byte[] encrypted = cipher.doFinal(plaintext);

        // nonce + encrypted
        byte[] result = new byte[nonce.length + encrypted.length];
        System.arraycopy(nonce, 0, result, 0, nonce.length);
        System.arraycopy(encrypted, 0, result, nonce.length, encrypted.length);

        return result;
    }

    /**
     * AES-GCM decrypt
     *  - ilk 12 byte = nonce
     */
    private static String decryptAES_GCM(byte[] input, SecretKey aesKey) throws Exception {
        if (input.length < NONCE_BYTES) {
            throw new Exception("Geçersiz veri");
        }
        byte[] nonce = Arrays.copyOfRange(input, 0, NONCE_BYTES);
        byte[] cipherBytes = Arrays.copyOfRange(input, NONCE_BYTES, input.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_BITS, nonce);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);

        byte[] plain = cipher.doFinal(cipherBytes);
        return new String(plain, "UTF-8");
    }

    /**
     * AES key üretiliyor
     */
    private static SecretKey generateAESKey(int bits) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(bits, new SecureRandom());
        return kg.generateKey();
    }
}
