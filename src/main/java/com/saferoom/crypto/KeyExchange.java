package com.saferoom.crypto;

import com.saferoom.crypto.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.saferoom.client.*;
import com.saferoom.relay.*;
import com.saferoom.server.*;

public class KeyExchange {
	
	  public static KeyPair keypair;
	  public static PublicKey publicKey;
	  public static PrivateKey privateKey;
	  public static SecretKey AES_Key;
	  
	    public static void init() {
	        try {
	            KeyPair keyPair = CryptoUtils.generatorRSAkeyPair();
				keypair = keyPair;
	            publicKey = keyPair.getPublic();
	            privateKey = keyPair.getPrivate();
	            System.out.println("[KEYSTORE] RSA KeyPair oluşturuldu.");
	            System.out.println("[KEYSTORE] Public Key (Base64): " + java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded()));
	        } catch (Exception e) {
	            System.err.println("[KEYSTORE] RSA anahtarları oluşturulamadı: " + e.getMessage());
	        }
	    }
	    public static void create_AES() {
	    	try {
	    		SecretKey aes_key = CryptoUtils.generatorAESkey();
	    		AES_Key = aes_key;
	    		System.out.println("[KEYSTORE] AES Key (Base64): " + java.util.Base64.getEncoder().encodeToString(AES_Key.getEncoded()));
	    		
	    	}
	    	catch(Exception e) {
	    		System.err.println("[KRYSTORE| AES anahtarı oluşturulamadı: " + e.getMessage());
	    	}
	    	
	    }
	    

	

}
