package org.aurora.launcher.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class CreatorController {
    @FXML
    private VBox root;

    private Object mainController;
    private Object router;

    @FXML
    private void initialize() {
    }

    public void setMainController(Object controller) {
        this.mainController = controller;
    }

    public void setRouter(Object router) {
        this.router = router;
    }
}
