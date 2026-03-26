package org.aurora.launcher.ui.router;

public class SubTabRoute {
    private final String id;
    private final String controllerName;
    private final String fxml;

    public SubTabRoute(String id, String controllerName, String fxml) {
        this.id = id;
        this.controllerName = controllerName;
        this.fxml = fxml;
    }

    public String getId() {
        return id;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getFxml() {
        return fxml;
    }
}