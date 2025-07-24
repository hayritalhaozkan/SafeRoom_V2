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
 * Client: STUN → NAT sinyali → çoklu kanal aç → server'a paket gönder.
 * Target yoksa hemen çıkma: belirli süre bekle, gerekirse tekrar gönder.
 */
public class NatAnalyzer {

    public static final List<Integer> Public_PortList = new ArrayList<>();
    public static String myPublicIP;
    public static byte   signal;

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

    public static final SocketAddress SERVER_SOCKET =
            new InetSocketAddress(ClientMenu.Server, ClientMenu.UDP_Port);

    /* === Parametreler === */
    private static final int   MIN_CHANNELS        = 4;
    private static final long  MATCH_TIMEOUT_MS    = 10_000;  // Target'ı bekleme süresi
    private static final long  RESEND_INTERVAL_MS  = 1_000;   // Server'a keepalive/resend
    private static final long  SELECT_TIMEOUT_MS   = 50;      // select() blok süresi

    // ---------- STUN ------------
    public static ByteBuffer Stun_Packet() {
        ByteBuffer packet = ByteBuffer.allocate(20);
        packet.putShort((short) ((0x0001) & 0x3FFF));
        packet.putShort((short) 0);
        packet.putInt(0x2112A442);

        byte[] transactionID = new byte[12];
        RNG.nextBytes(transactionID);
        packet.put(transactionID);
        packet.flip();
        return packet;
    }

    public static void parseStunResponse(ByteBuffer buffer, List<Integer> PublicPortList) {
        buffer.position(20);
        while (buffer.remaining() >= 4) {
            short attrType = buffer.getShort();
            short attrLen  = buffer.getShort();

            if (attrType == 0x0001) { // MAPPED-ADDRESS
                buffer.get(); // ignore 1 byte
                buffer.get(); // family
                int port = buffer.getShort() & 0xFFFF;
                byte[] addrBytes = new byte[4];
                buffer.get(addrBytes);

                String ip = String.format("%d.%d.%d.%d",
                        addrBytes[0] & 0xFF,
                        addrBytes[1] & 0xFF,
                        addrBytes[2] & 0xFF,
                        addrBytes[3] & 0xFF);

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
        T f = list.get(0);
        for (int i = 1; i < list.size(); i++)
            if (!Objects.equals(f, list.get(i))) return false;
        return true;
    }

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
                InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                System.err.println("[STUN] DNS fail: " + host);
                continue;
            }
            ch.send(Stun_Packet().duplicate(), new InetSocketAddress(host, port));
        }

        long deadline = System.nanoTime() + 100_000_000L;
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
        // uniq
        List<Integer> uniq = new ArrayList<>(new LinkedHashSet<>(Public_PortList));
        Public_PortList.clear();
        Public_PortList.addAll(uniq);

        if (Public_PortList.size() >= 2) {
            signal = allEqual(Public_PortList) ? (byte) 0x00 : (byte) 0x11;
        } else {
            signal = (byte) 0xFE;
        }
        System.out.println("[STUN] NAT signal = " + String.format("0x%02X", signal));
        return signal;
    }

    // ---------- MULTIPLEXER ----------
    public static void multiplexer(InetSocketAddress serverAddr) throws Exception {
        byte sig = analyzer(stunServers);
        if (ClientMenu.myUsername == null || ClientMenu.target_username == null)
            throw new IllegalStateException("Username/Target atanmadı!");

        int holeCount = Math.max(Public_PortList.size(), MIN_CHANNELS);

        Selector selector = Selector.open();
        List<DatagramChannel> channels = new ArrayList<>(holeCount);
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

        long start = System.currentTimeMillis();
        long lastSend = start;
        boolean matched = false;
        Set<Integer> remotePorts = new LinkedHashSet<>();

        while ((System.currentTimeMillis() - start) < MATCH_TIMEOUT_MS && !matched) {

            // Bekleme
            selector.select(SELECT_TIMEOUT_MS);

            // Gelen paketleri işle
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next(); it.remove();
                if (!key.isReadable()) continue;

                DatagramChannel dc = (DatagramChannel) key.channel();
                ByteBuffer buf = ByteBuffer.allocate(512);
                SocketAddress from = dc.receive(buf);
                if (from == null) continue;
                buf.flip();

                List<Object> info = LLS.parseLLSPacket(buf);
                InetAddress peerIP  = (InetAddress) info.get(4);
                int        peerPort = (int)        info.get(5);

                System.out.printf("[Client] <<< peer %s:%d\n",
                        peerIP.getHostAddress(), peerPort);

                remotePorts.add(peerPort);

                // KeepStand başlat
                new KeepStand(new InetSocketAddress(peerIP, peerPort), dc).start();
                matched = true; // en az bir port geldiyse eşleşti sayıyoruz
            }

            // Target hâlâ gelmediyse periyodik resend
            long now = System.currentTimeMillis();
            if (!matched && (now - lastSend) >= RESEND_INTERVAL_MS) {
                for (DatagramChannel dc : channels) {
                    dc.send(pkt.duplicate(), serverAddr);
                }
                lastSend = now;
            }
        }

        if (!matched) {
            System.out.println("[Client] Match timeout. Target don't responded yet.");
        } else {
            System.out.println("[Client] Remote ports learned: " + remotePorts);
        }
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
