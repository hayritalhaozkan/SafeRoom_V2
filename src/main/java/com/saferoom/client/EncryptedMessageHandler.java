package com.saferoom.client;

import com.saferoom.crypto.CryptoUtils;
import com.saferoom.grpc.*;
import com.saferoom.sessions.SessionManager;
import com.saferoom.sessions.SessionInfo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Scanner;

public class EncryptedMessageHandler implements Runnable {
	@Override
    public  void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("👤 Kullanıcı adınızı girin: ");
        String myUsername = scanner.nextLine();

        System.out.print("🎯 Hedef kullanıcı adını girin: ");
        String targetUsername = scanner.nextLine();

        System.out.print("💬 Göndermek istediğiniz mesaj: ");
        String message = scanner.nextLine();

        // AES anahtarını al
        SessionInfo session = SessionManager.get(myUsername);  // ✅ Yeni yapı
        if (session == null || session.getAesKey() == null) {
            System.out.println("🚨 AES anahtarı bulunamadı. Önce register + key exchange yapmalısınız.");
            return;
        }

        SecretKey aesKey = session.getAesKey();
try {
        // Mesajı AES ile şifrele
        byte[] encryptedData = CryptoUtils.encrypte_data_with_AES(message, aesKey);
        String base64Payload = Base64.getEncoder().encodeToString(encryptedData);

        // gRPC kanalı oluştur
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        UDPHoleGrpc.UDPHoleBlockingStub client = UDPHoleGrpc.newBlockingStub(channel);

        // Paket oluştur ve gönder
        SafeRoomProto.EncryptedPacket packet = SafeRoomProto.EncryptedPacket.newBuilder()
                .setSender(myUsername)
                .setReceiver(targetUsername)
                .setPayload(base64Payload)
                .build();

        SafeRoomProto.Status response = client.sendEncryptedMessage(packet);
        System.out.println("📤 Mesaj sonucu: " + response.getMessage());

        channel.shutdown();
}
catch(Exception e) {
	System.out.println("Exception: " + e);
}
    }
}
