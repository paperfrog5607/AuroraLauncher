package org.aurora.launcher.ui.component.common;

import javafx.scene.control.Label;

public class StatusBadge extends Label {
    
    public StatusBadge(String text, String styleClass) {
        super(text);
        getStyleClass().addAll("status-badge", styleClass);
    }
    
    public StatusBadge(String text) {
        this(text, "status-default");
    }
}