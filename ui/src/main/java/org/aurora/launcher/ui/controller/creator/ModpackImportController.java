package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModpackImportController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ModpackImportController.class);

    @FXML
    private ComboBox<String> sourceBox;

    @FXML
    private Label selectedFileLabel;

    @FXML
    private ListView<String> previewList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @Override
    protected void onInitialize() {
        sourceBox.getItems().addAll("CurseForge (.zip)", "Modrinth (.mrpack)", "Aurora (.aurora)");
    }

    @FXML
    private void onBrowseFile() {
        logger.info("Browsing for file");
    }

    @FXML
    private void onImport() {
        logger.info("Starting import");
        progressBar.setVisible(true);
        statusLabel.setVisible(true);
    }

    @FXML
    private void onCancel() {
        switchTab("creator");
    }
}
