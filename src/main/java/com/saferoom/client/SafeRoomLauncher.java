package com.saferoom.client;

public final class SafeRoomLauncher {
    public static void startClient(String myUsername, String target) {
        new Thread(() -> {
            try {
                com.saferoom.client.SafeRoomClient.run(myUsername, target);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "SR-P2P-" + myUsername + "→" + target).start();
    }
}
