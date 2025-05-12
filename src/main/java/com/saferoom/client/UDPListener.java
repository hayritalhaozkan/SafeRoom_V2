package com.saferoom.client;

import java.net.*;

import com.saferoom.grpc.SafeRoomProto;
import com.saferoom.grpc.UDPHoleGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.*;

public class UDPListener implements Runnable{
	
	static int port = 0;
	
	
	private String username;  // Bu peer'ın adı
	
	public UDPListener(int port, String username) {
	    UDPListener.port = port;
	    this.username = username;
	}


	
	
	@Override
	public  void run() {
		
		try(DatagramSocket listener = new DatagramSocket(port)){
			
            System.out.println(" UDP Listener aktif: " + port);
			
			byte[] buf = new byte[1024];
			
			DatagramPacket packet = new DatagramPacket( buf, buf.length);
			
			while(true) {
				listener.receive(packet);
				String message = new String(packet.getData(), 0, packet.getLength());
				System.out.println("[UDP]  Mesaj alındı → " + message +
	                    " from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
				
				if (message.startsWith("HOLE_PUNCH:")) {
		                String senderUsername = message.split(":")[1];
		                String senderIP = packet.getAddress().getHostAddress();
		                int senderPort = packet.getPort();

		                System.out.println("📨 HOLE_PUNCH alındı → " + senderUsername + " from " + senderIP + ":" + senderPort);

		                // → Handshake çağrısı
		                callGrpcHandshake(senderUsername);
				 }
			}
			
		}
		catch(Exception e) {
			System.out.println("Hata mesajı: " + e);
			System.exit(0);
		}
	
	}
	
	private void callGrpcHandshake(String senderUsername) {
	    try {
	        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
	                .usePlaintext()
	                .build();

	        UDPHoleGrpc.UDPHoleBlockingStub stub = UDPHoleGrpc.newBlockingStub(channel);

	        SafeRoomProto.HandshakeConfirm handshake = SafeRoomProto.HandshakeConfirm.newBuilder()
	                .setClientId(senderUsername)
	                .setTargetId(this.username) // listener'a eklenecek kendi username'i
	                .setTimestamp(System.currentTimeMillis())
	                .build();

	        SafeRoomProto.Status response = stub.handShake(handshake);
	        System.out.println("🤝 Handshake sonucu: " + response.getMessage());

	        channel.shutdown();
	    } catch (Exception e) {
	        System.out.println("Handshake GRPC çağrısı başarısız: " + e.getMessage());
	    }
	}

	
	
}
