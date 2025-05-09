package com.saferoom.client;

import com.saferoom.crypto.CryptoSessionStore;
import com.saferoom.crypto.CryptoUtils;
import com.saferoom.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Scanner;

public class EncryptedMessageHandler {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("👤 Kullanıcı adınızı girin: ");
        String myUsername = scanner.nextLine();

        System.out.print("🎯 Hedef kullanıcı adını girin: ");
        String targetUsername = scanner.nextLine();

        System.out.print("💬 Göndermek istediğiniz mesaj: ");
        String message = scanner.nextLine();

        // gRPC bağlantısı
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        UDPHoleGrpc.UDPHoleBlockingStub client = UDPHoleGrpc.newBlockingStub(channel);

        // AES anahtarını RAM'den al
        SecretKey aesKey = CryptoSessionStore.get(myUsername);
        if (aesKey == null) {
            System.out.println("🚨 AES anahtarı RAM'de bulunamadı. Önce register olup key exchange yapmalısınız.");
            return;
        }

        // AES ile şifrele
        byte[] encryptedData = CryptoUtils.encrypte_data_with_AES(message, aesKey);
        String base64Payload = Base64.getEncoder().encodeToString(encryptedData);

        // gRPC mesajı gönder
        SafeRoomProto.EncryptedPacket packet = SafeRoomProto.EncryptedPacket.newBuilder()
                .setSender(myUsername)
                .setReceiver(targetUsername)
                .setPayload(base64Payload)
                .build();

        SafeRoomProto.Status response = client.sendEncryptedMessage(packet);
        System.out.println("📤 Mesaj sonucu: " + response.getMessage());

        channel.shutdown();
    }
}
