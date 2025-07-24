package com.saferoom.natghost;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class LLS {
	private static void putFixedString(ByteBuffer buf, String str, int len) {
	    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
	    if (bytes.length > len) {
	        buf.put(bytes, 0, len);
	    } else {
	        buf.put(bytes);
	        for (int i = bytes.length; i < len; i++) buf.put((byte) 0);
	    }
	}
	private static String getFixedString(ByteBuffer buf, int len) {
	    byte[] bytes = new byte[len];
	    buf.get(bytes);
	    int realLen = 0;
	    while (realLen < len && bytes[realLen] != 0) realLen++;
	    return new String(bytes, 0, realLen, StandardCharsets.UTF_8);
	}
	
	public static ByteBuffer GatePack() {
		ByteBuffer buffer = ByteBuffer.allocate(25);
		buffer.put((byte) 0x18);
		putFixedString(buffer, "Hello, Sexter Morgan", 20);
		buffer.flip();
		return buffer;
	}


	 public static ByteBuffer New_LLS_Packet(byte signal, String username, String target, InetAddress publicIp, int port) {
	        ByteBuffer buffer = ByteBuffer.allocate(51);
	        buffer.put(signal);
	        buffer.putShort((short) buffer.capacity());
	        putFixedString(buffer, username, 20);
	        putFixedString(buffer, target, 20);
	        buffer.put(publicIp.getAddress());
	        buffer.putInt(port);
	        buffer.flip();
	        return buffer;
	    }

	public static ByteBuffer New_Multiplex_Packet(byte signal, String username, String target) {
		ByteBuffer packet = ByteBuffer.allocate(43);
		
		packet.put(signal);
		packet.putShort((short) packet.capacity());
		
		putFixedString(packet, username, 20);
		putFixedString(packet, target, 20);
		
		packet.flip();
		
		return packet;
	}
	 public static List<Object> parseMultiple_Packet(ByteBuffer buffer) {
	        final int MIN_LENGTH = 1 + 2 + 20 + 20;
	        if (buffer.remaining() < MIN_LENGTH) {
	            throw new IllegalArgumentException("Packet too short for LLS multiplex: remaining=" + buffer.remaining());
	        }
	        List<Object> parsed = new ArrayList<>(4);
	        byte type = buffer.get();
	        short len = buffer.getShort();
	        parsed.add(type);
	        parsed.add(len);
	        String sender = getFixedString(buffer, 20);
	        String target = getFixedString(buffer, 20);
	        parsed.add(sender);
	        parsed.add(target);
	        return parsed;
	    }
			
	public static List<Object> parseLLSPacket(ByteBuffer buffer) throws UnknownHostException {
	    List<Object> parsed = new ArrayList<>(7); 

	    byte type = buffer.get();
	    short len = buffer.getShort();
	    parsed.add(type); // packet.get(0)-->Message Type
	    parsed.add(len);  //packet.get(1)-->Message Length

	    String sender = getFixedString(buffer, 20);
	    String target = getFixedString(buffer, 20);
	    parsed.add(sender); //packet.get(2)-->Sender
	    parsed.add(target); //packet.get(3)-->Target

	    if (buffer.remaining() < 4 + 1)
	        throw new IllegalArgumentException("Packet too short for IP and port count");
	    
	    byte[] ipBytes = new byte[4];
	    buffer.get(ipBytes);
	    InetAddress ip = InetAddress.getByAddress(ipBytes);
	    parsed.add(ip); //packet.get(4)-->Public IP

	    int port = buffer.getInt();
	    parsed.add(port); //packet.get(5)-->Public Port
	    

	    return parsed;
	}


}
