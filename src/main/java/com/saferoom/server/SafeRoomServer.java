package com.saferoom.server;

import com.saferoom.grpc.UDPHoleImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class SafeRoomServer {
	

	public static void main(String[] args) throws Exception{
		int grpcPort = 50051;
		int udpPort = 45000;
		
		//KeyExchange.init();
		
	//	String myUsername = args.length > 0 ? args[0] : "defaultUser";
//		Thread listener = new Thread(new UDPListener(grpcPort, myUsername));

//		listener.start();
		
		Server server = ServerBuilder.forPort(grpcPort)
				.addService(new UDPHoleImpl())
				.build()
				.start();
		
		
	
		System.out.println("GRPC Server Started on port 50051");
		server.awaitTermination();
	}


	}
