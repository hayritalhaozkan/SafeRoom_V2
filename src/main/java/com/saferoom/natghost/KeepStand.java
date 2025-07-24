package com.saferoom.natghost;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class KeepStand extends Thread {

    private final InetSocketAddress addr;
    private final DatagramChannel channel;
    private final ByteBuffer keepAlive;

    // 15 saniye çok sık/az geliyorsa değiştirilebilir
    private static final long INTERVAL_MS = 15_000;

    public KeepStand(InetSocketAddress addr, DatagramChannel channel) {
        this.addr = addr;
        this.channel = channel;
        this.keepAlive = LLS.New_KeepAlive_Packet(); // <-- GatePack yerine
        setName("KeepStand-" + addr);
        setDaemon(true); // Uygulama kapanırken bloklamasın
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                keepAlive.rewind();          // Her gönderimden önce buffer başa
                channel.send(keepAlive, addr);
                Thread.sleep(INTERVAL_MS);
            }
        } catch (InterruptedException ignored) {
            // Thread interrupt ile nazikçe sonlanır
        } catch (IOException e) {
            System.err.println("[KeepStand] DatagramChannel error: " + e);
        }
    }
}
