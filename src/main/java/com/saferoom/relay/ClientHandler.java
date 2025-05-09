package com.saferoom.relay;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
	
	private final Socket clientSocket;
	
	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
	}
	
	@Override
	public void run() {

		try (
			InputStream input = clientSocket.getInputStream();
			OutputStream out = clientSocket.getOutputStream()){
			
			byte[] buffer = new byte[1024];
			int bytesRead = input.read(buffer);
			while((bytesRead != -1)) {
				String message = new String(buffer, 0, bytesRead);
				System.out.println("[RELAY] Veri alındı: " + message);
				
				String response = "[Echoed by Relay " + message;
				byte[] b_response = response.getBytes("UTF-8");
				
				out.write(b_response);
				out.flush();
			}
	          System.out.println("🔌 Client bağlantısı kapandı: " + clientSocket.getInetAddress());
	        } catch (IOException e) {
	            System.err.println("❌ Client işlem hatası: " + e.getMessage());
	        } finally {
	            try {
	                clientSocket.close();
	            } catch (IOException e) {
	                System.err.println("⚠️ Socket kapatılamadı: " + e.getMessage());
	            }
	        }
	    }
		
	}

