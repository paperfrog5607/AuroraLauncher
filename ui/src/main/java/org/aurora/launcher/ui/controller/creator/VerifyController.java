package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(VerifyController.class);

    @FXML
    private ListView<String> resultList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @Override
    protected void onInitialize() {
    }

    @FXML
    private void onVerifyDependencies() {
        logger.info("Verifying dependencies");
        progressBar.setVisible(true);
        statusLabel.setVisible(true);
    }

    @FXML
    private void onDetectConflicts() {
        logger.info("Detecting conflicts");
    }

    @FXML
    private void onCheckCompatibility() {
        logger.info("Checking compatibility");
    }
}
