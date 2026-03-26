package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepairController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RepairController.class);

    @FXML
    private CheckBox fixMissingCheck;

    @FXML
    private CheckBox fixCorruptedCheck;

    @FXML
    private CheckBox redownloadCheck;

    @FXML
    private ListView<String> issueList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @Override
    protected void onInitialize() {
    }

    @FXML
    private void onStartRepair() {
        logger.info("Starting repair");
        progressBar.setVisible(true);
        statusLabel.setVisible(true);
    }
}
