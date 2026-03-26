package org.aurora.launcher.ui.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.aurora.launcher.launcher.version.VersionInfo;

public class VersionEvent extends Event {
    
    public static final EventType<VersionEvent> ANY = 
        new EventType<>(Event.ANY, "VERSION");
    public static final EventType<VersionEvent> DOWNLOAD = 
        new EventType<>(ANY, "DOWNLOAD");
    public static final EventType<VersionEvent> INSTALL_FABRIC = 
        new EventType<>(ANY, "INSTALL_FABRIC");
    public static final EventType<VersionEvent> INSTALL_FORGE = 
        new EventType<>(ANY, "INSTALL_FORGE");
    
    private final VersionInfo version;
    
    public VersionEvent(EventType<? extends Event> eventType, VersionInfo version) {
        super(eventType);
        this.version = version;
    }
    
    public VersionInfo getVersion() {
        return version;
    }
}