package com.saferoom.client;

import com.saferoom.crypto.CryptoUtils;
import com.saferoom.crypto.KeyExchange;
import com.saferoom.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import com.saferoom.p2p.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;

public class SafeRoomClient {

    public static void run(String myUsername, String targetUsername) throws Exception{

        String[] info = MultiStunClient.StunClient();
        

        if (!info[0].equals("true")) {
            System.out.println("❌ STUN başarısız. Test durduruldu.");
            return;
        }
        
        SecretKey aesKey = CryptoUtils.generatorAESkey();  // AES-256 key


        String myIp = info[1];
        int myPort = Integer.parseInt(info[2]);
        boolean myState = Boolean.parseBoolean(info[3]);

        System.out.println("🌐 STUN IP: " + myIp + ", Port: " + myPort + ", OpenAccess: " + myState);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        

        UDPHoleGrpc.UDPHoleBlockingStub client = UDPHoleGrpc.newBlockingStub(channel);

        SafeRoomProto.PublicKeyMessage keyResponse = client.getServerPublicKey(SafeRoomProto.Empty.newBuilder().build());
        String base64PubKey = keyResponse.getBase64Key();
        
        byte[] decodedKey = Base64.getDecoder().decode(base64PubKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        PublicKey serverPubKey = keyFactory.generatePublic(keySpec);
        
        String encryptedAESKey = CryptoUtils.encrypt_AESkey(aesKey, serverPubKey);


        SafeRoomProto.EncryptedAESKeyMessage encrypted_aes_key = SafeRoomProto.EncryptedAESKeyMessage.newBuilder()
        			.setClientId(myUsername)
        			.setEncryptedKey(encryptedAESKey)
        			.build();
        
       SafeRoomProto.Status send_base64AES = client.sendEncryptedAESKey(encrypted_aes_key);
       
       int code = send_base64AES.getCode();

       if (code == 0) {
           System.out.println("✅ AES Key Başarıyla Gönderildi");
       } else if (code == 1) {
           System.out.println("❌ Kullanıcı bulunamadı.");
       } else if (code == 2) {
           System.out.println("🚨 AES Key gönderim hatası.");
       } else {
           System.out.println("❓ Bilinmeyen durum kodu: " + code);
       }

        
        SafeRoomProto.Stun_Info myInfo = SafeRoomProto.Stun_Info.newBuilder()
                .setUsername(myUsername)
                .setIp(myIp)
                .setPort(myPort)
                .setState(myState)
                .build();

        SafeRoomProto.Status reg = client.registerClient(myInfo);
        System.out.println("📥 Register sonucu: " + reg.getMessage());

        SafeRoomProto.Request_Client req = SafeRoomProto.Request_Client.newBuilder()
                .setUsername(targetUsername)
                .build();

        SafeRoomProto.Stun_Info targetInfo = client.getStunInfo(req);
        if (targetInfo.getIp().isEmpty()) {
            System.out.println("❌ Hedef kullanıcı RAM'de yok. Test iptal.");
            return;
        }

        System.out.println("🎯 Hedef IP: " + targetInfo.getIp() + ":" + targetInfo.getPort());

        String punchMessage = "HOLE_PUNCH:" + myUsername;
        UDPSender.sendPunch(myUsername, targetUsername, myIp, targetInfo.getIp(), myPort, targetInfo.getPort(), punchMessage);

        SafeRoomProto.HandshakeConfirm hs = SafeRoomProto.HandshakeConfirm.newBuilder()
                .setClientId(myUsername)
                .setTargetId(targetUsername)
                .setTimestamp(System.currentTimeMillis())
                .build();

        SafeRoomProto.Status hsResult = client.handShake(hs);
        System.out.println("🤝 Handshake sonucu: " + hsResult.getMessage());

        channel.shutdown();

        System.out.println("✅ SafeRoomClient işlemi tamamlandı.");
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
