package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    @FXML
    private ListView<String> backupList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @Override
    protected void onInitialize() {
    }

    @FXML
    private void onCreateBackup() {
        logger.info("Creating backup");
        progressBar.setVisible(true);
        statusLabel.setVisible(true);
    }

    @FXML
    private void onRestoreBackup() {
        logger.info("Restoring backup");
    }

    @FXML
    private void onDeleteBackup() {
        logger.info("Deleting backup");
    }
}
