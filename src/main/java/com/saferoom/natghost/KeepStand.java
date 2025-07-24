package com.saferoom.natghost;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class KeepStand extends Thread {
    private final InetSocketAddress addr;
    private final DatagramChannel channel;
    private final ByteBuffer pack;

    public KeepStand(InetSocketAddress addr, DatagramChannel channel) {
        this.addr = addr;
        this.channel = channel;
        this.pack = LLS.GatePack();
    }
	
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                pack.rewind();
                channel.send(pack, addr);
                Thread.sleep(15_000);
            }
        } catch (InterruptedException e) {
        } catch (IOException e) {
            System.err.println("Datagram Channel Error: " + e);
        }
    }

	
}
