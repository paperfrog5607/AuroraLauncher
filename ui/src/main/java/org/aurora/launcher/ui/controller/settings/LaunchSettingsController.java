package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.aurora.launcher.core.config.ConfigManager;
import org.aurora.launcher.launcher.java.JavaManager;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.service.ServiceLocator;

import java.io.File;
import java.util.List;

public class LaunchSettingsController extends BaseController {
    
    @FXML
    private TextField javaPathField;
    
    @FXML
    private Button detectJavaButton;
    
    @FXML
    private Button browseJavaButton;
    
    @FXML
    private ComboBox<String> memoryPresetBox;
    
    @FXML
    private Slider minMemorySlider;
    
    @FXML
    private Slider maxMemorySlider;
    
    @FXML
    private Label minMemoryLabel;
    
    @FXML
    private Label maxMemoryLabel;
    
    @FXML
    private TextArea jvmArgsField;
    
    private ConfigManager configManager;
    
    @Override
    protected void onInitialize() {
        loadSettings();
    }
    
    private void loadSettings() {
        if (memoryPresetBox != null) {
            memoryPresetBox.getItems().addAll(
                t("memory.preset.auto"),
                t("memory.preset.low"),
                t("memory.preset.standard"),
                t("memory.preset.high")
            );
        }
    }
    
    @FXML
    private void onDetectJava() {
    }
    
    @FXML
    private void onBrowseJava() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(t("java.select"));
        File file = chooser.showOpenDialog(null);
        if (file != null && javaPathField != null) {
            javaPathField.setText(file.getAbsolutePath());
        }
    }
    
    @FXML
    private void onSave() {
        showNotification(t("settings.saved"));
    }
    
    private void showNotification(String message) {
    }
}