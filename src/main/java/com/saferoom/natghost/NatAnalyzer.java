package com.saferoom.natghost;

import com.saferoom.client.ClientMenu;
import com.saferoom.server.SafeRoomServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.SecureRandom;
import java.util.*;

/**
 * Client tarafı: STUN analizi + çoklu UDP soketi açıp server'a paket gönderme,
 * server'dan gelenleri yakalayıp karşı tarafla P2P başlatma.
 */
public class NatAnalyzer {

    // ---- STUN & NAT TESPİT ----
    public static final List<Integer> Public_PortList = new ArrayList<>();
    public static String myPublicIP;
    public static byte signal;

    private static final SecureRandom RNG = new SecureRandom();

    public static final String[][] stunServers = {
            {"stun1.l.google.com", "19302"},
            {"stun2.l.google.com", "19302"},
            {"stun.stunprotocol.org", "3478"},
            {"stun.ekiga.net", "3478"},
            {"stun.schlund.de", "3478"},
            {"stun.voipgate.com", "3478"},
            {"stun.xten.com", "3478"},
            {"stun.antisip.com", "3478"},
            {"stun.server.org", "3478"}
    };

    // Server adresi (ClientMenu içinden geliyor)
    public static final SocketAddress SERVER_SOCKET =
            new InetSocketAddress(ClientMenu.Server, ClientMenu.UDP_Port);

    /** STUN için 20 byte'lık basit Binding Request */
    public static ByteBuffer Stun_Packet() {
        ByteBuffer packet = ByteBuffer.allocate(20);
        packet.putShort((short) ((0x0001) & 0x3FFF)); // message type 0x0001 (binding) & mask
        packet.putShort((short) 0);                   // length = 0
        packet.putInt(0x2112A442);                    // magic cookie

        byte[] transactionID = new byte[12];
        RNG.nextBytes(transactionID);
        packet.put(transactionID);
        packet.flip();
        return packet;
    }

    /** STUN cevabından MAPPED-ADDRESS çek */
    public static void parseStunResponse(ByteBuffer buffer, List<Integer> PublicPortList) {
        buffer.position(20);
        while (buffer.remaining() >= 4) {
            short attrType = buffer.getShort();
            short attrLen  = buffer.getShort();

            if (attrType == 0x0001) { // MAPPED-ADDRESS
                buffer.get(); // ignore 1 byte (unused)
                buffer.get(); // family (IPv4 = 0x01)
                int port = buffer.getShort() & 0xFFFF;
                byte[] addrBytes = new byte[4];
                buffer.get(addrBytes);

                String ip = String.format("%d.%d.%d.%d",
                        addrBytes[0] & 0xFF,
                        addrBytes[1] & 0xFF,
                        addrBytes[2] & 0xFF,
                        addrBytes[3] & 0xFF
                );

                System.out.println("[STUN] Public IP: " + ip + " | Port: " + port);
                myPublicIP = ip;
                PublicPortList.add(port);
            } else {
                buffer.position(buffer.position() + attrLen);
            }
        }
    }

