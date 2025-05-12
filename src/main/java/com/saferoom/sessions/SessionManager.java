package com.saferoom.sessions;

import javax.crypto.SecretKey;

import com.saferoom.grpc.SafeRoomProto.Stun_Info;

import java.security.KeyPair;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Stun_Info> peerMap = new ConcurrentHashMap<>();


    public static void register(String clientA, String clientB, SecretKey aesKey, KeyPair rsaKeyPair) {
        SessionInfo session = new SessionInfo(clientA, clientB, aesKey, rsaKeyPair);
        sessions.put(clientA, session);
    }

    public static SessionInfo get(String clientA) {
        return sessions.get(clientA);
    }

    public static boolean has(String clientA) {
        return sessions.containsKey(clientA);
    }

    public static void remove(String clientA) {
        sessions.remove(clientA);
    }

    public static void setHandshakeDone(String clientA, boolean status) {
        if (sessions.containsKey(clientA)) {
            sessions.get(clientA).setHandshakeDone(status);
        }
    }

    public static void updateAESKey(String clientA, SecretKey key) {
        if (sessions.containsKey(clientA)) {
            sessions.get(clientA).setAesKey(key);
        }
    }

    public static void updateRSAKey(String clientA, KeyPair keyPair) {
        if (sessions.containsKey(clientA)) {
            sessions.get(clientA).setRsaKeyPair(keyPair);
        }
    }
    
    public static void registerPeer(String username, Stun_Info info) {
        peerMap.put(username, info);
    }

    public static Stun_Info getPeer(String username) {
        return peerMap.get(username);
    }

    public static boolean hasPeer(String username) {
        return peerMap.containsKey(username);
    }

    public static void removePeer(String username) {
        peerMap.remove(username);
    }

    public static Map<String, Stun_Info> getAllPeers() {
        return peerMap;
    }

}