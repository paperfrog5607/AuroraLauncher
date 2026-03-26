package org.aurora.launcher.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.aurora.launcher.ui.controller.BaseController;

import java.io.File;

public class AboutController extends BaseController {
    
    @FXML
    private Label versionLabel;
    
    @FXML
    private Label buildLabel;
    
    @FXML
    private Hyperlink githubLink;
    
    @FXML
    private Hyperlink websiteLink;
    
    @FXML
    private TextArea licenseArea;
    
    @FXML
    private Button checkUpdateButton;
    
    @Override
    protected void onInitialize() {
        loadInfo();
    }
    
    private void loadInfo() {
        if (versionLabel != null) {
            versionLabel.setText(t("about.version", "1.0.0"));
        }
        if (buildLabel != null) {
            buildLabel.setText(t("about.build", "2026-03-17"));
        }
        
        if (githubLink != null) {
            githubLink.setText("GitHub");
        }
    }
    
    @FXML
    private void onCheckUpdate() {
        if (checkUpdateButton != null) {
            checkUpdateButton.setDisable(true);
        }
    }
    
    @FXML
    private void onExportLogs() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("aurora-logs.zip");
        File file = chooser.showSaveDialog(null);
        if (file != null) {
        }
    }
}