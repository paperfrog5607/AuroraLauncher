package org.aurora.launcher.ui.controller.download;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.aurora.launcher.api.mojang.VersionInfo;
import org.aurora.launcher.ui.service.VersionDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VersionDetailController {
    private static final Logger logger = LoggerFactory.getLogger(VersionDetailController.class);
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String MC_GRASS_URL = "https://gamepedia.cursecdn.com/minecraft_gamepedia/3/35/GrassBlock.png";
    
    @FXML
    private ImageView versionIcon;
    
    @FXML
    private Label versionTitle;
    
    @FXML
    private Label versionDate;
    
    private VersionInfo version;
    private VersionDownloadService downloadService;
    private Stage parentStage;
    
    public void setVersion(VersionInfo version) {
        this.version = version;
        
        if (version != null) {
            versionTitle.setText("Minecraft " + version.getId());
            
            if (version.getReleaseTime() != null) {
                try {
                    LocalDateTime date = LocalDateTime.parse(version.getReleaseTime().replace("Z", ""));
                    versionDate.setText("发布日期: " + date.format(DATE_FORMAT));
                } catch (Exception e) {
                    versionDate.setText("发布日期: --");
                }
            }
            
            try {
                Image icon = new Image(MC_GRASS_URL, 64, 64, true, true);
                versionIcon.setImage(icon);
            } catch (Exception e) {
                logger.warn("Failed to load icon: {}", e.getMessage());
            }
        }
    }
    
    public void setDownloadService(VersionDownloadService service) {
        this.downloadService = service;
    }
    
    public void setParentStage(Stage stage) {
        this.parentStage = stage;
    }
    
    @FXML
    private void onClose() {
        if (parentStage != null) {
            parentStage.close();
        }
    }
    
    @FXML
    private void onDownloadVanilla() {
        startDownload("vanilla");
    }
    
    @FXML
    private void onDownloadForge() {
        startDownload("forge");
    }
    
    @FXML
    private void onDownloadFabric() {
        startDownload("fabric");
    }
    
    @FXML
    private void onDownloadNeoForge() {
        startDownload("neoforge");
    }
    
    @FXML
    private void onDownloadQuilt() {
        startDownload("quilt");
    }
    
    private void startDownload(String loaderType) {
        if (downloadService == null || version == null) {
            logger.error("DownloadService or version is null");
            return;
        }
        
        logger.info("Starting download for version {} with loader {}", version.getId(), loaderType);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/download/DownloadProgressView.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            
            VBox root = loader.load();
            
            DownloadProgressController controller = loader.getController();
            controller.setDownloadService(downloadService);
            controller.setVersionInfo(version);
            
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setAlwaysOnTop(true);
            
            if (parentStage != null) {
                parentStage.close();
            }
            
            controller.startDownload();
            dialogStage.show();
            
        } catch (Exception e) {
            logger.error("Failed to start download", e);
        }
    }
}