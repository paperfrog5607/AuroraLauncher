package org.aurora.launcher.core.event;

public class GameStartedEvent {
    private final String instanceName;

    public GameStartedEvent(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() { return instanceName; }
}