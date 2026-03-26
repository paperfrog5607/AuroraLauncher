package org.aurora.launcher.core.event;

public class InstanceCreatedEvent {
    private final String instanceName;

    public InstanceCreatedEvent(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() { return instanceName; }
}