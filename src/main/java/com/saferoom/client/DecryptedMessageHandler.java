package com.saferoom.client;

import com.saferoom.crypto.CryptoUtils;
import com.saferoom.grpc.*;
import com.saferoom.sessions.SessionInfo;
import com.saferoom.sessions.SessionManager;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
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

        // AES anahtarını al
        SessionInfo session = SessionManager.get(senderUsername);
        if (session == null || session.getAesKey() == null) {
            System.out.println("🚨 AES key bulunamadı. Şifre çözüm yapılamıyor.");
            return;
        }

        SecretKey aesKey = session.getAesKey();

        String plaintext = "";

        try {
            byte[] decodedData = Base64.getDecoder().decode(base64Payload);
            byte[] iv = new byte[16];
            byte[] ciphertext = new byte[decodedData.length - 16];

            System.arraycopy(decodedData, 0, iv, 0, 16);
            System.arraycopy(decodedData, 16, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(ciphertext);

            plaintext = new String(decrypted, StandardCharsets.UTF_8);

            System.out.println("✅ Çözülen mesaj: " + plaintext);
        } catch (Exception e) {
            System.out.println("❌ Decryption failed: " + e.getMessage());
        }
    }
}
