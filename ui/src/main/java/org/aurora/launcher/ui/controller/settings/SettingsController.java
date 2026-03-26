package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.router.TabRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @Override
    protected void onInitialize() {
        logger.info("SettingsController initialized");
    }

    @FXML
    private void onLaunchSettings() {
        logger.info("Opening launch settings");
        navigateToSubSettings("settings", "launch");
    }

    @FXML
    private void onDownloadSettings() {
        logger.info("Opening download settings");
        navigateToSubSettings("settings", "download");
    }

    @FXML
    private void onThemeSettings() {
        logger.info("Opening theme settings");
        navigateToSubSettings("settings", "theme");
    }

    @FXML
    private void onAccountSettings() {
        logger.info("Opening account settings");
        navigateToSubSettings("settings", "account");
    }

    @FXML
    private void onAdvancedSettings() {
        logger.info("Opening advanced settings");
        navigateToSubSettings("settings", "advanced");
    }

    @FXML
    private void onFileSettings() {
        logger.info("Opening file settings");
        navigateToSubSettings("settings", "file");
    }

    @FXML
    private void onHelpSettings() {
        logger.info("Opening help settings");
        navigateToSubSettings("settings", "help");
    }

    private void navigateToSubSettings(String tab, String subTab) {
        if (router != null) {
            router.switchTab(tab, subTab);
        }
    }
}
