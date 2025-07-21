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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.saferoom.grpc.UDPHoleImpl;
import com.saferoom.natghost.LLS;
import com.saferoom.natghost.PeerInfo;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class SafeRoomServer {
	public static String ServerIP = "10.189.49.191";
	public static int grpcPort = 50051;
	public static int udpPort1 = 45000;
	public static Map<String, PeerInfo> PeerMap = new ConcurrentHashMap<>();
	public static Map<PeerInfo, PeerInfo> MatchMap = new ConcurrentHashMap<>();
	
	public static void main(String[] args) throws Exception{
		
		new Thread(() -> {
		    try {
		        Selector selector = Selector.open();
		        DatagramChannel channel = DatagramChannel.open();
		        channel.configureBlocking(false);
		        channel.bind(new InetSocketAddress(udpPort1));
		        channel.register(selector, SelectionKey.OP_READ);

		        System.out.println("UDP Listener running on port " + udpPort1);

		        while (true) {
		            if (selector.selectNow() == 0) continue;

		            Set<SelectionKey> keys = selector.selectedKeys();
		            Iterator<SelectionKey> iter = keys.iterator();
		            while (iter.hasNext()) {
		                SelectionKey key = iter.next();
		                iter.remove();

		                ByteBuffer buffer = ByteBuffer.allocate(512);
		                SocketAddress clientAddr = channel.receive(buffer);
		                buffer.flip();

		                if (clientAddr instanceof InetSocketAddress inetAddr) {
		                    try {
		                        List<Object> parsed = LLS.parseMultiple_Packet(buffer.duplicate());
		                        byte signal = (byte) parsed.get(0);
		                        String username = (String) parsed.get(2);
		                        String target = (String) parsed.get(3);
		                        InetAddress publicIp = inetAddr.getAddress();
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

		                    } catch (Exception e) {
		                        System.out.println("Parse Error: " + e);
		                    }
		                }
		            }
		        }
		    } catch (Exception e) {
		        System.err.println("UDP Listener error: " + e);
		    }
		}).start();

		
		//	KeyExchange.init();
		//	String myUsername = args.length > 0 ? args[0] : "defaultUser";
		//	Thread listener = new Thread(new UDPListener(grpcPort, myUsername));
		//	listener.start();
		
		Server server = ServerBuilder.forPort(grpcPort)
				.addService(new UDPHoleImpl())
				.build()
				.start();
		
		System.out.println("GRPC Server Started on port 50051");
		server.awaitTermination();
	}

	}
