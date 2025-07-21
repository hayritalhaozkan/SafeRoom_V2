package com.saferoom.natghost;

import com.saferoom.client.ClientMenu;
import com.saferoom.server.SafeRoomServer;

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
import java.util.Objects;
import java.util.Set;

public class NatAnalyzer {
	public static List<Integer> Public_PortList = new ArrayList<>();
	public static String myPublicIP; 
	public static byte signal;
	
	private static final SecureRandom RNG = new SecureRandom();
	

	public static String[][] stunServers = {
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
	
	public static SocketAddress serversocket = new InetSocketAddress(ClientMenu.Server,ClientMenu.UDP_Port);	
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
	
	public static <T> boolean allEqual(List<T> list) {
	    if (list.isEmpty()) return true;
	    T first = list.get(0);
	    for (int i = 1; i < list.size(); i++) {
	        if (!Objects.equals(first, list.get(i))) return false;
	    }
	    return true;
	}

	
	
	public static byte analyzer(String[][] stun_servers) throws IOException {
	    Selector selector = Selector.open();
	    DatagramChannel channel = DatagramChannel.open();
	    channel.configureBlocking(false);
	    channel.bind(new InetSocketAddress(0));
	    channel.register(selector, SelectionKey.OP_READ);

	    for (String[] stun : stun_servers) {
	        String stun_server = stun[0];
	        int stun_port = Integer.parseInt(stun[1]);
	        SocketAddress sockaddr = new InetSocketAddress(stun_server, stun_port);

	        ByteBuffer packet = Stun_Packet();
	        channel.send(packet, sockaddr);
	    }

	    long deadline = System.nanoTime() + 100_000_000;
	    while (System.nanoTime() < deadline) {
	        if (selector.selectNow() == 0) continue;

	        Set<SelectionKey> keys = selector.selectedKeys();
	        Iterator<SelectionKey> iter = keys.iterator();
	        while (iter.hasNext()) {
	            SelectionKey key = iter.next();
	            iter.remove();

	            DatagramChannel ch = (DatagramChannel) key.channel();
	            ByteBuffer recv_pack = ByteBuffer.allocate(512);
	            ch.receive(recv_pack);
	            recv_pack.flip();
	            parseStunResponse(recv_pack, Public_PortList);
	        }
	    }

	    if (Public_PortList.size() >= 2) {
	        if (allEqual(Public_PortList)) {
	            signal = 0x00;
	            System.out.println("NAT Type: NOT Symmetric");
	        } else {
	            signal = 0x11;
	            System.out.println("NAT Type: Symmetric");
	        }
	    } else {
	        System.out.println("Unknown Host Exception");
	        signal = (byte) 0xFE; // Optional: Unknown/Timeout
	    }

	    return signal;
	}

	
	public static void multiplexer(InetSocketAddress Server_Address, int hole_count) throws IOException {
	    List<DatagramChannel> channels = new ArrayList<>();
	    Selector selector = Selector.open();

	    byte signal = analyzer(stunServers);
	    ByteBuffer packet = LLS.New_Multiplex_Packet(signal, ClientMenu.myUsername, ClientMenu.target_username);

	    for (int i = 0; i < hole_count; i++) {
	        DatagramChannel ch = DatagramChannel.open();
	        ch.configureBlocking(false);
	        ch.bind(new InetSocketAddress(0));
	        ch.connect(Server_Address);
	        ch.register(selector, SelectionKey.OP_READ);
	        ch.write(packet.duplicate()); 
	        channels.add(ch);
	    }

	    long deadline = System.nanoTime() + 100_000_000; // 100ms

	    while (System.nanoTime() < deadline) {
	        if (selector.selectNow() == 0) continue;

	        Set<SelectionKey> keys = selector.selectedKeys();
	        Iterator<SelectionKey> iter = keys.iterator();
	        while (iter.hasNext()) {
	            SelectionKey key = iter.next();
	            iter.remove();

	            if (key.isReadable()) {
	                DatagramChannel ch = (DatagramChannel) key.channel();
	                ByteBuffer buf = ByteBuffer.allocate(512);
	                SocketAddress from = ch.receive(buf);
	                if (from != null) {
	                    buf.flip();
	                    byte[] data = new byte[buf.remaining()];
	                    buf.get(data);
	                    System.out.println("Answer From Server: " + new String(data));
	                }
	            }
	        }
	    }

	    for (DatagramChannel ch : channels) ch.close();
	    selector.close();
	}

	
	public static void main(String[] args) {
		InetSocketAddress server_addr = new InetSocketAddress(SafeRoomServer.ServerIP, SafeRoomServer.udpPort1);
		try {
			multiplexer(server_addr, 4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
	
		
		

