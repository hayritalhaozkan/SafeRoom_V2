package com.saferoom.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.saferoom.natghost.LLS;
import com.saferoom.natghost.PeerInfo;

public class PeerListener extends Thread {
    public static Map<String, PeerInfo> PeerMap = new ConcurrentHashMap<>();
    public static Map<PeerInfo, PeerInfo> MatchMap = new ConcurrentHashMap<>();

    public static int udpPort1 = 45000;

    @Override
    public void run() {
        try {
            Selector selector = Selector.open();
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(udpPort1));
            channel.register(selector, SelectionKey.OP_READ);

            System.out.println("UDP Listener running on port " + udpPort1);

            // Bu sabit, parseMultiple_Packet'in okuması gereken minimum header boyutu
            final int MIN_PACKET_SIZE = 1 + 2 + 20 + 20; // 43 byte

            while (true) {
                if (selector.selectNow() == 0) 
                    continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    SocketAddress clientAddr = channel.receive(buffer);
                    if (clientAddr == null) 
                        continue;

                    buffer.flip();

                    // Paket yeterince büyük değilse atla
                    if (buffer.remaining() < MIN_PACKET_SIZE) {
                        System.out.println("Gelen paket çok kısa (" + buffer.remaining() + " bytes), atlanıyor.");
                        continue;
                    }

                    if (clientAddr instanceof InetSocketAddress inetAddr) {
                        try {
                            List<Object> parsed = LLS.parseMultiple_Packet(buffer.duplicate());
                            byte signal = (byte) parsed.get(0);
                            String username = (String) parsed.get(2);
                            String target   = (String) parsed.get(3);
                            java.net.InetAddress publicIp = inetAddr.getAddress();
                            int port = inetAddr.getPort();

                            PeerInfo peer = new PeerInfo(username, target, signal, publicIp, port);
                            PeerMap.put(username, peer);

                            PeerInfo targetInfo = PeerMap.get(target);
                            if (targetInfo != null && Objects.equals(targetInfo.Target, username)) {
                                ByteBuffer infoToHost = LLS.New_LLS_Packet(
                                        targetInfo.signal,
                                        targetInfo.Host,
                                        targetInfo.Target,
                                        targetInfo.PublicInfo,
                                        targetInfo.Port
                                );
                                channel.send(infoToHost, new InetSocketAddress(peer.PublicInfo, peer.Port));

                                ByteBuffer infoToTarget = LLS.New_LLS_Packet(
                                        signal,
                                        username,
                                        target,
                                        publicIp,
                                        port
                                );
                                channel.send(infoToTarget, new InetSocketAddress(targetInfo.PublicInfo, targetInfo.Port));

                                PeerMap.remove(username);
                                PeerMap.remove(target);
                            }

                        } catch (IllegalArgumentException e) {
                            // Özellikle bizim eklediğimiz "packet too short" durumları
                            System.out.println("Parse uyarısı: " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("Beklenmeyen parse hatası: " + e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Datagram Channel ERROR: " + e);
        }
    }
}
