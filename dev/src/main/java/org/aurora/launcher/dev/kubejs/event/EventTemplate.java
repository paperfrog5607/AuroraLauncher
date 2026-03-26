package org.aurora.launcher.dev.kubejs.event;

import org.aurora.launcher.dev.template.KubeJsCategory;
import org.aurora.launcher.dev.template.KubeJsTemplate;

public class EventTemplate extends KubeJsTemplate {
    private EventType eventType;
    private String eventClass;

    public EventTemplate() {
        setCategory(KubeJsCategory.EVENT);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }
}