package org.aurora.launcher.ui.controller.download;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.aurora.launcher.api.mojang.VersionInfo;
import org.aurora.launcher.ui.cache.ImageCache;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.controller.download.DownloadProgressController;
import org.aurora.launcher.ui.service.ServiceLocator;
import org.aurora.launcher.ui.service.VersionDownloadService;
import org.aurora.launcher.ui.service.VersionService;
import org.aurora.launcher.ui.service.VersionDownloadService.DownloadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VersionController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ChoiceBox<String> typeFilter;
    
    @FXML
    private ListView<VersionInfo> versionList;
    
    @FXML
    private ProgressIndicator loadingIndicator;
    
    @FXML
    private Label statusLabel;
    
    private VersionService versionService;
    private VersionDownloadService downloadService;
    private String currentFilter = "all";
    
    @Override
    protected void onInitialize() {
        logger.info("VersionController onInitialize called");
        try {
            versionService = ServiceLocator.get(VersionService.class);
            downloadService = ServiceLocator.get(VersionDownloadService.class);
            logger.info("Services obtained: versionService={}, downloadService={}", 
                versionService != null, downloadService != null);
        } catch (Exception e) {
            logger.warn("Services not available: {}", e.getMessage());
        }
        
        setupTypeFilter();
        setupListCells();
        loadVersions();
    }
    
    private void setupTypeFilter() {
        typeFilter.setItems(FXCollections.observableArrayList(
            t("version.filter.all"),
            t("version.filter.release"),
            t("version.filter.snapshot")
        ));
        typeFilter.getSelectionModel().selectFirst();
        typeFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.equals(t("version.filter.all"))) {
                    currentFilter = "all";
                } else if (newVal.equals(t("version.filter.release"))) {
                    currentFilter = "release";
                } else if (newVal.equals(t("version.filter.snapshot"))) {
                    currentFilter = "snapshot";
                }
                loadVersions();
            }
        });
    }
    
    private void setupListCells() {
        versionList.setCellFactory(list -> new VersionListCell());
    }
    
    private void loadVersions() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
        if (statusLabel != null) {
            statusLabel.setText(t("version.loading"));
        }
        
        if (versionService == null) {
            logger.error("VersionService is null!");
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
            if (statusLabel != null) {
                statusLabel.setText("Version service not available");
            }
            return;
        }
        
        versionService.getVersionList()
            .thenAcceptAsync(versions -> {
                List<VersionInfo> filtered = versions.stream()
                    .filter(v -> {
                        if ("all".equals(currentFilter)) return true;
                        return currentFilter.equals(v.getType());
                    })
                    .limit(50)
                    .toList();
                
                Platform.runLater(() -> {
                    versionList.setItems(FXCollections.observableArrayList(filtered));
                    
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    if (statusLabel != null) {
                        statusLabel.setText(t("version.loaded", versions.size()));
                    }
                });
            })
            .exceptionally(e -> {
                logger.error("Failed to load versions", e);
                Platform.runLater(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    if (statusLabel != null) {
                        statusLabel.setText(t("version.loadFailed"));
                    }
                });
                return null;
            });
    }
    
    @FXML
    private void onSearch() {
        String query = searchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            loadVersions();
            return;
        }
        
        versionService.getVersionList()
            .thenAcceptAsync(versions -> {
                List<VersionInfo> filtered = versions.stream()
                    .filter(v -> v.getId().toLowerCase().contains(query))
                    .filter(v -> {
                        if ("all".equals(currentFilter)) return true;
                        return currentFilter.equals(v.getType());
                    })
                    .limit(50)
                    .toList();
                    
                Platform.runLater(() -> {
                    versionList.setItems(FXCollections.observableArrayList(filtered));
                });
            });
    }
    
    private void downloadVersion(VersionInfo version) {
        logger.info("downloadVersion called for: {}", version.getId());
        
        if (downloadService == null) {
            logger.error("DownloadService is null");
            if (statusLabel != null) {
                statusLabel.setText("Download service not available");
            }
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/download/DownloadProgressView.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            
            javafx.scene.layout.StackPane root = loader.load();
            
            DownloadProgressController controller = loader.getController();
            controller.setDownloadService(downloadService);
            controller.setVersionInfo(version);
            
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setAlwaysOnTop(true);
            
            controller.startDownload();
            dialogStage.show();
            
        } catch (Exception e) {
            logger.error("Failed to show download dialog", e);
            if (statusLabel != null) {
                statusLabel.setText("Failed to open download dialog: " + e.getMessage());
            }
        }
    }
    
    private class VersionListCell extends ListCell<VersionInfo> {
        private ImageView imageView;
        
        public VersionListCell() {
            imageView = new ImageView();
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
        }
        
        @Override
        protected void updateItem(VersionInfo version, boolean empty) {
            super.updateItem(version, empty);
            
            if (empty || version == null) {
                setGraphic(null);
                setText(null);
                imageView.setImage(null);
                return;
            }
            
            HBox container = new HBox(10);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPrefHeight(56);
            container.setMaxHeight(56);
            container.setStyle("-fx-padding: 8; -fx-background-color: #1F2937; -fx-background-radius: 8;");
            
            boolean isSnapshot = !"release".equals(version.getType());
            
            Label iconLabel = new Label();
            iconLabel.setMinSize(40, 40);
            iconLabel.setMaxSize(40, 40);
            if (isSnapshot) {
                iconLabel.setStyle("-fx-background-color: #F59E0B; -fx-background-radius: 8;");
            } else {
                iconLabel.setStyle("-fx-background-color: #10B981; -fx-background-radius: 8;");
            }
            
            VBox info = new VBox(2);
            info.setAlignment(Pos.CENTER_LEFT);
            
            Label name = new Label(version.getId());
            name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
            
            String typeText = "release".equals(version.getType()) ? "正式版" : "快照版";
            Label type = new Label(typeText);
            type.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
            
            info.getChildren().addAll(name, type);
            
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button downloadBtn = new Button("下载");
            downloadBtn.setStyle("-fx-background-color: #8B5CF6; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 13px;");
            downloadBtn.setOnAction(e -> downloadVersion(version));
            
            if (downloadService != null && downloadService.isVersionDownloaded(version.getId())) {
                downloadBtn.setText("重新下载");
                downloadBtn.setDisable(false);
                downloadBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 13px;");
            }
            
            container.getChildren().addAll(iconLabel, info, spacer, downloadBtn);
            setGraphic(container);
            setText(null);
        }
    }
}