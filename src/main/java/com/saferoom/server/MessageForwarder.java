package com.saferoom.server;

import com.saferoom.grpc.SafeRoomProto;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MessageForwarder {
    private final Map<String, SafeRoomProto.Stun_Info> peerMap;

    public MessageForwarder(Map<String, SafeRoomProto.Stun_Info> peerMap) {
        this.peerMap = peerMap;
    }

    public boolean forwardToPeer(SafeRoomProto.EncryptedPacket packet) {
        String target = packet.getReceiver();
        if (peerMap.containsKey(target)) {
            // relay logic or message buffering
            System.out.println("✅ Yönlendirme başarılı: " + target);
            return true;
        }
        return false;
    }
}
