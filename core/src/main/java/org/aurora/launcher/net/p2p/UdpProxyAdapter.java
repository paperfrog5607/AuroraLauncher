package org.aurora.launcher.net.p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class UdpProxyAdapter implements GameAdapter {
    
    private static final int DEFAULT_PORT = 8193;
    private static final int MAX_PACKET_SIZE = 65535;
    private static final String PROTOCOL_HEADER = "AURORA";
    
    private final int port;
    private final AtomicBoolean running;
    private final CopyOnWriteArrayList<InetSocket> gameClients;
    
    private DatagramSocket socket;
    private Thread receiveThread;
    private P2PManager p2pManager;
    private GameDataCallback callback;
    
    public UdpProxyAdapter() {
        this(DEFAULT_PORT);
    }
    
    public UdpProxyAdapter(int port) {
        this.port = port;
        this.running = new AtomicBoolean(false);
        this.gameClients = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        
        receiveThread = new Thread(this::receivePackets);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }
    
    @Override
    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
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
        for (InetSocket client : gameClients) {
            try {
                DatagramPacket dp = new DatagramPacket(packet, packet.length, client.address, client.port);
                socket.send(dp);
            } catch (IOException ignored) {
            }
        }
    }
    
    private void receivePackets() {
        try {
            socket = new DatagramSocket(port);
            socket.setReuseAddress(true);
            byte[] buffer = new byte[MAX_PACKET_SIZE];
            
            while (running.get()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    
                    byte[] data = packet.getData();
                    int length = packet.getLength();
                    
                    if (length > 8 && PROTOCOL_HEADER.equals(new String(data, 0, 6))) {
                        int dataLen = ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);
                        if (dataLen <= length - 8) {
                            byte[] gameData = new byte[dataLen];
                            System.arraycopy(data, 8, gameData, 0, dataLen);
                            
                            InetAddress addr = packet.getAddress();
                            int port = packet.getPort();
                            addGameClient(addr, port);
                            
                            if (p2pManager != null) {
                                p2pManager.broadcastToRoom(gameData);
                            }
                            
                            if (callback != null) {
                                callback.onDataReceived(gameData, null);
                            }
                        }
                    }
                } catch (IOException e) {
                    if (running.get()) {
                    }
                }
            }
        } catch (IOException e) {
        }
    }
    
    private void addGameClient(InetAddress address, int port) {
        for (InetSocket client : gameClients) {
            if (client.address.equals(address) && client.port == port) {
                client.lastSeen = System.currentTimeMillis();
                return;
            }
        }
        gameClients.add(new InetSocket(address, port));
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
    
    private static class InetSocket {
        final InetAddress address;
        final int port;
        volatile long lastSeen;
        
        InetSocket(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            this.lastSeen = System.currentTimeMillis();
        }
    }
}
