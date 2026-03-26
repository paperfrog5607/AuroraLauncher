package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModpackShareController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ModpackShareController.class);

    @FXML
    private TextField shareCodeField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private void onGenerate() {
        logger.info("Generating share code");
    }

    @FXML
    private void onCopy() {
        logger.info("Copying share code");
    }

    @FXML
    private void onParse() {
        logger.info("Parsing share code");
    }
}
