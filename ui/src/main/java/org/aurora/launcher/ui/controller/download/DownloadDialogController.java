package org.aurora.launcher.ui.controller.download;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.aurora.launcher.api.mojang.VersionInfo;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadDialogController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadDialogController.class);
    
    @FXML
    private Label versionTitle;
    
    private VersionInfo version;
    
    public void setVersion(VersionInfo version) {
        this.version = version;
        if (versionTitle != null) {
            versionTitle.setText("Minecraft " + version.getId());
        }
    }
    
    @FXML
    private void onClose() {
        Stage stage = (Stage) versionTitle.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void onDownloadVanilla() {
        logger.info("Download vanilla: {}", version != null ? version.getId() : "unknown");
        closeDialog();
    }
    
    @FXML
    private void onDownloadForge() {
        logger.info("Download Forge for: {}", version != null ? version.getId() : "unknown");
        closeDialog();
    }
    
    @FXML
    private void onDownloadFabric() {
        logger.info("Download Fabric for: {}", version != null ? version.getId() : "unknown");
        closeDialog();
    }
    
    @FXML
    private void onDownloadNeoForge() {
        logger.info("Download NeoForge for: {}", version != null ? version.getId() : "unknown");
        closeDialog();
    }
    
    @FXML
    private void onDownloadQuilt() {
        logger.info("Download Quilt for: {}", version != null ? version.getId() : "unknown");
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) versionTitle.getScene().getWindow();
        stage.close();
    }
}
