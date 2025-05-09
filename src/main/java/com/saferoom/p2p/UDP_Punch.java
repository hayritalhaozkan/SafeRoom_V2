package com.saferoom.p2p;

import com.saferoom.db.DBManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP_Punch {

    private static final String MESSAGE = "HOLE_PUNCH";
    private static final int TIMEOUT_MS = 2000;

    private static final int PORT_START = 45000;
    private static final int PORT_END = 45010;

    public static boolean sendPunch(String me, String target) {
        try {
            if (!DBManager.setSTUN_INFO(me)) {
                System.out.println("[HATA] STUN bilgisi ayarlanamadı.");
                return false;
            }

            String[] targetInfo = DBManager.getSTUN_INFO(target);
            if (targetInfo[0] == null || targetInfo[1] == null) {
                System.out.println("[HATA] Hedef kullanıcının STUN bilgisi eksik.");
                return false;
            }

            String targetIP = targetInfo[0];
            int targetPort = Integer.parseInt(targetInfo[1]);

            InetAddress address = InetAddress.getByName(targetIP);
            byte[] msgBytes = MESSAGE.getBytes("UTF-8");

            for (int myPort = PORT_START; myPort <= PORT_END; myPort++) {
                try (DatagramSocket socket = new DatagramSocket(myPort)) {
                    socket.setSoTimeout(TIMEOUT_MS);
                    socket.setReuseAddress(true);

                    DatagramPacket packet = new DatagramPacket(
                            msgBytes,
                            msgBytes.length,
                            address,
                            targetPort
                    );

                    socket.send(packet);
                    System.out.println("[SEND] Port " + myPort + " → " + targetIP + ":" + targetPort);


                } catch (Exception e) {
                    System.out.println("[WARN] Port " + myPort + " kullanılamadı: " + e.getMessage());
                }

                Thread.sleep(500);
            }

            System.out.println("[INFO] Hole punching denemeleri tamamlandı.");
            return true;

        } catch (Exception e) {
            System.out.println("[HATA] " + e.getMessage());
            return false;
        }
    }
    
    public static boolean getPunch(String me, String target) {
    	try {
            if (!DBManager.setSTUN_INFO(me)) {
                System.out.println("[HATA] STUN bilgisi ayarlanamadı.");
                return false;
            }

            String[] targetInfo = DBManager.getSTUN_INFO(target);
            if (targetInfo[0] == null || targetInfo[1] == null) {
                System.out.println("[HATA] Hedef kullanıcının STUN bilgisi eksik.");
                return false;
            }

            String targetIP = targetInfo[0];
            int targetPort = Integer.parseInt(targetInfo[1]);

            InetAddress address = InetAddress.getByName(targetIP);
            byte[] msgBytes = MESSAGE.getBytes("UTF-8");

    
    	
    } catch (Exception e) {
        System.out.println("[HATA] " + e.getMessage());
        return false;
    }
    	return false;
    
}
}
