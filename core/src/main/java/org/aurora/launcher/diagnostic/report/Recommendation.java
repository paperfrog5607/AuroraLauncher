package org.aurora.launcher.diagnostic.report;

public class Recommendation {
    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    private String title;
    private String description;
    private Priority priority;
    private String action;

    public Recommendation() {
    }

    public Recommendation(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", priority, title, description);
    }
}