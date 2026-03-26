package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancementController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdvancementController.class);

    @FXML
    private TreeView<String> advancementTree;

    @FXML
    private Label advancementIdLabel;

    @FXML
    private Label displayNameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label parentLabel;

    @Override
    protected void onInitialize() {
    }

    @FXML
    private void onAddAdvancement() {
        logger.info("Adding new advancement");
    }

    @FXML
    private void onRefresh() {
        logger.info("Refreshing advancement tree");
    }
}
