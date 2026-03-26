package org.aurora.launcher.ui.router;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabRoute {
    private final String id;
    private final String controllerName;
    private final String fxml;
    private final Map<String, SubTabRoute> subTabs = new LinkedHashMap<>();
    private String defaultSubTab;

    public TabRoute(String id, String controllerName, String fxml) {
        this.id = id;
        this.controllerName = controllerName;
        this.fxml = fxml;
    }

    public TabRoute subTab(String id, String controllerName, String fxml) {
        SubTabRoute subRoute = new SubTabRoute(id, controllerName, fxml);
        subTabs.put(id, subRoute);
        if (defaultSubTab == null) {
            defaultSubTab = id;
        }
        return this;
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

    public Map<String, SubTabRoute> getSubTabs() {
        return subTabs;
    }

    public String getDefaultSubTab() {
        return defaultSubTab;
    }

    public boolean hasSubTabs() {
        return !subTabs.isEmpty();
    }
}