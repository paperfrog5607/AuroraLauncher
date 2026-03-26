package org.aurora.launcher.ui.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.aurora.launcher.modpack.instance.Instance;

public class InstanceEvent extends Event {
    
    public static final EventType<InstanceEvent> ANY = 
        new EventType<>(Event.ANY, "INSTANCE");
    public static final EventType<InstanceEvent> LAUNCH = 
        new EventType<>(ANY, "LAUNCH");
    public static final EventType<InstanceEvent> SETTINGS = 
        new EventType<>(ANY, "SETTINGS");
    public static final EventType<InstanceEvent> DELETE = 
        new EventType<>(ANY, "DELETE");
    
    private final Instance instance;
    
    public InstanceEvent(EventType<? extends Event> eventType, Instance instance) {
        super(eventType);
        this.instance = instance;
    }
    
    public Instance getInstance() {
        return instance;
    }
}