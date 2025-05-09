package com.saferoom.grpc;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Base64;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.saferoom.crypto.CryptoUtils;
import com.saferoom.crypto.KeyExchange;
import com.saferoom.grpc.SafeRoomProto.FromTo;
import com.saferoom.grpc.SafeRoomProto.HandshakeConfirm;
import com.saferoom.grpc.SafeRoomProto.Request_Client;
import com.saferoom.grpc.SafeRoomProto.Status;
import com.saferoom.grpc.SafeRoomProto.Stun_Info;
import com.saferoom.server.MessageForwarder;
import com.saferoom.grpc.SafeRoomProto.DecryptedPacket;
import com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage;
import com.saferoom.grpc.SafeRoomProto.EncryptedPacket;


import io.grpc.stub.StreamObserver;

public class UDPHoleImpl extends UDPHoleGrpc.UDPHoleImplBase {

	private static final Map<String, SafeRoomProto.Stun_Info> peerMap = new ConcurrentHashMap<>();
	private static final Map<String, SecretKey> sessionKeys = new ConcurrentHashMap<>();
	private final Object lock = new Object();

	
	@Override
	public void registerClient(Stun_Info request, StreamObserver<Status> responseObserver){
		
		synchronized(lock) {
		peerMap.put(request.getUsername(), request);
		lock.notifyAll();
		}
		SafeRoomProto.Status response = SafeRoomProto.Status.newBuilder()
				.setMessage("Client Registered")
				.setCode(0)
				.build();
		
		responseObserver.onNext(response);
		responseObserver.onCompleted();
			
	}

