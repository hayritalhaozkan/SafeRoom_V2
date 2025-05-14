package com.saferoom.sessions;

import javax.crypto.SecretKey;

import com.saferoom.grpc.SafeRoomProto.Stun_Info;

import java.security.KeyPair;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Stun_Info> peerMap = new ConcurrentHashMap<>();


    public static void register(String Session_ID, String clientA, String clientB) {
        SessionInfo session = new SessionInfo(Session_ID,clientA, clientB);
        sessions.put(Session_ID, session);
    }

    public static SessionInfo get(String Session_ID) {
        return sessions.get(Session_ID);
    }

    public static boolean has(String Session_ID) {
        return sessions.containsKey(Session_ID);
    }

    public static void remove(String Session_ID) {
        sessions.remove(Session_ID);
    }

    public static void setHandshakeDone(String Session_ID, boolean status) {
        if (sessions.containsKey(Session_ID)) {
            sessions.get(Session_ID).setHandshakeDone(status);
        }
    }

    public static void updateAESKey(String Session_ID, SecretKey key) {
        if (sessions.containsKey(Session_ID)) {
            sessions.get(Session_ID).setAesKey(key);
        }
    }

    public static void updateRSAKey(String Session_ID, String keyPair) {
        if (sessions.containsKey(Session_ID)) {
            sessions.get(Session_ID).setRsaKeyPair(keyPair);
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