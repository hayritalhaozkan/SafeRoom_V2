package com.saferoom.server;

import com.saferoom.natghost.LLS;
import com.saferoom.natghost.PeerInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server tarafı: Çoklu portları toplayıp karşılıklı port-port eşleme yapar.
 */
public class PeerListener extends Thread {

    /** Bir kullanıcının tüm port / IP durumunu tutan state objesi */
    static final class PeerState {
        final String host;     // sender(username)
        final String target;   // target(username)
        final byte   signal;
        InetAddress  ip;       // son gelen paketle güncellenebilir (genelde aynı)
        final Set<Integer> ports = Collections.synchronizedSet(new LinkedHashSet<>());

        PeerState(String host, String target, byte signal, InetAddress ip, int port) {
            this.host = host;
            this.target = target;
            this.signal = signal;
            this.ip = ip;
            this.ports.add(port);
        }

        void addPort(InetAddress newIp, int port) {
            this.ip = newIp;           // ip aynıysa değişmez, değilse son geleni koruyoruz
            this.ports.add(port);
        }
    }

    /** username -> PeerState */
    private static final Map<String, PeerState> STATES = new ConcurrentHashMap<>();

    public static final int UDP_PORT = 45000;
    private static final int MIN_PACKET_SIZE = 1 + 2 + 20 + 20; // (signal + len + 2×username) yaklaşık

    @Override
    public void run() {
        try (Selector selector = Selector.open();
             DatagramChannel channel = DatagramChannel.open()) {

            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(UDP_PORT));
            channel.register(selector, SelectionKey.OP_READ);

            System.out.println("UDP Listener running on port " + UDP_PORT);

            while (true) {
                if (selector.selectNow() == 0) continue;

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    // Okunabilir değilse atla
                    if (!key.isReadable()) continue;

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    SocketAddress clientAddr = channel.receive(buffer);
                    if (clientAddr == null) continue;

                    buffer.flip();
                    if (buffer.remaining() < MIN_PACKET_SIZE) {
                        System.out.println("[PeerListener] Packet too short (" + buffer.remaining() + "B), skip.");
                        continue;
                    }
                    if (!(clientAddr instanceof InetSocketAddress inetAddr)) continue;

                    // LLS paketini parse et
                    List<Object> parsed = LLS.parseMultiple_Packet(buffer.duplicate());
                    byte   signal = (Byte)  parsed.get(0);
                    short  length = (Short) parsed.get(1);
                    String sender = (String) parsed.get(2);
                    String target = (String) parsed.get(3);

                    InetAddress pubIp = inetAddr.getAddress();
                    int port = inetAddr.getPort();

                    System.out.printf(">> [%s] %s:%d  sig=0x%02X len=%d target=%s%n",
                            sender, pubIp.getHostAddress(), port, signal, length, target);

                    // STATE Güncelle
                    STATES.compute(sender, (k, old) -> {
                        if (old == null) return new PeerState(sender, target, signal, pubIp, port);
                        old.addPort(pubIp, port);
                        return old;
                    });

                    // Hedef taraf mevcut mu ve cross hedefleme var mı?
                    PeerState a = STATES.get(sender);
                    PeerState b = STATES.get(target);
                    if (b != null && b.target.equals(sender)) {
                        // CROSS MATCH OLUŞTU
                        crossSendPairwise(channel, a, b);
                        // Temizleyelim ki yeniden aynı eşleşme tetiklenmesin
                        STATES.remove(a.host);
                        STATES.remove(b.host);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Datagram Channel ERROR in PeerListener: " + ex);
        }
    }

    /**
     * a ve b'nin port listelerini 1:1 mapleyerek çift yönlü gönderir.
     * Boyutlar farklıysa, küçük liste bittiğinde round-robin devam eder.
     */
    private void crossSendPairwise(DatagramChannel ch, PeerState a, PeerState b) throws Exception {
        List<Integer> aPorts = new ArrayList<>(a.ports);
        List<Integer> bPorts = new ArrayList<>(b.ports);

        int max = Math.max(aPorts.size(), bPorts.size());
        for (int i = 0; i < max; i++) {
            int aPort = aPorts.get(i % aPorts.size());
            int bPort = bPorts.get(i % bPorts.size());

            // B'nin bilgisi A'nın port'una
            ByteBuffer toA = LLS.New_LLS_Packet(
                    b.signal, b.host, b.target, b.ip, bPort
            );
            ch.send(toA, new InetSocketAddress(a.ip, aPort));

            // A'nın bilgisi B'nin port'una
            ByteBuffer toB = LLS.New_LLS_Packet(
                    a.signal, a.host, a.target, a.ip, aPort
            );
            ch.send(toB, new InetSocketAddress(b.ip, bPort));
        }

        System.out.printf("<< Match complete: %s ↔ %s. Sent %d pairwise packets.\n",
                a.host, b.host, max * 2);
    }
}
