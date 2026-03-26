package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.aurora.launcher.ui.controller.BaseController;

import java.io.File;

public class ThemeSettingsController extends BaseController {
    
    @FXML
    private ComboBox<String> themeBox;
    
    @FXML
    private CheckBox darkModeBox;
    
    @FXML
    private CheckBox enableAnimationBox;
    
    @FXML
    private Button selectBackgroundButton;
    
    @FXML
    private ImageView backgroundPreview;
    
    @FXML
    private ComboBox<String> fontFamilyBox;
    
    @Override
    protected void onInitialize() {
        loadThemes();
        loadSettings();
    }
    
    private void loadThemes() {
        if (themeBox != null) {
            themeBox.getItems().addAll("Dark", "Light", "Custom");
        }
    }
    
    private void loadSettings() {
    }
    
    @FXML
    private void onThemeChange() {
    }
    
    @FXML
    private void onSelectBackground() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
        }
    }
    
    @FXML
    private void onResetBackground() {
        if (backgroundPreview != null) {
            backgroundPreview.setImage(null);
        }
    }
}