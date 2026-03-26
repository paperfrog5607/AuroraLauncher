package org.aurora.launcher.net.p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class P2PManager {
    private final IceService iceService;
    private final RoomService roomService;
    private final Map<String, P2PConnection> connections;
    private final CopyOnWriteArrayList<Consumer<P2PEvent>> eventListeners;
    private String localPlayerId;
    private String localPlayerName;
    private Room currentRoom;
    private boolean isRunning;
    private int localUdpPort;

    public P2PManager() {
        this.iceService = new IceService();
        this.roomService = new RoomService();
        this.connections = new ConcurrentHashMap<>();
        this.eventListeners = new CopyOnWriteArrayList<>();
        this.isRunning = false;
        this.localUdpPort = 51912;
    }

    public void start(String playerId, String playerName) {
        this.localPlayerId = playerId;
        this.localPlayerName = playerName;
        this.isRunning = true;
        
        iceService.start();
        
        startPacketListener();
        
        iceService.addNatTypeListener(this::onNatTypeChanged);
    }

    public void stop() {
        isRunning = false;
        
        if (currentRoom != null) {
            leaveRoom();
        }
        
        connections.values().forEach(P2PConnection::close);
        connections.clear();
        
        iceService.stop();
    }

    private void startPacketListener() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(localUdpPort)) {
                socket.setSoTimeout(1000);
                byte[] buffer = new byte[65535];
                
                while (isRunning) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        
                        handleIncomingPacket(packet.getData(), packet.getLength(),
                                packet.getAddress(), packet.getPort());
                        
                    } catch (java.net.SocketTimeoutException e) {
                        // continue
                    }
                }
            } catch (IOException e) {
                notifyEvent(new P2PEvent(P2PEvent.Type.ERROR, "Failed to start packet listener: " + e.getMessage()));
            }
        }).start();
    }

    private void handleIncomingPacket(byte[] data, int length, InetAddress address, int port) {
        if (length < 4) {
            return;
        }
        
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
        int magic = buffer.getInt();
        
        if (magic != 0x4155524F) {
            return;
        }
        
        byte type = buffer.get();
        
        switch (type) {
            case 0x01:
                handlePingPacket(buffer, address, port);
                break;
            case 0x02:
                handlePongPacket(buffer);
                break;
            case 0x03:
                handleDataPacket(buffer);
                break;
        }
    }

    private void handlePingPacket(ByteBuffer buffer, InetAddress address, int port) {
        if (buffer.remaining() < 16) {
            return;
        }
        
        byte[] remoteId = new byte[16];
        buffer.get(remoteId);
        String remotePlayerId = new UUID(byteArrayToLong(remoteId, 0),
                byteArrayToLong(remoteId, 8)).toString();
        
        int latency = measureLatency(address, port);
        
        byte[] response = ByteBuffer.allocate(24).putInt(0x4155524F)
                .put((byte) 0x02)
                .put(remoteId)
                .putInt(latency)
                .array();
        
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.send(new DatagramPacket(response, response.length, address, port));
            socket.close();
        } catch (IOException e) {
            // ignore
        }
        
        notifyEvent(new P2PEvent(P2PEvent.Type.PEER_CONNECTED, remotePlayerId));
    }

    private void handlePongPacket(ByteBuffer buffer) {
        if (buffer.remaining() < 4) {
            return;
        }
        int latency = buffer.getInt();
        notifyEvent(new P2PEvent(P2PEvent.Type.LATENCY_UPDATED, String.valueOf(latency)));
    }

    private void handleDataPacket(ByteBuffer buffer) {
        if (buffer.remaining() < 16) {
            return;
        }
        
        byte[] senderId = new byte[16];
        buffer.get(senderId);
        String senderPlayerId = new UUID(byteArrayToLong(senderId, 0),
                byteArrayToLong(senderId, 8)).toString();
        
        byte[] gameData = new byte[buffer.remaining()];
        buffer.get(gameData);
        
        notifyEvent(new P2PEvent(P2PEvent.Type.DATA_RECEIVED, senderPlayerId, gameData));
    }

    private int measureLatency(InetAddress address, int port) {
        byte[] request = ByteBuffer.allocate(20).putInt(0x4155524F)
                .put((byte) 0x01)
                .put(UUID.fromString(localPlayerId).toString().getBytes())
                .array();
        
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(1000);
            socket.send(new DatagramPacket(request, request.length, address, port));
            
            byte[] response = new byte[1024];
            DatagramPacket packet = new DatagramPacket(response, response.length);
            socket.receive(packet);
            
            long sendTime = System.currentTimeMillis();
            ByteBuffer rb = ByteBuffer.wrap(response);
            int magic = rb.getInt();
            byte type = rb.get();
            
            if (magic == 0x4155524F && type == 0x02) {
                return (int) (System.currentTimeMillis() - sendTime);
            }
            
            socket.close();
        } catch (IOException e) {
            // ignore
        }
        
        return -1;
    }

    public Room createRoom() {
        currentRoom = roomService.createRoom(localPlayerId, localPlayerName);
        notifyEvent(new P2PEvent(P2PEvent.Type.ROOM_CREATED, currentRoom.getRoomCode()));
        return currentRoom;
    }

    public boolean joinRoom(String roomCodeOrLink, String playerName) {
        String roomCode = roomService.extractRoomCodeFromLink(roomCodeOrLink);
        if (roomCode == null) {
            roomCode = roomCodeOrLink.toUpperCase().trim();
        }
        
        Optional<Room> roomOpt = roomService.getRoomByCode(roomCode);
        if (roomOpt.isEmpty()) {
            notifyEvent(new P2PEvent(P2PEvent.Type.ERROR, "Room not found: " + roomCode));
            return false;
        }
        
        Room room = roomOpt.get();
        RoomMember member = new RoomMember(localPlayerId, playerName, false);
        
        if (!roomService.joinRoom(roomCode, member)) {
            notifyEvent(new P2PEvent(P2PEvent.Type.ERROR, "Failed to join room"));
            return false;
        }
        
        currentRoom = room;
        notifyEvent(new P2PEvent(P2PEvent.Type.ROOM_JOINED, roomCode));
        return true;
    }

    public void leaveRoom() {
        if (currentRoom != null) {
            roomService.leaveRoom(localPlayerId);
            notifyEvent(new P2PEvent(P2PEvent.Type.ROOM_LEFT, currentRoom.getRoomCode()));
            currentRoom = null;
        }
        
        connections.values().forEach(P2PConnection::close);
        connections.clear();
    }

    public void sendToPeer(String peerId, byte[] data) {
        P2PConnection conn = connections.get(peerId);
        if (conn != null && conn.isConnected()) {
            conn.send(data);
        }
    }

    public void broadcastToRoom(byte[] data) {
        if (currentRoom == null) {
            return;
        }
        
        currentRoom.getMembers().forEach(member -> {
            if (!member.getId().equals(localPlayerId)) {
                sendToPeer(member.getId(), data);
            }
        });
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public IceService getIceService() {
        return iceService;
    }

    public RoomService getRoomService() {
        return roomService;
    }

    public NatType getNatType() {
        return iceService.getNatType();
    }

    public void addEventListener(Consumer<P2PEvent> listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(Consumer<P2PEvent> listener) {
        eventListeners.remove(listener);
    }

    private void notifyEvent(P2PEvent event) {
        eventListeners.forEach(l -> l.accept(event));
    }

    private void onNatTypeChanged(NatType type) {
        notifyEvent(new P2PEvent(P2PEvent.Type.NAT_TYPE_CHANGED, type.name()));
    }

    private long byteArrayToLong(byte[] array, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (array[offset + i] & 0xFF);
        }
        return value;
    }

    public class P2PConnection {
        private final String peerId;
        private InetAddress address;
        private int port;
        private volatile boolean connected;
        private Instant lastPing;

        public P2PConnection(String peerId) {
            this.peerId = peerId;
            this.connected = false;
            this.lastPing = Instant.now();
        }

        public void updateEndpoint(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            this.connected = true;
            this.lastPing = Instant.now();
        }

        public void send(byte[] data) {
            if (!connected || address == null) {
                return;
            }
            
            try {
                ByteBuffer buffer = ByteBuffer.allocate(data.length + 20);
                buffer.putInt(0x4155524F);
                buffer.put((byte) 0x03);
                buffer.put(UUID.fromString(localPlayerId).toString().getBytes());
                buffer.put(data);
                
                DatagramSocket socket = new DatagramSocket();
                socket.send(new DatagramPacket(buffer.array(), buffer.array().length, address, port));
                socket.close();
            } catch (IOException e) {
                // ignore
            }
        }

        public void close() {
            connected = false;
        }

        public boolean isConnected() {
            return connected;
        }

        public String getPeerId() {
            return peerId;
        }
    }

    public static class P2PEvent {
        public enum Type {
            ROOM_CREATED,
            ROOM_JOINED,
            ROOM_LEFT,
            PEER_CONNECTED,
            PEER_DISCONNECTED,
            NAT_TYPE_CHANGED,
            LATENCY_UPDATED,
            DATA_RECEIVED,
            ERROR
        }

        private final Type type;
        private final String data;
        private final byte[] binaryData;

        public P2PEvent(Type type, String data) {
            this.type = type;
            this.data = data;
            this.binaryData = null;
        }

        public P2PEvent(Type type, String data, byte[] binaryData) {
            this.type = type;
            this.data = data;
            this.binaryData = binaryData;
        }

        public Type getType() {
            return type;
        }

        public String getData() {
            return data;
        }

        public byte[] getBinaryData() {
            return binaryData;
        }
    }
}
