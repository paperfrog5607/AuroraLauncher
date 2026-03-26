package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsHelpController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsHelpController.class);

    @Override
    protected void onInitialize() {
        logger.info("SettingsHelpController initialized");
    }

    @FXML
    private void onBack() {
        if (router != null) {
            router.switchTab("settings");
        }
    }

    @FXML
    private void onWebsite() {
        logger.info("Opening website");
    }

    @FXML
    private void onChangelog() {
        logger.info("Opening changelog");
    }

    @FXML
    private void onFeedback() {
        logger.info("Opening feedback");
    }

    @FXML
    private void onDocumentation() {
        logger.info("Opening documentation");
    }
}
