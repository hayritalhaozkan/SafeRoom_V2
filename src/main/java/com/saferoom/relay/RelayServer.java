package com.saferoom.relay;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class RelayServer {

    private static final int PORT = 60000;
    private static final int THREAD_POOL_SIZE = 10;

    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public RelayServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start() {
        System.out.println("RelayServer listening on port " + PORT);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientHandler(clientSocket));
            } catch (IOException e) {
                System.err.println("[ERROR] Client connection error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            RelayServer server = new RelayServer(PORT);
            server.start();
        } catch (IOException e) {
            System.err.println("[FATAL] RelayServer startup failed: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {

        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ) {
                String line;
                while ((line = in.readLine()) != null) {
                    RelayPacket packet = RelayPacket.decode(line);
                    System.out.println("[RELAY] From: " + packet);
                    // Şu anda sadece geri gönderiyoruz (echo-like davranış)
                    out.write("[RELAY RECEIVED] " + packet.encode());
                    out.newLine();
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Handler error: " + e.getMessage());
            }
        }
    }
}
