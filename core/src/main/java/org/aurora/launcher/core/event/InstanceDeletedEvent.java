package org.aurora.launcher.core.event;

public class InstanceDeletedEvent {
    private final String instanceName;

    public InstanceDeletedEvent(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() { return instanceName; }
}