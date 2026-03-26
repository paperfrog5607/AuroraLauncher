package org.aurora.launcher.core.event;

public class InstanceModifiedEvent {
    private final String instanceName;

    public InstanceModifiedEvent(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() { return instanceName; }
}