    public static <T> boolean allEqual(List<T> list) {
        if (list.isEmpty()) return true;
        T first = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (!Objects.equals(first, list.get(i))) return false;
        }
        return true;
    }

    /** STUN sunucularına parallel request atıp 100ms bekler, NAT tip sinyali döner */
    public static byte analyzer(String[][] stun_servers) throws IOException {
        Selector selector = Selector.open();
        DatagramChannel ch = DatagramChannel.open();
        ch.configureBlocking(false);
        ch.bind(new InetSocketAddress(0));
        ch.register(selector, SelectionKey.OP_READ);

        for (String[] stun : stun_servers) {
            String host = stun[0];
            int    port = Integer.parseInt(stun[1]);
            try {
                InetAddress.getByName(host); // DNS test
            } catch (UnknownHostException e) {
                System.err.println("[STUN] DNS resolve fail: " + host);
                continue;
            }
            SocketAddress stunAddr = new InetSocketAddress(host, port);
            ch.send(Stun_Packet().duplicate(), stunAddr);
        }

        long deadline = System.nanoTime() + 100_000_000L; // 100ms
        while (System.nanoTime() < deadline) {
            if (selector.selectNow() == 0) continue;

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next(); it.remove();
                DatagramChannel rcvCh = (DatagramChannel) key.channel();

                ByteBuffer recv = ByteBuffer.allocate(512);
                rcvCh.receive(recv);
                recv.flip();
                parseStunResponse(recv, Public_PortList);
            }
        }
        // dedupe
        List<Integer> uniq = new ArrayList<>(new LinkedHashSet<>(Public_PortList));
        Public_PortList.clear();
        Public_PortList.addAll(uniq);

        if (Public_PortList.size() >= 2) {
            signal = allEqual(Public_PortList) ? (byte) 0x00 : (byte) 0x11;
        } else {
            signal = (byte) 0xFE;
        }
        System.out.println("[STUN] NAT Type Signal: " + String.format("0x%02X", signal));
        return signal;
    }

    // ---- MULTIPLEXER / HOLE-PUNCH ----

    /**
     * İstediğin gibi: KAÇ port açacağımız argüman değil.
     * Burada iki seçenek var:
     *  a) Aşağıdaki sabit ile belirle (ör: 5)
     *  b) STUN’dan dönen port sayısını kullan
     *
     * Aşağıda b)’yi yaptım: Public_PortList.size() kadar kanal açıyor.
     * Boşsa en az 1 yapıyoruz.
     */
    private static final int MIN_CHANNELS = 1;

    public static void multiplexer(InetSocketAddress serverAddr) throws IOException, InterruptedException {
        // STUN NAT analizi
        byte sig = analyzer(stunServers);

        if (ClientMenu.myUsername == null || ClientMenu.target_username == null)
            throw new IllegalStateException("Kullanıcı adı veya hedef atanmadı!");

        // Açacağımız kanal sayısı (parametre yok)
        int holeCount = Math.max(Public_PortList.size(), MIN_CHANNELS);

        Selector selector = Selector.open();
        List<DatagramChannel> channels = new ArrayList<>(holeCount);

        // Sunucuya gidecek temel paket (IP/port’u server UDP header’dan okuyacak)
        ByteBuffer pkt = LLS.New_Multiplex_Packet(sig, ClientMenu.myUsername, ClientMenu.target_username);

        for (int i = 0; i < holeCount; i++) {
            DatagramChannel dc = DatagramChannel.open();
            dc.configureBlocking(false);
            dc.bind(new InetSocketAddress(0));
            dc.send(pkt.duplicate(), serverAddr);
            dc.register(selector, SelectionKey.OP_READ);
            channels.add(dc);

            InetSocketAddress local = (InetSocketAddress) dc.getLocalAddress();
            System.out.println("[Client] Sent to server from local port: " + local.getPort());
        }

        // Server’dan gelen peer info paketlerini al
        // Not: 100ms agresif olabilir, biraz daha geniş tuttum.
        long deadline = System.nanoTime() + 300_000_000L; // 300ms
        Set<Integer> remotePorts = new LinkedHashSet<>(); // karşı tarafın port’ları

        while (System.nanoTime() < deadline) {
            if (selector.selectNow() == 0) continue;

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next(); it.remove();
                if (!key.isReadable()) continue;

                DatagramChannel dc = (DatagramChannel) key.channel();
                ByteBuffer buf = ByteBuffer.allocate(512);
                SocketAddress from = dc.receive(buf);
                if (from == null) continue;
                buf.flip();

                List<Object> pp = LLS.parseLLSPacket(buf);
                byte  msgType   = (byte)  pp.get(0);
                short msgLen    = (short) pp.get(1);
                String fromUser = (String) pp.get(2);
                String toUser   = (String) pp.get(3);
                InetAddress peerIP   = (InetAddress) pp.get(4);
                int        peerPort  = (int)        pp.get(5);

                System.out.printf("[Client] <<< from %s | IP:%s Port:%d (type=0x%02X len=%d)%n",
                        fromUser, peerIP.getHostAddress(), peerPort, msgType, msgLen);

                remotePorts.add(peerPort);

                // Bu kanalda karşıya keepalive başlat
                InetSocketAddress peerSock = new InetSocketAddress(peerIP, peerPort);
                new KeepStand(peerSock, dc).start();
            }
        }

        System.out.println("[Client] Remote port list (learned) = " + remotePorts);

        // Kanalları burada kapatmak ister misin? P2P devreden sonra kapatmak mantıklı olabilir.
        // channels.forEach(c -> { try { c.close(); } catch (IOException ignored) {} });
        // selector.close();
    }

    public static void main(String[] args) {
        InetSocketAddress serverAddr =
                new InetSocketAddress(SafeRoomServer.ServerIP, SafeRoomServer.udpPort1);
        try {
            multiplexer(serverAddr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
