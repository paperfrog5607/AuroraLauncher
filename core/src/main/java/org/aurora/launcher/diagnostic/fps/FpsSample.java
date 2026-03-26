package org.aurora.launcher.diagnostic.fps;

import java.time.Instant;

public class FpsSample {
    private Instant timestamp;
    private int fps;
    private int frameTime;
    private String location;

    public FpsSample() {
        this.timestamp = Instant.now();
    }

    public FpsSample(int fps) {
        this.timestamp = Instant.now();
        this.fps = fps;
        this.frameTime = fps > 0 ? 1000 / fps : 0;
    }

    public FpsSample(Instant timestamp, int fps, int frameTime, String location) {
        this.timestamp = timestamp;
        this.fps = fps;
        this.frameTime = frameTime;
        this.location = location;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
        this.frameTime = fps > 0 ? 1000 / fps : 0;
    }

    public int getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(int frameTime) {
        this.frameTime = frameTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}