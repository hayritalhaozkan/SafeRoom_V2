package com.saferoom.client;

import com.saferoom.crypto.CryptoUtils;
import com.saferoom.crypto.KeyExchange;
import com.saferoom.grpc.*;
import com.saferoom.grpc.SafeRoomProto.Status;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import com.saferoom.p2p.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;

public class SafeRoomClient {
	
	public static String[] info;
	public static SafeRoomProto.Stun_Info myInfo;
	public static SafeRoomProto.Status reg;
	public static SafeRoomProto.Request_Client request;
	public static  SafeRoomProto.Stun_Info targetInfo; 
	public static SafeRoomProto.HandshakeConfirm hs;
	public static SafeRoomProto.Status hsResult;
	public static Status rsaResult;
	public static Object lock = new Object();
	
	 public static void run(String myUsername, String targetUsername) throws Exception{

	        info = MultiStunClient.StunClient();
	        

	        if (!info[0].equals("true")) {
	            System.out.println("❌ STUN başarısız. Test durduruldu.");
	            return;
	        }
	        

	        String myIp = info[1];
	        int myPort = Integer.parseInt(info[2]);
	        boolean myState = Boolean.parseBoolean(info[3]);

	        System.out.println("🌐 STUN IP: " + myIp + ", Port: " + myPort + ", OpenAccess: " + myState);

	        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)//server
	                .usePlaintext()
	                .build();
	        

	        UDPHoleGrpc.UDPHoleBlockingStub client = UDPHoleGrpc.newBlockingStub(channel);
	        
	        myInfo = SafeRoomProto.Stun_Info.newBuilder()
	                .setUsername(myUsername)
	                .setIp(myIp)
	                .setPort(myPort)
	                .setState(myState)
	                .build();

	        reg = client.registerClient(myInfo);
	        System.out.println("📥 Register sonucu: " + reg.getMessage());

	        request = SafeRoomProto.Request_Client.newBuilder()
	                .setUsername(targetUsername)
	                .build();

	        targetInfo = client.getStunInfo(request);
	        if (targetInfo.getIp().isEmpty()) {
	            System.out.println("❌ Hedef kullanıcı RAM'de yok. Test iptal.");
	            return;
	        }

	        System.out.println("🎯 Hedef IP: " + targetInfo.getIp() + ":" + targetInfo.getPort());

	        String punchMessage = "HOLE_PUNCH:" + myUsername;
	        UDPSender.sendPunch(myUsername, targetUsername, myIp, targetInfo.getIp(), myPort, targetInfo.getPort(), punchMessage);

	        hs = SafeRoomProto.HandshakeConfirm.newBuilder()
	                .setClientId(myUsername)
	                .setTargetId(targetUsername)
	                .setTimestamp(System.currentTimeMillis())
	                .build();

	        hsResult = client.handShake(hs);
	        System.out.println("🤝 Handshake sonucu: " + hsResult.getMessage());

	        channel.shutdown();

	        System.out.println("✅ SafeRoomClient işlemi tamamlandı.");
	    }
	
	public static void Join(String me ,String wtoJoin) {
			try {
				run("localhost", wtoJoin);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		synchronized(lock) {
			KeyExchange.create_AES();
			lock.notifyAll();
		}

		String client_ID =wtoJoin+me;

		SecretKey SecretKey_AES = KeyExchange.AES_Key;
		
		String wtoJoinAddress =targetInfo.getIp()+":"+targetInfo.getPort();
		
		ManagedChannel channel = ManagedChannelBuilder.forTarget(wtoJoinAddress)
				.usePlaintext()
				.build();
		
		UDPHoleGrpc.UDPHoleBlockingStub stub = UDPHoleGrpc.newBlockingStub(channel);

		while(stub.pubKey.equals(null)){
			Thread.sleep(150);
		}

		String encryptedKey = CryptoUtils.encrypt_AESkey( SecretKey_AES, pubKey);
		
		SafeRoomProto.EncryptedAESKeyMessage encrypted_key = SafeRoomProto.EncryptedAESKeyMessage.newBuilder()
																								 .setClientId(client_ID)
																								 .setEncryptedKey(encryptedKey)
																								 .build();
		Status status = stub.sendEncryptedAESKey(encrypted_key);
		 
	 	       if(status.getCode().equals(0)){
				System.out.println("Encrypted AES Key(128-bit) successfully sended");
			   } 
			   else{
				System.err.out("ERROR HAS BEEN ACCOUR WHILE KEY SENDİNG[ERROR]");
			   }
			SessionInfo thisSession = new SessionInfo(wtoWait, me, SecretKey_AES, stub.getBase64Key);
			sessions.put(client_ID, thisSession);

		
	}
	
	public static void NewMeeting(String me, String wtoWait) {
		synchronized(lock) {
		KeyExchange.init();
		lock.notifyAll();
		}

		String client_ID = me+wtoWait;

		PublicKey Public_Key = KeyExchange.publicKey;
		PrivateKey Private_Key = KeyExchange.privateKey;

		
		String wtoClientAddress =targetInfo.getIp()+":"+targetInfo.getPort();

		
		ManagedChannel channel = ManagedChannelBuilder.forTarget(wtoClientAddress)
				.usePlaintext()
				.build();
		
		UDPHoleGrpc.UDPHoleBlockingStub stub = UDPHoleGrpc.newBlockingStub(channel);
		
		String EncodedPublicKey = Base64.getEncoder().encodeToString(Public_Key.toString().getBytes(StandardCharsets.UTF_8));

		UDPHoleImpl.SendPublicKeyRequest request = UDPHoleImpl.SendPublicKeyRequest.newBuilder()
																			.setBase64Key(EncodedPublicKey)
																			.setUsername(me)
																			.build();

		Status response = stub.SendPublicKeyRequest(request);

			if(response.getCode().equals(0)){
				System.out.println("Public Key was sent");
			}
			else{
				Syste.err.out("Error has been accour[ERROR]");
			}
			SessionInfo thisSession = new SessionInfo(me, wtoWait, stub.getAesKey, EncodedPublicKey);
			sessions.put(client_ID, thisSession);

		
	}

   
    public static void main(String[] args){
        try {
			run("root","target");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
