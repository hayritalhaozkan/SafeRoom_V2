package com.saferoom.sessions;

import javax.crypto.SecretKey;

import com.saferoom.grpc.SafeRoomProto.Stun_Info;

import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionInfo {
	private final String Session_ID;
    private final String clientA;
    private final String clientB;
    private SecretKey aesKey;
    private String rsaKeyPair;
    private long timestamp;
    private boolean handshakeDone;

    public SessionInfo(String Session_ID,String clientA, String clientB) {
    	this.Session_ID = Session_ID;
        this.clientA = clientA;
        this.clientB = clientB;
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

    public String getRsaKeyPair() {
        return rsaKeyPair;
    }

    public void setRsaKeyPair(String keyPair) {
        this.rsaKeyPair = keyPair;
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
