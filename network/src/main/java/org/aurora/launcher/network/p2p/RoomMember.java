package org.aurora.launcher.network.p2p;

import java.time.Instant;

public class RoomMember {
    private final String id;
    private final String displayName;
    private final boolean isHost;
    private ConnectionState state;
    private int latency;
    private Instant joinedAt;

    public RoomMember(String id, String displayName, boolean isHost) {
        this.id = id;
        this.displayName = displayName;
        this.isHost = isHost;
        this.state = ConnectionState.CONNECTED;
        this.latency = -1;
        this.joinedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isHost() {
        return isHost;
    }

    public ConnectionState getState() {
        return state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }
}
