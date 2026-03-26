package org.aurora.launcher.launcher.launch;

import java.time.Instant;
import java.nio.file.Path;

public class GameProcess {
    private String instanceId;
    private Process process;
    private Instant startTime;
    private Path logFile;
    private ProcessState state;

    public GameProcess() {
        this.state = ProcessState.STARTING;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Path getLogFile() {
        return logFile;
    }

    public void setLogFile(Path logFile) {
        this.logFile = logFile;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public boolean isRunning() {
        return process != null && process.isAlive();
    }

    public long getUptime() {
        if (startTime == null) return 0;
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    public int getExitCode() {
        if (process == null || process.isAlive()) {
            return -1;
        }
        return process.exitValue();
    }
}