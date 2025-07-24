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
        {"stun.voipgate.com", "3478"},
        {"stun.xten.com", "3478"},
        {"stun.antisip.com", "3478"},
        {"stun.server.org", "3478"}
    };

    public static SocketAddress serversocket = new InetSocketAddress(ClientMenu.Server, ClientMenu.UDP_Port);

    public static ByteBuffer Stun_Packet() {
        ByteBuffer packet = ByteBuffer.allocate(20);
        packet.putShort((short) ((0x0001) & (0x3FFF)));
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
            short attrLen = buffer.getShort();

            if (attrType == 0x0001) {
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

                System.out.println("Public IP: " + ip);
                System.out.println("Public Port: " + port);
                myPublicIP = ip;
                PublicPortList.add(port);
            } else {
                buffer.position(buffer.position() + attrLen);
                System.out.println("No MAPPED-ADDRESS.");
            }
        }
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

            // DNS kontrolü
            try {
                InetAddress.getByName(stun_server);
            } catch (UnknownHostException e) {
                System.err.println("DNS çözülemedi: " + stun_server);
                continue;
            }

            SocketAddress sockaddr = new InetSocketAddress(stun_server, stun_port);
            ByteBuffer packet = Stun_Packet().duplicate();
            channel.send(packet, sockaddr);
        }

        long deadline = System.nanoTime() + 100_000_000; // 100ms
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
            signal = allEqual(Public_PortList) ? (byte) 0x00 : (byte) 0x11;
            System.out.println("NAT Type: " + (signal == 0x00 ? "NOT Symmetric" : "Symmetric"));
        } else {
            System.out.println("STUN cevabı alınamadı veya yetersiz");
            signal = (byte) 0xFE;
        }

        return signal;
    }

    public static void multiplexer(InetSocketAddress Server_Address, int hole_count) throws IOException, InterruptedException {
        List<DatagramChannel> channels = new ArrayList<>();
        Selector selector = Selector.open();

        byte signal = analyzer(stunServers);
        if (ClientMenu.myUsername == null || ClientMenu.target_username == null) {
            throw new IllegalStateException("Kullanıcı adı ya da hedef kullanıcı atanmadı!");
        }

        ByteBuffer packet = LLS.New_Multiplex_Packet(signal, ClientMenu.myUsername, ClientMenu.target_username);
        for (int i = 0; i < hole_count; i++) {
            DatagramChannel ch = DatagramChannel.open();
            ch.configureBlocking(false);
            ch.bind(new InetSocketAddress(0));
            ch.send(packet.duplicate(),Server_Address);
            ch.register(selector, SelectionKey.OP_READ);
            channels.add(ch);
        }
    	int i = 0;


        long deadline = System.nanoTime() + 100_000_000; // 100ms

        while (System.nanoTime() < deadline) {
            if (selector.selectNow() == 0) continue;

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();
            while(true) {
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isReadable()) {
                    DatagramChannel ch = (DatagramChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(512);
                    SocketAddress from = ch.receive(buf);
                    if(from == null) continue;
                    buf.flip();
                    if(from instanceof InetSocketAddress inet_addr) {
                    	List<Object> peer_info_packet = LLS.parseLLSPacket(buf);
                    	byte message_type = (byte)peer_info_packet.get(0);
                    	short message_len = (short)peer_info_packet.get(1);
                    	
                    	String message_from = (String)peer_info_packet.get(2);
                    	String message_to = (String)peer_info_packet.get(3);
                    	
                    	InetAddress public_IP = (InetAddress)peer_info_packet.get(4);
                    	int public_Port = (int)peer_info_packet.get(5);
                    	System.out.println("[Incoming From]: " + message_from);
                    	System.out.println("[Message Type]: " + message_type);
                    	System.out.println("[Message Lenght]:" + message_len);
                    	System.out.println(message_from + " 's Public IP: " + public_IP);
                    	System.out.println(message_from + "'s Public Socket: " + public_Port);
                    	InetSocketAddress addr = new InetSocketAddress(public_IP, public_Port);
                        KeepStand runner = new KeepStand(addr, ch);
                        runner.start();
                    	
                    }
                }
            }
        }}
        for (DatagramChannel ch : channels) ch.close();
        selector.close();
    }

    public static void main(String[] args) throws InterruptedException {
        InetSocketAddress server_addr = new InetSocketAddress(SafeRoomServer.ServerIP, SafeRoomServer.udpPort1);
        try {
            multiplexer(server_addr, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
