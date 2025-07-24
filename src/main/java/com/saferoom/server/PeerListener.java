package com.saferoom.server;

import com.saferoom.natghost.LLS;
import com.saferoom.natghost.PeerInfo;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PeerListener extends Thread {

    static final class PeerState {
        final String host;
        final String target;
        final byte   signal;
        InetAddress  ip;
        final Set<Integer> ports = Collections.synchronizedSet(new LinkedHashSet<>());

        // Karşı tarafa hangi portlarımızı yolladık?
        final Set<Integer> sentToTarget = Collections.synchronizedSet(new HashSet<>());

        // Karşı taraftan gelen portları hangi local portlara gönderdik?
        // (gerekirse duplicate önlemek için kullanılır)
        final Set<Integer> deliveredFromTarget = Collections.synchronizedSet(new HashSet<>());

        volatile long lastSeenMs;

        PeerState(String host, String target, byte signal, InetAddress ip, int port) {
            this.host = host;
            this.target = target;
            this.signal = signal;
            this.ip = ip;
            this.ports.add(port);
            this.lastSeenMs = System.currentTimeMillis();
        }

        void add(InetAddress ip, int port) {
            this.ip = ip;
            this.ports.add(port);
            this.lastSeenMs = System.currentTimeMillis();
        }
    }

    private static final Map<String, PeerState> STATES = new ConcurrentHashMap<>();

    public static final int UDP_PORT = 45000;
    private static final int MIN_PACKET_SIZE = 1 + 2 + 20 + 20;

    // Temizlik parametreleri
    private static final long STATE_TTL_MS        = 20_000;
    private static final long CLEANUP_INTERVAL_MS = 5_000;
    private long lastCleanup = System.currentTimeMillis();

    @Override
    public void run() {
        try (Selector selector = Selector.open();
             DatagramChannel channel = DatagramChannel.open()) {

            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(UDP_PORT));
            channel.register(selector, SelectionKey.OP_READ);

            System.out.println("UDP Listener running on port " + UDP_PORT);

            while (true) {
                selector.select(5);

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next(); it.remove();
                    if (!key.isReadable()) continue;

                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    SocketAddress from = channel.receive(buf);
                    if (from == null) continue;
                    buf.flip();
                    if (buf.remaining() < MIN_PACKET_SIZE) continue;
                    if (!(from instanceof InetSocketAddress inetAddr)) continue;

                    List<Object> parsed = LLS.parseMultiple_Packet(buf.duplicate());
                    byte   signal = (Byte)  parsed.get(0);
                    short  len    = (Short) parsed.get(1);
                    String sender = (String) parsed.get(2);
                    String target = (String) parsed.get(3);

                    InetAddress ip   = inetAddr.getAddress();
                    int         port = inetAddr.getPort();

                    PeerState me = STATES.compute(sender, (k, old) -> {
                        if (old == null) return new PeerState(sender, target, signal, ip, port);
                        old.add(ip, port);
                        return old;
                    });

                    System.out.printf(">> %s @ %s:%d ports=%s target=%s%n",
                            sender, ip.getHostAddress(), port, me.ports, me.target);

                    PeerState tgt = STATES.get(target);
                    if (tgt != null && tgt.target.equals(sender)) {
                        // Her iki taraf da var. Sadece YENİ portları gönder.
                        pushDeltas(channel, me, tgt);
                        pushDeltas(channel, tgt, me);
                    }
                }

                long now = System.currentTimeMillis();
                if (now - lastCleanup >= CLEANUP_INTERVAL_MS) {
                    cleanupExpired(now);
                    lastCleanup = now;
                }
            }

        } catch (Exception e) {
            System.err.println("PeerListener ERROR: " + e);
        }
    }

    /**
     * 'from' tarafındaki yeni portları 'to' tarafına gönder.
     * Her yeni portu 'to' tarafındaki tüm portlara dağıtıyoruz.
     * (İstersen 1:1 map yapacak şekilde round-robin'e çevirebilirsin.)
     */
    private void pushDeltas(DatagramChannel ch, PeerState from, PeerState to) throws Exception {
        // from.ports - from.sentToTarget = yeni portlar
        List<Integer> newPorts = new ArrayList<>();
        for (int p : from.ports)
            if (!from.sentToTarget.contains(p))
                newPorts.add(p);

        if (newPorts.isEmpty()) return;

        for (int newP : newPorts) {
            for (int toPort : to.ports) {
                ByteBuffer pkt = LLS.New_LLS_Packet(
                        from.signal, from.host, from.target,
                        from.ip, newP
                );
                ch.send(pkt, new InetSocketAddress(to.ip, toPort));
            }
            from.sentToTarget.add(newP);
        }

        System.out.printf("<< Pushed %d new ports from %s to %s%n",
                newPorts.size(), from.host, to.host);
    }

    private void cleanupExpired(long nowMs) {
        STATES.entrySet().removeIf(e -> (nowMs - e.getValue().lastSeenMs) > STATE_TTL_MS);
    }
}
