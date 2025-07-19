package com.saferoom.natghost;

import com.saferoom.client.ClientMenu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NatAnalyzer {
	public static List<Integer> Public_PortList = new ArrayList<>();
	public static String myPublicIP; 
	public static ByteBuffer Symmetric = ByteBuffer.wrap("Symmetric".getBytes());
	public static Thread t1;
	public static Thread t2;
	
	private static final SecureRandom RNG = new SecureRandom();
	

	String[][] stunServers = {
		    {"stun1.l.google.com", "19302"},
		    {"stun2.l.google.com", "19302"},
		    
		    {"stun.stunprotocol.org", "3478"},
		    {"stun.ekiga.net", "3478"},
		    {"stun.schlund.de", "3478"},
		    
		    {"stun.ideasip.com", "3478"},
		    {"stun.voipgate.com", "3478"},
		    {"stun.xten.com", "3478"},
		    {"stun.antisip.com", "3478"},
		    {"stun.server.org", "3478"}  
		};
	
	public static SocketAddress serversocket = new InetSocketAddress(ClientMenu.Server,ClientMenu.Port);	
	public static ByteBuffer Stun_Packet() {
		ByteBuffer packet = ByteBuffer.allocate(20);
		packet.putShort((short) ((0x0001) & (0x3FFF)));
		packet.putShort((short) 0 );
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
	        short attrLen = buffer.getShort();

	        if (attrType == 0x0001) { 
	            buffer.get(); 
	            byte family = buffer.get(); 
	            int port = buffer.getShort() & 0xFFFF;
	            byte[] addrBytes = new byte[4];
	            buffer.get(addrBytes);

	            String ip = (addrBytes[0] & 0xFF) + "." + 
	                        (addrBytes[1] & 0xFF) + "." +
	                        (addrBytes[2] & 0xFF) + "." +
	                        (addrBytes[3] & 0xFF);

	            System.out.println("Public IP: " + ip);
	            System.out.println("Public Port: " + port);
	            myPublicIP = ip;
	            PublicPortList.add(port);
	        } else {
	            buffer.position(buffer.position() + attrLen);
	    	    System.out.println("No MAPPED-ADDRESS.");

	        }
	    }
	    return;
	}
	
	
	public static void analyzer(String[][] stun_servers) throws IOException {

			Selector selector = Selector.open();
			DatagramChannel channel = DatagramChannel.open();
 			channel.configureBlocking(false);
 			channel.register(selector, SelectionKey.OP_READ);
			channel.bind(new InetSocketAddress(0));
			for(String[]parser : stun_servers) {
				String host = parser[0];
				int host_port = Integer.parseInt(parser[1]);
				SocketAddress sockaddr = new InetSocketAddress(host, host_port);
						channel.send(Stun_Packet().duplicate(),sockaddr);
			}
						long deadline = System.nanoTime() + 100_000_000; 
						while (System.nanoTime() < deadline) {
						    if (selector.selectNow() == 0) continue;
						    
						Set<SelectionKey> keys = selector.selectedKeys();
						Iterator<SelectionKey> iter = keys.iterator();
							while(iter.hasNext()) {
								SelectionKey key = iter.next();
								iter.remove();
								
								if(key.isReadable()) {
									ByteBuffer buf = ByteBuffer.allocate(512);
									DatagramChannel dc = (DatagramChannel)key.channel();
									SocketAddress sender = dc.receive(buf);
									if(sender != null) {
										buf.flip();
										parseStunResponse(buf, Public_PortList);
									}
									
								}
							
					
				}
							if(Public_PortList.size() >= 3) {
							    boolean isSymmetric = false;
							    for (int i = 0; i < Public_PortList.size() - 1; i++) {
							        if (!Public_PortList.get(i).equals(Public_PortList.get(i + 1))) {
							            isSymmetric = true;
							            break;
							        }
							    }

							    ByteBuffer msg;
							    if (isSymmetric) {
							        msg = Symmetric.duplicate(); 
							    } else {
							        msg = ByteBuffer.wrap((myPublicIP + ":" +  Public_PortList.getFirst()).getBytes());
							    }

							    channel.send(msg, serversocket);
							}
		}
						
		}
		
	}
	
		
		

