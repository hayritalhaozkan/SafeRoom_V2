package com.saferoom.crypto;

import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;

public class CryptoSessionStore {
    private static final ConcurrentHashMap<String, SecretKey> keyStore = new ConcurrentHashMap<>();

    public static void store(String username, SecretKey key) {
        keyStore.put(username, key);
    }

    public static SecretKey get(String username) {
        return keyStore.get(username);
    }

    public static boolean hasKey(String username) {
        return keyStore.containsKey(username);
    }
}
