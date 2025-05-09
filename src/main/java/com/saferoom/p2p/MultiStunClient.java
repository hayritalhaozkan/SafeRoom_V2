package com.saferoom.p2p;

import de.javawi.jstun.test.DiscoveryInfo;
import de.javawi.jstun.test.DiscoveryTest;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.PortUnreachableException;

public class MultiStunClient {
   
	
	public static String[] StunClient() {
		String[] returns = new String[4];
    	String[][] stunServers = {
    		    {"stun1.l.google.com", "19302"},
    		    {"stun2.l.google.com", "19302"},
    		    
    		    {"stun.stunprotocol.org", "3478"},
    		    {"stun.ekiga.net", "3478"},
    		    {"stun.schlund.de", "3478"},
    		    
    		    {"stun.ideasip.com", "3478"},
    		    {"stun.voipgate.com", "3478"},
    		    {"stun.xten.com", "3478"},
    		    {"stun.antisip.com", "3478"},
    		    {"stun.server.org", "3478"}  
    		};

        try {
            InetAddress localIP = InetAddress.getLocalHost();
            int localPort = 0;

            DiscoveryInfo finalInfo = null;
            boolean success = false;

            for (String[] serverData : stunServers) {
                String stunHost = serverData[0];
                int stunPort = Integer.parseInt(serverData[1]);

                System.out.println("\nSTUN deneme: " + stunHost + ":" + stunPort);

                DiscoveryTest test = new DiscoveryTest(localIP, localPort, stunHost, stunPort);

                try {
                    DiscoveryInfo info = test.test();

                    if (info.getPublicIP() != null && info.getPublicPort() != -1) {
                        finalInfo = info;
                        System.out.println("  --> Başarılı!");
                        success = true;
                        break;  
                    } else {
                        System.out.println("  Public IP = null veya Port = -1. Diğer sunucuya geçiliyor...");
                    }
                } catch (PortUnreachableException pue) {
                    System.out.println("  PortUnreachableException: Sunucuya ulaşılmıyor, diğer sunucuya geçiliyor...");
                } catch (SocketException se) {
                    System.out.println("  SocketException: " + se.getMessage());
                } catch (Exception e) {
                    System.out.println("  Hata: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            }

            if (success && finalInfo != null) {
         returns[0] = "true";    //   System.out.println("\n==== STUN Başarılı Yanıt ====");
          returns[1] = finalInfo.getPublicIP().getHostAddress(); //  System.out.println("Public IP   : " + finalInfo.getPublicIP().getHostAddress());
            returns[2] =   Integer.toString(finalInfo.getPublicPort()); // System.out.println("Public Port : " + finalInfo.getPublicPort());
               returns[3] = Boolean.toString(finalInfo.isOpenAccess()); //System.out.println("OpenAccess? : " + finalInfo.isOpenAccess());
            	
            } else {
            	returns[0] = "false";

            	System.out.println("\nMaalesef hiçbir STUN sunucusundan geçerli yanıt alınamadı.");
                System.out.println("Lütfen NAT/Firewall ayarlarını kontrol edin veya UDP engelini kaldırın.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
		return returns;
    }
}