	@Override
	public void getStunInfo(Request_Client request, StreamObserver<Stun_Info> responseObserver) {
		
		synchronized(lock) {
			while(!peerMap.containsKey(request.getUsername())){
				try {
					lock.wait(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		SafeRoomProto.Stun_Info peerInfo = peerMap.get(request.getUsername());
		
		if(peerInfo != null) {
			responseObserver.onNext(peerInfo);
		}
		else {
			responseObserver.onNext(SafeRoomProto.Stun_Info.newBuilder()
					.setUsername(request.getUsername())
					.setState(false)
					.build());
		}
	    responseObserver.onCompleted();
	    System.out.println("[OKEY]");

	}

	@Override
	public void punchTest(FromTo request, StreamObserver<Status> responseObserver) {
		// TODO Auto-generated method stub
		  String me = request.getMe();
		    String them = request.getThem();

		    SafeRoomProto.Stun_Info targetInfo = peerMap.get(them);

		    SafeRoomProto.Status.Builder responseBuilder = SafeRoomProto.Status.newBuilder();

		    if (targetInfo != null) {
		        responseBuilder.setMessage("Target peer found. Ready to punch.");
		        responseBuilder.setCode(0); // OK
		    } else {
		        responseBuilder.setMessage("Target peer not found.");
		        responseBuilder.setCode(1); // NOT_FOUND
		    }

		    responseObserver.onNext(responseBuilder.build());
		    responseObserver.onCompleted();
	}
	
	@Override
	public void handShake(SafeRoomProto.HandshakeConfirm request, StreamObserver<SafeRoomProto.Status> responseObserver) {
	    String client = request.getClientId();
	    String target = request.getTargetId();
	    long time = request.getTimestamp();

	    System.out.println("[HANDSHAKE] " + client + " ↔ " + target + " @ " + time);

	    // (İleride buraya log database işlemleri eklenebilir)

	    SafeRoomProto.Status response = SafeRoomProto.Status.newBuilder()
	        .setMessage("Handshake logged successfully.")
	        .setCode(0)
	        .build();

	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}	    // (İleride buraya log database işlemleri eklenebilir)


	@Override
	public void heartBeat(SafeRoomProto.Stun_Info request, StreamObserver<SafeRoomProto.Status> responseObserver) {
	    String username = request.getUsername();

	    if (peerMap.containsKey(username)) {
	        // İsteğe bağlı: burada istenirse lastSeen gibi bir yapı tutulabilir
	        System.out.println("[HEARTBEAT] Aktif: " + username);

	        SafeRoomProto.Status status = SafeRoomProto.Status.newBuilder()
	            .setMessage("Peer is active.")
	            .setCode(0)
	            .build();
	        responseObserver.onNext(status);
	    } else {
	        System.out.println("[HEARTBEAT] Peer not found: " + username);

	        SafeRoomProto.Status status = SafeRoomProto.Status.newBuilder()
	            .setMessage("Peer not found.")
	            .setCode(1)
	            .build();
	        responseObserver.onNext(status);
	    }

	    responseObserver.onCompleted();
	}


	@Override
	public void finish(SafeRoomProto.Request_Client request, StreamObserver<SafeRoomProto.Status> responseObserver) {
	    String username = request.getUsername();
	    SafeRoomProto.Stun_Info removed = peerMap.remove(username);

	    if (removed != null) {
	        System.out.println("[FINISH] Peer removed: " + username);

	        SafeRoomProto.Status status = SafeRoomProto.Status.newBuilder()
	            .setMessage("Peer successfully removed.")
	            .setCode(0)
	            .build();
	        responseObserver.onNext(status);
	    } else {
	        System.out.println("[FINISH] Peer not found for removal: " + username);

	        SafeRoomProto.Status status = SafeRoomProto.Status.newBuilder()
	            .setMessage("Peer not found.")
	            .setCode(1)
	            .build();
	        responseObserver.onNext(status);
	    }

	    responseObserver.onCompleted();
	}
	@Override
	public void getServerPublicKey(SafeRoomProto.Empty request, StreamObserver<SafeRoomProto.PublicKeyMessage> responseObserver) {
	    byte[] rsa_pub = KeyExchange.publicKey.getEncoded();
	    String publicKeyBase64 = Base64.getEncoder().encodeToString(rsa_pub);

	    SafeRoomProto.PublicKeyMessage response = SafeRoomProto.PublicKeyMessage.newBuilder()
	        .setBase64Key(publicKeyBase64)
	        .build();

	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}
	

@Override
public void sendEncryptedAESKey(SafeRoomProto.EncryptedAESKeyMessage request, StreamObserver<SafeRoomProto.Status> responseObserver) {
    try {
        String encryptedBase64 = request.getEncryptedKey();

        // Decode base64 → byte[]
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        // RSA ile çöz (RSA Private Key: sadece server bilir)
        SecretKey aesKey = CryptoUtils.decrypt_AESkey(encryptedBase64, KeyExchange.privateKey);

        // (Opsiyonel) AES key’i RAM’e koy: client_id kullanabilirdik ama elimizde yok
         sessionKeys.put("client_username", aesKey); 

        // Başarılı cevap
        sessionKeys.put(request.getClientId(), aesKey);

        SafeRoomProto.Status status = SafeRoomProto.Status.newBuilder()
                .setMessage("AES key başarıyla çözüldü.")
                .setCode(0)
                .build();

        responseObserver.onNext(status);
        responseObserver.onCompleted();

    } catch (Exception e) {
        SafeRoomProto.Status status = SafeRoomProto.Status.newBuilder()
                .setMessage("AES çözümleme başarısız: " + e.getMessage())
                .setCode(2)
                .build();

        responseObserver.onNext(status);
        responseObserver.onCompleted();
    }
}

@Override
public void sendEncryptedMessage(SafeRoomProto.EncryptedPacket request, StreamObserver<SafeRoomProto.Status> responseObserver) {
    String from = request.getSender();
    String to = request.getReceiver();
    String base64EncryptedData = request.getPayload();

    System.out.println("📩 Mesaj alındı: " + from + " → " + to);

    // 🔁 Yönlendirici nesnesini oluştur (RAM içindeki eşleştirmeyi kullanır)
    MessageForwarder forwarder = new MessageForwarder(peerMap);
    boolean success = forwarder.forwardToPeer(request);

    // 🔄 Geri bildirim mesajı
    SafeRoomProto.Status.Builder response = SafeRoomProto.Status.newBuilder();
    if (success) {
        response.setMessage("Mesaj başarıyla iletildi.")
                .setCode(0);
    } else {
        response.setMessage("Mesaj iletilemedi (alıcı RAM'de yok ya da UDP hatası).")
                .setCode(2);
    }

    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
}


@Override
public void decryptedMessage(SafeRoomProto.EncryptedPacket request, StreamObserver<DecryptedPacket> responseObserver){
	String sender = request.getSender();
	String to_who = request.getReceiver();
    String base64EncryptedData = request.getPayload(); 
   
    SecretKey aes_key = sessionKeys.get(sender);
    
    String plaintext = "";
    
try {
 // decode edilmiş Base64 veriyi çöz
    byte[] decoded_data = Base64.getDecoder().decode(base64EncryptedData);
    byte[] iv_part = new byte[16];
    byte[] raw_data = new byte[decoded_data.length - 16];

    // IV ve veri ayrımı
    System.arraycopy(decoded_data, 0, iv_part, 0, 16);
    System.arraycopy(decoded_data, 16, raw_data, 0, raw_data.length);

    
    IvParameterSpec iv = new IvParameterSpec(iv_part);
    
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, aes_key, iv);
    byte[] decrypted_data = cipher.doFinal(raw_data);
   
    plaintext = new String(decrypted_data, StandardCharsets.UTF_8);
}
catch(Exception e ) {
	System.err.println("ERROR: " + e.getMessage());
}
    DecryptedPacket response = SafeRoomProto.DecryptedPacket.newBuilder()
			.setSendedBy(sender)
			.setRecvedBy(to_who)
			.setPlaintext(plaintext)
			.build();
    
    responseObserver.onNext(response);
    responseObserver.onCompleted();



}

}
