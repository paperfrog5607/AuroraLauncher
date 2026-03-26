package org.aurora.launcher.ui.controller;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public abstract class CardController extends BaseController {
    @FXML
    protected StackPane expandIcon;

    @FXML
    protected VBox contentArea;

    private boolean expanded = true;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setupCardToggle();
    }

    private void setupCardToggle() {
        if (expandIcon != null) {
            expandIcon.setOnMouseClicked(e -> toggleCard());
        }
    }

    public void toggleCard() {
        expanded = !expanded;
        updateCardState();
        animateExpandIcon();
    }

    private void updateCardState() {
        if (contentArea != null) {
            contentArea.setVisible(expanded);
            contentArea.setManaged(expanded);
        }
    }

    private void animateExpandIcon() {
        if (expandIcon != null) {
            RotateTransition rotate = new RotateTransition(Duration.millis(200), expandIcon);
            rotate.setToAngle(expanded ? 0 : 180);
            rotate.play();
        }
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        updateCardState();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void expand() {
        if (!expanded) {
            toggleCard();
        }
    }

    public void collapse() {
        if (expanded) {
            toggleCard();
        }
    }
}