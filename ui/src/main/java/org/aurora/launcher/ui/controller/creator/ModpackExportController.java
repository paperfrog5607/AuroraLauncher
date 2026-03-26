package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModpackExportController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ModpackExportController.class);

    @FXML
    private ComboBox<String> formatBox;

    @FXML
    private CheckBox preserveFilenamesCheck;

    @FXML
    private TextField outputPathField;

    @FXML
    private ListView<String> modList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @Override
    protected void onInitialize() {
        formatBox.getItems().addAll(
            "双平台导出 (推荐)",
            "CurseForge (.zip)",
            "Modrinth (.mrpack)",
            "Aurora (.aurora)"
        );
        formatBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onBrowseOutput() {
        logger.info("Browsing for output directory");
    }

    @FXML
    private void onExport() {
        logger.info("Starting export");
        progressBar.setVisible(true);
        statusLabel.setVisible(true);
    }

    @FXML
    private void onCancel() {
        switchTab("creator");
    }
}
