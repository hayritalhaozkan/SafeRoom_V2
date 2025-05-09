package com.saferoom.relay;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RelayPacket {

    private final String from;
    private final String to;
    private final String source;
    private final String destination;
    private final String type;
    private final long timestamp;
    private final String sessionId;
    private final String payload; 

    public RelayPacket(String from, String to, String source, String destination,
                       String type, long timestamp, String sessionId, String payload) {
        this.from = from;
        this.to = to;
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
        this.payload = payload;
    }

    public String encode() {
        String raw =
            "[" + from + "][" + to + "][" + source + "][" + destination + "]" +
            "[" + type + "][" + timestamp + "][" + sessionId + "][" + payload + "]";
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static RelayPacket decode(String encoded) {
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        String[] parts = decoded.split("\\[|\\]");
        return new RelayPacket(
            parts[1],  // from
            parts[3],  // to
            parts[5],  // source
            parts[7],  // destination
            parts[9],  // type
            Long.parseLong(parts[11]), // timestamp
            parts[13], // sessionId
            parts[15]  // payload
        );
    }

    @Override
    public String toString() {
        return "RelayPacket{" +
            "from='" + from + '\'' +
            ", to='" + to + '\'' +
            ", source='" + source + '\'' +
            ", destination='" + destination + '\'' +
            ", type='" + type + '\'' +
            ", timestamp=" + timestamp +
            ", sessionId='" + sessionId + '\'' +
            ", payload='" + payload + '\'' +
            '}';
    }

    public String getPayloadRaw() {
        return new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);
    }
}
