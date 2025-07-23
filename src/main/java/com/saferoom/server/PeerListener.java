package com.saferoom.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.saferoom.natghost.LLS;
import com.saferoom.natghost.PeerInfo;

public class PeerListener extends Thread {
    public static Map<String, PeerInfo> PeerMap = new ConcurrentHashMap<>();
    public static Map<PeerInfo, PeerInfo> MatchMap = new ConcurrentHashMap<>();
    
    public static final Set<String> HostSet     = ConcurrentHashMap.newKeySet();
    public static final Set<String> UsernameSet = ConcurrentHashMap.newKeySet();


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

                    if (buffer.remaining() < MIN_PACKET_SIZE) {
                        System.out.println("Gelen paket çok kısa (" + buffer.remaining() + " bytes), atlanıyor.");
                        continue;
                    }

                    if (clientAddr instanceof InetSocketAddress inetAddr) {
                        try {
                        	  List<Object> parsed = LLS.parseLLSPacket(buffer.duplicate());

                              byte       signal       = (Byte)   parsed.get(0);
                              short      length       = (Short)  parsed.get(1);
                              String     sender       = (String) parsed.get(2);
                              String     target       = (String) parsed.get(3);
                              InetAddress embeddedIp   = (InetAddress) parsed.get(4);
                              int        embeddedPort = (Integer) parsed.get(5);

                              System.out.printf(
                                  "   Parsed LLS Packet → signal=0x%02X, length=%d, sender=%s, target=%s, embeddedIp=%s, embeddedPort=%d%n",
                                  signal, length, sender, target,
                                  embeddedIp.getHostAddress(), embeddedPort
                              );

                              String hostKey = inetAddr.getHostString() + ":" + inetAddr.getPort();
                              HostSet.add(hostKey);
                              UsernameSet.add(sender);
                              System.out.println("   HostSet: " + HostSet);
                              System.out.println("   UsernameSet: " + UsernameSet);

                              PeerInfo peer = new PeerInfo(
                                  sender, target, signal,
                                  inetAddr.getAddress(),
                                  inetAddr.getPort()
                              );
                              PeerMap.put(sender, peer);
                              System.out.println("   PeerMap updated: " + sender + " → " + peer);

                              PeerInfo targetInfo = PeerMap.get(target);
                              if (targetInfo != null && targetInfo.Target.equals(sender)) {
                                  ByteBuffer toHost = LLS.New_LLS_Packet(
                                      targetInfo.signal,
                                      targetInfo.Host,
                                      targetInfo.Target,
                                      targetInfo.PublicInfo,
                                      targetInfo.Port
                                  );
                                  channel.send(
                                      toHost,
                                      new InetSocketAddress(peer.PublicInfo, peer.Port)
                                  );

                                  ByteBuffer toTarget = LLS.New_LLS_Packet(
                                      signal,
                                      sender,
                                      target,
                                      inetAddr.getAddress(),
                                      inetAddr.getPort()
                                  );
                                  channel.send(
                                      toTarget,
                                      new InetSocketAddress(
                                          targetInfo.PublicInfo,
                                          targetInfo.Port
                                      )
                                  );

                                  PeerMap.remove(sender);
                                  PeerMap.remove(target);
                              }

                          } catch (Exception e) {
                              System.err.println("   Paket parse hatası: " + e.getMessage());
                          }
                      }
                  }
              }
          } catch (Exception ex) {
              System.err.println("Datagram Channel ERROR: " + ex);
          }
      }
  }