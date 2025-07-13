package com.saferoom.client;

import com.saferoom.grpc.SafeRoomProto.Menu;
import com.saferoom.grpc.SafeRoomProto.Create_User;
import com.saferoom.grpc.SafeRoomProto.Status;

public class Client{
	public static String Server;
	public static int Port;

		public static int Login(String username, String Password,
				String server, int port)
		{
		ManagedChannel channel = ManagedChannelBuilder.forTarget(server,port)
			.setPlaintext()
			.build();

		UDPHoleGrpc.UDPHoleBlockingStub client = UDPHoleGrpc.newBlockingStub(channel);
		SafeRoomProto.Menu main_menu = SafeRoomProto.Menu.newBuilder()
			.setUsername(username)
			.setHash_password(password)
			.build();
		SafeRoomProto.Status stats = client.menuAns(main_menu);
		
		String message = stats.getMessage();
		int code = stats.getCode();
		switch(code){
			case 0:
				System.out.println("Success!");
				return 0;
			case 1:
				if(message.equals("N_REGISTER")){
					System.out.println("Not Registered");
					return 1;
				}else{
					System.out.println("Blocked User");
					return 2;
		}
		default:
				System.out.println("Message has broken");
				return 3;					
			}
		}
	public static int register_client(String username, String password, String mail)
	{
		ManagedChannel channel = ManagedChannelBuilder.forTarget(Server, Port)
			.usePlaintext()
			.build();

		UDPHoleGrpc.UDPHoleBlockingStub stub = UDPHoleGrpc.newBlockingStub(channel);
		
		SafeRoomProto.Create_User insert_obj = SafeRoomProto.Create_User.newBuilder()
			.setUsername(username)
			.setEmail(mail)
			.setPassword(password)
			.setIs_Verified(0)
			.build();
		SafeRoomProto.Status stat = stub.insertUser(insert_obj);

		int code = stat.getCode();
		switch(code){
			case 0:
				System.out.println("Success!");
				return 0;
			case 2:
				if(message.equals("VUSERNAME")){
					System.out.println("Username already taken");
					return 1;
				}else{
					System.out.println("Invalid E-mail");
					return 2;
		}
		default:
				System.out.println("Message has broken");
				return 3;					
			}
		}

	}
}
