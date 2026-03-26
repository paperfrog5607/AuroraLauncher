package org.aurora.launcher.net.p2p;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpProxyAdapter implements GameAdapter {
    
    private static final int DEFAULT_PORT = 8192;
    private static final String PROTOCOL_HEADER = "AURORA";
    
    private final int port;
    private final AtomicBoolean running;
    private final CopyOnWriteArrayList<Socket> clientSockets;
    
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private Thread readerThread;
    private P2PManager p2pManager;
    private GameDataCallback callback;
    
    public TcpProxyAdapter() {
        this(DEFAULT_PORT);
    }
    
    public TcpProxyAdapter(int port) {
        this.port = port;
        this.running = new AtomicBoolean(false);
        this.clientSockets = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        
        acceptThread = new Thread(this::acceptClients);
        acceptThread.setDaemon(true);
        acceptThread.start();
        
        readerThread = new Thread(this::readFromGame);
        readerThread.setDaemon(true);
        readerThread.start();
    }
    
    @Override
    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
        
        for (Socket socket : clientSockets) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        clientSockets.clear();
    }
    
    @Override
    public boolean isRunning() {
        return running.get();
    }
    
    @Override
    public int getLocalPort() {
        return port;
    }
    
    public void setP2PManager(P2PManager manager) {
        this.p2pManager = manager;
    }
    
    @Override
    public void setOnGameDataCallback(GameDataCallback callback) {
        this.callback = callback;
    }
    
    @Override
    public void sendGameData(byte[] data) {
        byte[] packet = buildPacket(data);
        for (Socket socket : clientSockets) {
            try {
                if (socket.isConnected() && !socket.isClosed()) {
                    OutputStream out = socket.getOutputStream();
                    out.write(packet);
                    out.flush();
                }
            } catch (IOException ignored) {
            }
        }
    }
    
    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            
            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setTcpNoDelay(true);
                    clientSockets.add(clientSocket);
                } catch (IOException e) {
                    if (running.get()) {
                    }
                }
            }
        } catch (IOException e) {
        }
    }
    
    private void readFromGame() {
        while (running.get()) {
            for (Socket socket : clientSockets) {
                try {
                    if (socket.isConnected() && !socket.isClosed() && socket.getInputStream().available() > 0) {
                        InputStream in = socket.getInputStream();
                        byte[] header = new byte[6];
                        int read = in.read(header);
                        
                        if (read == 6 && PROTOCOL_HEADER.equals(new String(header))) {
                            int length = (in.read() & 0xFF) << 8 | (in.read() & 0xFF);
                            byte[] data = new byte[length];
                            int totalRead = 0;
                            while (totalRead < length) {
                                int r = in.read(data, totalRead, length - totalRead);
                                if (r == -1) break;
                                totalRead += r;
                            }
                            
                            if (p2pManager != null) {
                                p2pManager.broadcastToRoom(data);
                            }
                            
                            if (callback != null) {
                                callback.onDataReceived(data, null);
                            }
                        }
                    }
                } catch (IOException ignored) {
                }
            }
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }
    
    private byte[] buildPacket(byte[] data) {
        byte[] packet = new byte[8 + data.length];
        byte[] header = PROTOCOL_HEADER.getBytes();
        packet[0] = header[0];
        packet[1] = header[1];
        packet[2] = header[2];
        packet[3] = header[3];
        packet[4] = header[4];
        packet[5] = header[5];
        packet[6] = (byte)(data.length >> 8);
        packet[7] = (byte)(data.length & 0xFF);
        System.arraycopy(data, 0, packet, 8, data.length);
        return packet;
    }
}
