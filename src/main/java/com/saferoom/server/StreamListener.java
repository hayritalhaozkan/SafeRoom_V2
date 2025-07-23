package com.saferoom.server;

import com.saferoom.grpc.UDPHoleImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class StreamListener extends Thread{
	public static int grpcPort = 50051;

	public void run(){
		try {
		Server server = ServerBuilder.forPort(grpcPort)
				.addService(new UDPHoleImpl())
				.build()
				.start();
		
		System.out.println("GRPC Server Started on port 50051");
		server.awaitTermination();
	}catch(Exception e) {
		System.err.println("Server Builder [ERROR]: " + e);
	}
	

}
	}
