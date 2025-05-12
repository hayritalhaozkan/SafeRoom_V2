package com.saferoom.sessions;

import javax.crypto.SecretKey;

import com.saferoom.grpc.SafeRoomProto.Stun_Info;

import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionInfo {

    private final String clientA;
    private final String clientB;
    private SecretKey aesKey;
    private KeyPair rsaKeyPair;
    private long timestamp;
    private boolean handshakeDone;

    public SessionInfo(String clientA, String clientB, SecretKey aesKey, KeyPair rsaKeyPair) {
        this.clientA = clientA;
        this.clientB = clientB;
        this.aesKey = aesKey;
        this.rsaKeyPair = rsaKeyPair;
        this.timestamp = System.currentTimeMillis();
        this.handshakeDone = false;
    }

    public String getClientA() {
        return clientA;
    }

    public String getClientB() {
        return clientB;
    }

    public SecretKey getAesKey() {
        return aesKey;
    }

    public void setAesKey(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    public KeyPair getRsaKeyPair() {
        return rsaKeyPair;
    }

    public void setRsaKeyPair(KeyPair rsaKeyPair) {
        this.rsaKeyPair = rsaKeyPair;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isHandshakeDone() {
        return handshakeDone;
    }

    public void setHandshakeDone(boolean handshakeDone) {
        this.handshakeDone = handshakeDone;
    }
    
}
