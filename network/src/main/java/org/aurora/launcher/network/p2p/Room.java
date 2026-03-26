package org.aurora.launcher.network.p2p;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {
    private final String roomCode;
    private final String uuid;
    private final String hostId;
    private String hostName;
    private final List<RoomMember> members;
    private final Instant createdAt;
    private int maxMembers;
    private RoomState state;

    public enum RoomState {
        WAITING,
        IN_GAME,
        CLOSED
    }

    public Room(String hostId, String hostName) {
        this.roomCode = generateRoomCode();
        this.uuid = UUID.randomUUID().toString();
        this.hostId = hostId;
        this.hostName = hostName;
        this.members = new ArrayList<>();
        this.createdAt = Instant.now();
        this.maxMembers = 8;
        this.state = RoomState.WAITING;
        
        this.members.add(new RoomMember(hostId, hostName, true));
    }

    private String generateRoomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getUuid() {
        return uuid;
    }

    public String getInviteLink() {
        return "https://aurora.gg/join/" + uuid;
    }

    public String getHostId() {
        return hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public List<RoomMember> getMembers() {
        return new ArrayList<>(members);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public RoomState getState() {
        return state;
    }

    public void setState(RoomState state) {
        this.state = state;
    }

    public boolean addMember(RoomMember member) {
        if (members.size() >= maxMembers) {
            return false;
        }
        members.add(member);
        return true;
    }

    public boolean removeMember(String memberId) {
        return members.removeIf(m -> m.getId().equals(memberId));
    }

    public RoomMember getMember(String memberId) {
        return members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElse(null);
    }

    public boolean isHost(String memberId) {
        return hostId.equals(memberId);
    }
}
