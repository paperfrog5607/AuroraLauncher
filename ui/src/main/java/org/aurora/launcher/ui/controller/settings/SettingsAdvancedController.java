package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsAdvancedController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsAdvancedController.class);

    @FXML
    private TextArea jvmArgsField;

    @FXML
    private TextArea gameArgsField;

    @FXML
    private CheckBox checkGameFiles;

    @FXML
    private CheckBox checkJava;

    @FXML
    private CheckBox autoRepair;

    @FXML
    private CheckBox showDebug;

    @Override
    protected void onInitialize() {
        logger.info("SettingsAdvancedController initialized");
        loadSettings();
    }

    private void loadSettings() {
        jvmArgsField.setText("-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch");
        checkGameFiles.setSelected(true);
        checkJava.setSelected(true);
    }

    @FXML
    private void onBack() {
        if (router != null) {
            router.switchTab("settings");
        }
    }

    @FXML
    private void addG1GC() {
        String current = jvmArgsField.getText();
        String newArg = "-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch";
        if (!current.contains("UseG1GC")) {
            jvmArgsField.setText(current + " " + newArg);
        }
    }

    @FXML
    private void addAGC() {
        String current = jvmArgsField.getText();
        String newArg = "-XX:+UseParallelGC -XX:+UseParallelOldGC";
        if (!current.contains("UseParallelGC")) {
            jvmArgsField.setText(current + " " + newArg);
        }
    }

    @FXML
    private void addHighMemory() {
        String current = jvmArgsField.getText();
        String newArg = "-Xmx4G -Xms2G -XX:+UseG1GC -XX:MaxGCPauseMillis=200";
        if (!current.contains("Xmx")) {
            jvmArgsField.setText(current + " " + newArg);
        }
    }

    @FXML
    private void onReset() {
        jvmArgsField.setText("-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch");
        gameArgsField.setText("");
        checkGameFiles.setSelected(true);
        checkJava.setSelected(true);
        autoRepair.setSelected(false);
        showDebug.setSelected(false);
    }

    @FXML
    private void onSave() {
        logger.info("Saving advanced settings");
    }
}
