package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsFileController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsFileController.class);

    @FXML
    private TextField versionsPath;

    @FXML
    private TextField modsPath;

    @FXML
    private TextField resourcePacksPath;

    @FXML
    private TextField shaderPacksPath;

    @Override
    protected void onInitialize() {
        logger.info("SettingsFileController initialized");
        loadPaths();
    }

    private void loadPaths() {
        String basePath = System.getProperty("user.home") + "/.aurora";
        versionsPath.setText(basePath + "/versions");
        modsPath.setText(basePath + "/mods");
        resourcePacksPath.setText(basePath + "/resourcepacks");
        shaderPacksPath.setText(basePath + "/shaderpacks");
    }

    @FXML
    private void onBack() {
        if (router != null) {
            router.switchTab("settings");
        }
    }

    @FXML
    private void onBrowseVersions() {
        logger.info("Browse versions folder");
    }

    @FXML
    private void onBrowseMods() {
        logger.info("Browse mods folder");
    }

    @FXML
    private void onBrowseResourcePacks() {
        logger.info("Browse resource packs folder");
    }

    @FXML
    private void onBrowseShaderPacks() {
        logger.info("Browse shader packs folder");
    }

    @FXML
    private void onOpenGameFolder() {
        logger.info("Open game folder");
    }

    @FXML
    private void onOpenModsFolder() {
        logger.info("Open mods folder");
    }

    @FXML
    private void onOpenConfigFolder() {
        logger.info("Open config folder");
    }

    @FXML
    private void onReset() {
        loadPaths();
    }

    @FXML
    private void onSave() {
        logger.info("Saving file settings");
    }
}
