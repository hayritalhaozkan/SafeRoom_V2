package com.saferoom.client;

import com.saferoom.crypto.CryptoUtils;
import com.saferoom.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Scanner;

public class DecryptedMessageHandler {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("🧾 Kullanıcı adınızı girin (şifre çözülecek alıcı): ");
        String myUsername = scanner.nextLine();

        System.out.print("✉️ Mesajı gönderen kullanıcı: ");
        String senderUsername = scanner.nextLine();

        System.out.print("🔐 Şifreli (Base64) mesajı girin: ");
        String base64Payload = scanner.nextLine();

        // gRPC bağlantısı
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        UDPHoleGrpc.UDPHoleBlockingStub client = UDPHoleGrpc.newBlockingStub(channel);

        // EncryptedPacket objesini oluştur
        SafeRoomProto.EncryptedPacket packet = SafeRoomProto.EncryptedPacket.newBuilder()
                .setSender(senderUsername)
                .setReceiver(myUsername)
                .setPayload(base64Payload)
                .build();

        // gRPC ile Decryption isteği gönder
        SafeRoomProto.DecryptedPacket plaintext = client.decryptedMessage(packet);

        System.out.println("✅ Çözülen mesaj: " + plaintext.getPlaintext());

        channel.shutdown();
    }
}
