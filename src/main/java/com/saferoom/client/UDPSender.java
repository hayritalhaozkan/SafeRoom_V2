package com.saferoom.client;

import java.io.*;
import java.net.*;

public class UDPSender {
	
	private static final int TIMEOUT_MS = 2000;
	
	public static void sendPunch(String myUsername, String targetUsername,
	                             String myIp, String targetIp,
	                             int myPort, int targetPort,
	                             String messageContent) {
				
		try {
			InetAddress targetAddress =  InetAddress.getByName(targetIp);
			byte[] msg = messageContent.getBytes("UTF-8");
			
			for (int Port = myPort; Port <= myPort + 5; Port++) {
				
				try (DatagramSocket socket = new DatagramSocket(Port)) {
					socket.setSoTimeout(TIMEOUT_MS);
					socket.setReuseAddress(true);
					
					DatagramPacket packet = new DatagramPacket(msg, msg.length, targetAddress, targetPort);
					
					socket.send(packet);
					System.out.println("[SEND] " + myUsername + " → " + targetUsername +
					                   " @ " + targetIp + ":" + targetPort +
					                   " [LocalPort: " + Port + "] Content: " + messageContent);
					
				} catch (Exception e) {
					System.out.println("[ERROR] Port " + Port + " ile gönderim başarısız: " + e.getMessage());
				}
				
				Thread.sleep(250);
			}
		}
		catch (Exception e) {
			System.out.println("[FATAL] UDP Punch gönderiminde hata: " + e.getMessage());
		}
	}
}
