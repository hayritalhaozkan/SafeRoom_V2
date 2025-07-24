package com.saferoom.natghost;

import com.saferoom.client.ClientMenu;
import com.saferoom.server.SafeRoomServer;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.SecureRandom;
import java.util.*;

/**
 * Client:
 * - STUN ile NAT sinyali
 * - Çoklu DatagramChannel açıp server'a paket gönder
 * - Hedef gelene kadar tekrar tekrar gönder (resend)
 * - İlk paketten sonra da kısa bir süre daha dinleyerek tüm portları toplar
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

    private static final int   MIN_CHANNELS        = 4;     // Minimum delik sayısı
    private static final long  MATCH_TIMEOUT_MS    = 15_000;
    private static final long  QUIET_AFTER_LAST_MS = 500;   // son paketten sonra beklenen sessizlik
    private static final long  RESEND_INTERVAL_MS  = 1_000;
    private static final long  SELECT_BLOCK_MS     = 50;

    // ------------ STUN ------------
    private static ByteBuffer stunPacket() {
        ByteBuffer p = ByteBuffer.allocate(20);
        p.putShort((short) ((0x0001) & 0x3FFF)); // binding request
        p.putShort((short) 0);
        p.putInt(0x2112A442);
        byte[] tid = new byte[12];
        RNG.nextBytes(tid);
        p.put(tid);
        p.flip();
        return p;
    }

    private static void parseStunResponse(ByteBuffer buf, List<Integer> list) {
        buf.position(20);
        while (buf.remaining() >= 4) {
            short type = buf.getShort();
            short len  = buf.getShort();
            if (type == 0x0001) { // MAPPED-ADDRESS
                buf.get(); // ignore
                buf.get(); // family
                int port = buf.getShort() & 0xFFFF;
                byte[] ipb = new byte[4];
                buf.get(ipb);
                String ip = (ipb[0] & 0xFF) + "." + (ipb[1] & 0xFF) + "." + (ipb[2] & 0xFF) + "." + (ipb[3] & 0xFF);
                myPublicIP = ip;
                list.add(port);
            } else {
                buf.position(buf.position() + len);
            }
        }
    }

    private static <T> boolean allEqual(List<T> l) {
        if (l.isEmpty()) return true;
        T f = l.get(0);
        for (int i = 1; i < l.size(); i++) if (!Objects.equals(f, l.get(i))) return false;
        return true;
    }

    public static byte analyzer(String[][] servers) throws Exception {
        Selector sel = Selector.open();
        DatagramChannel ch = DatagramChannel.open();
        ch.configureBlocking(false);
        ch.bind(new InetSocketAddress(0));
        ch.register(sel, SelectionKey.OP_READ);

        for (String[] s : servers) {
            try { InetAddress.getByName(s[0]); } catch (UnknownHostException e) { continue; }
            ch.send(stunPacket().duplicate(), new InetSocketAddress(s[0], Integer.parseInt(s[1])));
        }

        long dl = System.nanoTime() + 100_000_000L;
        while (System.nanoTime() < dl) {
            if (sel.selectNow() == 0) continue;
            Iterator<SelectionKey> it = sel.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey k = it.next(); it.remove();
                DatagramChannel rc = (DatagramChannel) k.channel();
                ByteBuffer r = ByteBuffer.allocate(512);
                rc.receive(r);
                r.flip();
                parseStunResponse(r, Public_PortList);
            }
        }

        // uniq
        List<Integer> uniq = new ArrayList<>(new LinkedHashSet<>(Public_PortList));
        Public_PortList.clear();
        Public_PortList.addAll(uniq);

        signal = (Public_PortList.size() >= 2)
                ? (allEqual(Public_PortList) ? (byte)0x00 : (byte)0x11)
                : (byte)0xFE;

        System.out.println("[STUN] NAT signal = 0x" + String.format("%02X", signal));
        return signal;
    }

    // ------------ MULTIPLEXER ------------
    public static void multiplexer(InetSocketAddress serverAddr) throws Exception {
        byte sig = analyzer(stunServers);
        if (ClientMenu.myUsername == null || ClientMenu.target_username == null)
            throw new IllegalStateException("Username/Target null!");

        int holeCount = Math.max(Public_PortList.size(), MIN_CHANNELS);

        Selector selector = Selector.open();
        List<DatagramChannel> chans = new ArrayList<>(holeCount);
        ByteBuffer pkt = LLS.New_Multiplex_Packet(sig, ClientMenu.myUsername, ClientMenu.target_username);

        for (int i = 0; i < holeCount; i++) {
            DatagramChannel dc = DatagramChannel.open();
            dc.configureBlocking(false);
            dc.bind(new InetSocketAddress(0));
            dc.send(pkt.duplicate(), serverAddr);
            dc.register(selector, SelectionKey.OP_READ);
            chans.add(dc);

            InetSocketAddress local = (InetSocketAddress) dc.getLocalAddress();
            System.out.println("[Client] Sent to server from local port: " + local.getPort());
        }

        long start   = System.currentTimeMillis();
        long lastRcv = 0;
        long lastSnd = start;

        Set<Integer> remotePorts = new LinkedHashSet<>();
        InetAddress  remoteIP    = null;

        while (System.currentTimeMillis() - start < MATCH_TIMEOUT_MS) {
            selector.select(SELECT_BLOCK_MS);

            boolean got = false;
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

                if (remoteIP == null) remoteIP = peerIP;
                remotePorts.add(peerPort);
                System.out.printf("[Client] <<< peer %s:%d%n", peerIP.getHostAddress(), peerPort);

                new KeepStand(new InetSocketAddress(peerIP, peerPort), dc).start();

                lastRcv = System.currentTimeMillis();
                got = true;
            }

            // Sessizlik koşulu: en az bir port aldıysan ve QUIET_AFTER_LAST_MS geçtiyse çık
            if (!remotePorts.isEmpty() && lastRcv != 0 &&
                (System.currentTimeMillis() - lastRcv) > QUIET_AFTER_LAST_MS) {
                break;
            }

            // Hâlâ hiç cevap yoksa resend
            long now = System.currentTimeMillis();
            if (remotePorts.isEmpty() && (now - lastSnd) >= RESEND_INTERVAL_MS) {
                for (DatagramChannel dc : chans)
                    dc.send(pkt.duplicate(), serverAddr);
                lastSnd = now;
            }
        }

        System.out.println("[Client] Remote ports learned: " + remotePorts);
    }

    public static void main(String[] args) {
        InetSocketAddress serverAddr = new InetSocketAddress(
                SafeRoomServer.ServerIP,
                SafeRoomServer.udpPort1
        );
        try {
            multiplexer(serverAddr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
