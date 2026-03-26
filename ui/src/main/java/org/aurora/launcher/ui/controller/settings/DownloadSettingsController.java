package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;

public class DownloadSettingsController extends BaseController {
    
    @FXML
    private Spinner<Integer> concurrentSpinner;
    
    @FXML
    private CheckBox proxyEnabledBox;
    
    @FXML
    private TextField proxyHostField;
    
    @FXML
    private TextField proxyPortField;
    
    @FXML
    private ComboBox<String> mirrorBox;
    
    @Override
    protected void onInitialize() {
        loadSettings();
    }
    
    private void loadSettings() {
        if (mirrorBox != null) {
            mirrorBox.getItems().addAll(
                t("mirror.auto"),
                t("mirror.mojang"),
                t("mirror.bmclapi"),
                t("mirror.mcbsc")
            );
        }
    }
    
    @FXML
    private void onSave() {
        showNotification(t("settings.saved"));
    }
    
    private void showNotification(String message) {
    }
}