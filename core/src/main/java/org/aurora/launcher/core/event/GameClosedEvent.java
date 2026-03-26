package org.aurora.launcher.core.event;

public class GameClosedEvent {
    private final String instanceName;
    private final int exitCode;

    public GameClosedEvent(String instanceName, int exitCode) {
        this.instanceName = instanceName;
        this.exitCode = exitCode;
    }

    public String getInstanceName() { return instanceName; }
    public int getExitCode() { return exitCode; }
}