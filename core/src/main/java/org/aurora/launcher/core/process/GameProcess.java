package org.aurora.launcher.core.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;

public class GameProcess {
    private final String name;
    private final Process process;
    private final Instant startTime;

    public GameProcess(String name, Process process) {
        this.name = name;
        this.process = process;
        this.startTime = Instant.now();
    }

    public void waitFor() throws InterruptedException {
        process.waitFor();
    }

    public void kill() {
        process.destroy();
    }

    public void killForcibly() {
        process.destroyForcibly();
    }

    public boolean isAlive() {
        return process.isAlive();
    }

    public int getExitCode() {
        return process.exitValue();
    }

    public InputStream getInputStream() {
        return process.getInputStream();
    }

    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    public Duration getUptime() {
        return Duration.between(startTime, Instant.now());
    }

    public String getName() { return name; }
    public Instant getStartTime() { return startTime; }
}