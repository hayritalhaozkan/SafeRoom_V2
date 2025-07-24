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

/**
 * Server: Gelen her paketten kullanıcıya ait public port'u kaydeder.
 * Target geldiğinde port-port eşleme yapıp her porta ayrı yanıt yollar.
 * Target henüz gelmediyse state bekletilir; TTL dolunca temizlenir.
 */
public class PeerListener extends Thread {

    /** Tek kullanıcının state'i */
    static final class PeerState {
        final String host;
        final String target;
        final byte   signal;
        InetAddress  ip;                  // son görülen public IP
        final Set<Integer> ports = Collections.synchronizedSet(new LinkedHashSet<>());
        volatile long lastSeen;           // en son paket zamanı (ms)

        PeerState(String host, String target, byte signal, InetAddress ip, int port) {
            this.host = host;
            this.target = target;
            this.signal = signal;
            this.ip = ip;
            this.ports.add(port);
            this.lastSeen = System.currentTimeMillis();
        }

        void add(InetAddress ip, int port) {
            this.ip = ip;
            this.ports.add(port);
            this.lastSeen = System.currentTimeMillis();
        }
    }

    /** username -> PeerState */
    private static final Map<String, PeerState> STATES = new ConcurrentHashMap<>();

    public static final int UDP_PORT = 45000;

    // Bekleme / temizlik parametreleri
    private static final long STATE_TTL_MS       = 20_000; // target gelmezse 20 sn sonra sil
    private static final long CLEANUP_INTERVAL_MS= 5_000;  // 5 sn'de bir state temizle
    private long lastCleanup = System.currentTimeMillis();

    // Paket kontrolü için minimum boy (yaklaşık)
    private static final int MIN_PACKET_SIZE = 1 + 2 + 20 + 20;

    @Override
    public void run() {
        try (Selector selector = Selector.open();
             DatagramChannel channel = DatagramChannel.open()) {

            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(UDP_PORT));
            channel.register(selector, SelectionKey.OP_READ);

            System.out.println("UDP Listener running on port " + UDP_PORT);

            while (true) {
                // 5ms bekleyip bloklama (çok yüksek frekansta dönmesin)
                selector.select(5);

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (!key.isReadable()) continue;

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    SocketAddress clientAddr = channel.receive(buffer);
                    if (clientAddr == null) continue;
                    buffer.flip();
                    if (buffer.remaining() < MIN_PACKET_SIZE) continue;

                    if (!(clientAddr instanceof InetSocketAddress inetAddr)) continue;

                    // LLS parse
                    List<Object> parsed = LLS.parseMultiple_Packet(buffer.duplicate());
                    byte   signal = (Byte)  parsed.get(0);
                    short  length = (Short) parsed.get(1);
                    String sender = (String) parsed.get(2);
                    String target = (String) parsed.get(3);

                    InetAddress ip = inetAddr.getAddress();
                    int         port = inetAddr.getPort();

                    // STATE güncelle
                    PeerState a = STATES.compute(sender, (k, old) -> {
                        if (old == null) return new PeerState(sender, target, signal, ip, port);
                        old.add(ip, port);
                        return old;
                    });

                    System.out.printf(">> %s @ %s:%d (target=%s, ports=%s)\n",
                            sender, ip.getHostAddress(), port, target, a.ports);

                    // Target geldiyse eşleşmeyi yap
                    PeerState b = STATES.get(target);
                    if (b != null && b.target.equals(sender)) {
                        crossSendPairwise(channel, a, b);
                        STATES.remove(a.host);
                        STATES.remove(b.host);
                    }
                }

                // Periyodik temizlik
                long now = System.currentTimeMillis();
                if (now - lastCleanup >= CLEANUP_INTERVAL_MS) {
                    cleanupExpiredStates(now);
                    lastCleanup = now;
                }
            }
        } catch (Exception ex) {
            System.err.println("Datagram Channel ERROR in PeerListener: " + ex);
        }
    }

    /** A ve B'nin port listelerini 1:1 mapleyerek çift yönlü gönder. */
    private void crossSendPairwise(DatagramChannel ch, PeerState a, PeerState b) throws Exception {
        List<Integer> aPorts = new ArrayList<>(a.ports);
        List<Integer> bPorts = new ArrayList<>(b.ports);
        int max = Math.max(aPorts.size(), bPorts.size());

        for (int i = 0; i < max; i++) {
            int aPort = aPorts.get(i % aPorts.size());
            int bPort = bPorts.get(i % bPorts.size());

            // B bilgisi A'nın portuna
            ByteBuffer toA = LLS.New_LLS_Packet(b.signal, b.host, b.target, b.ip, bPort);
            ch.send(toA, new InetSocketAddress(a.ip, aPort));

            // A bilgisi B'nin portuna
            ByteBuffer toB = LLS.New_LLS_Packet(a.signal, a.host, a.target, a.ip, aPort);
            ch.send(toB, new InetSocketAddress(b.ip, bPort));
        }

        System.out.printf("<< Match %s ↔ %s (sent %d pairwise pkts)\n", a.host, b.host, max * 2);
    }

    /** Target hiç gelmemişse, TTL dolunca state'i sil. */
    private void cleanupExpiredStates(long now) {
        STATES.entrySet().removeIf(e -> (now - e.getValue().lastSeen) > STATE_TTL_MS);
    }
}
