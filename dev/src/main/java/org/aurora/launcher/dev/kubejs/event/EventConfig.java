package org.aurora.launcher.dev.kubejs.event;

public class EventConfig {
    private String filter;
    private String logic;

    public EventConfig() {
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}