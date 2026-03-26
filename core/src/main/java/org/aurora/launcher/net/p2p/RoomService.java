package org.aurora.launcher.net.p2p;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RoomService {
    private final Map<String, Room> roomsByCode;
    private final Map<String, Room> roomsByUuid;
    private final Map<String, Room> playerRooms;

    public RoomService() {
        this.roomsByCode = new ConcurrentHashMap<>();
        this.roomsByUuid = new ConcurrentHashMap<>();
        this.playerRooms = new ConcurrentHashMap<>();
    }

    public Room createRoom(String hostId, String hostName) {
        Room room = new Room(hostId, hostName);
        roomsByCode.put(room.getRoomCode(), room);
        roomsByUuid.put(room.getUuid(), room);
        playerRooms.put(hostId, room);
        return room;
    }

    public Optional<Room> getRoomByCode(String roomCode) {
        if (roomCode == null) {
            return Optional.empty();
        }
        String normalized = roomCode.toUpperCase().trim();
        return Optional.ofNullable(roomsByCode.get(normalized));
    }

    public Optional<Room> getRoomByUuid(String uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(roomsByUuid.get(uuid));
    }

    public Optional<Room> getRoomByPlayer(String playerId) {
        return Optional.ofNullable(playerRooms.get(playerId));
    }

    public boolean joinRoom(String roomCode, RoomMember member) {
        Optional<Room> roomOpt = getRoomByCode(roomCode);
        if (roomOpt.isEmpty()) {
            return false;
        }
        
        Room room = roomOpt.get();
        if (room.getState() != Room.RoomState.WAITING) {
            return false;
        }
        
        if (!room.addMember(member)) {
            return false;
        }
        
        playerRooms.put(member.getId(), room);
        return true;
    }

    public boolean leaveRoom(String playerId) {
        Room room = playerRooms.remove(playerId);
        if (room == null) {
            return false;
        }
        
        room.removeMember(playerId);
        
        if (room.isHost(playerId)) {
            closeRoom(room.getRoomCode());
        }
        
        return true;
    }

    public void closeRoom(String roomCode) {
        Room room = roomsByCode.remove(roomCode);
        if (room != null) {
            roomsByUuid.remove(room.getUuid());
            room.getMembers().forEach(m -> playerRooms.remove(m.getId()));
            room.setState(Room.RoomState.CLOSED);
        }
    }

    public String extractRoomCodeFromLink(String link) {
        if (link == null || link.isEmpty()) {
            return null;
        }
        
        if (link.contains("aurora.gg/join/")) {
            String uuid = link.substring(link.lastIndexOf("/") + 1);
            return uuid;
        }
        
        if (link.matches("^[A-Z0-9]{6}$")) {
            return link.toUpperCase();
        }
        
        return null;
    }
}
