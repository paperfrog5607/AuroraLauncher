package org.aurora.launcher.ui.controller.download;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.aurora.launcher.api.mojang.VersionInfo;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.service.VersionService;
import org.aurora.launcher.ui.service.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    
    @FXML
    private TextField searchField;
    
    @FXML
    private VBox versionListContainer;
    
    @FXML
    private Button filterAll;
    
    @FXML
    private Button filterRelease;
    
    @FXML
    private Button filterSnapshot;
    
    @FXML
    private Button filterOld;
    
    private VersionService versionService;
    private List<VersionInfo> allVersions;
    private String currentFilter = "all";
    
    @Override
    protected void onInitialize() {
        logger.info("DownloadController onInitialize");
        setupFilterButtons();
        setupSearch();
        loadVersions();
    }
    
    private void setupFilterButtons() {
        filterAll.setOnAction(e -> setFilter("all"));
        filterRelease.setOnAction(e -> setFilter("release"));
        filterSnapshot.setOnAction(e -> setFilter("snapshot"));
        filterOld.setOnAction(e -> setFilter("old"));
        updateFilterButtons();
    }
    
    private void setupSearch() {
        searchField.setOnAction(e -> filterVersions());
    }
    
    private void setFilter(String filter) {
        currentFilter = filter;
        updateFilterButtons();
        filterVersions();
    }
    
    private void updateFilterButtons() {
        filterAll.getStyleClass().remove("active");
        filterRelease.getStyleClass().remove("active");
        filterSnapshot.getStyleClass().remove("active");
        filterOld.getStyleClass().remove("active");
        
        switch (currentFilter) {
            case "release":
                filterRelease.getStyleClass().add("active");
                break;
            case "snapshot":
                filterSnapshot.getStyleClass().add("active");
                break;
            case "old":
                filterOld.getStyleClass().add("active");
                break;
            default:
                filterAll.getStyleClass().add("active");
                break;
        }
    }
    
    private void loadVersions() {
        try {
            versionService = ServiceLocator.get(VersionService.class);
        } catch (Exception e) {
            logger.warn("Services not available: {}", e.getMessage());
        }
        
        if (versionService == null) {
            displayVersions(List.of());
            return;
        }
        
        versionService.getVersionList()
            .thenAcceptAsync(versions -> {
                allVersions = versions;
                Platform.runLater(() -> filterVersions());
            })
            .exceptionally(e -> {
                logger.error("Failed to load versions", e);
                Platform.runLater(() -> displayVersions(List.of()));
                return null;
            });
    }
    
    private void filterVersions() {
        if (allVersions == null) return;
        
        String query = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        
        List<VersionInfo> filtered = allVersions.stream()
            .filter(v -> {
                if (!query.isEmpty() && !v.getId().toLowerCase().contains(query)) {
                    return false;
                }
                if ("all".equals(currentFilter)) return true;
                if ("old".equals(currentFilter)) {
                    return !"release".equals(v.getType()) && !"snapshot".equals(v.getType());
                }
                return currentFilter.equals(v.getType());
            })
            .limit(100)
            .collect(Collectors.toList());
        
        displayVersions(filtered);
    }
    
    private void displayVersions(List<VersionInfo> versions) {
        versionListContainer.getChildren().clear();
        
        for (VersionInfo version : versions) {
            HBox row = createVersionRow(version);
            versionListContainer.getChildren().add(row);
        }
    }
    
    private HBox createVersionRow(VersionInfo version) {
        HBox row = new HBox();
        row.getStyleClass().add("version-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setSpacing(16);
        row.setPadding(new Insets(0, 16, 0, 16));
        row.setMinHeight(52);
        row.setPrefHeight(52);
        
        Label icon = new Label("🎮");
        icon.getStyleClass().add("version-row-icon");
        
        Label name = new Label(version.getId());
        name.getStyleClass().add("version-row-name");
        HBox.setMargin(name, new Insets(0, 0, 0, 8));
        
        String type = version.getType();
        String typeText;
        String typeClass;
        
        switch (type) {
            case "release":
                typeText = "● 正式版";
                typeClass = "version-row-type-release";
                break;
            case "snapshot":
                typeText = "◆ 快照版";
                typeClass = "version-row-type-snapshot";
                break;
            default:
                typeText = "■ 历史版";
                typeClass = "version-row-type-old";
                break;
        }
        
        Label typeLabel = new Label(typeText);
        typeLabel.getStyleClass().addAll("version-row-type", typeClass);
        HBox.setMargin(typeLabel, new Insets(0, 0, 0, 24));
        
        String dateStr = "--";
        if (version.getReleaseTime() != null) {
            try {
                LocalDateTime date = LocalDateTime.parse(version.getReleaseTime().replace("Z", ""));
                dateStr = date.format(DATE_FORMAT);
            } catch (Exception e) {
                // ignore
            }
        }
        
        Label dateLabel = new Label(dateStr);
        dateLabel.getStyleClass().add("version-row-date");
        HBox.setMargin(dateLabel, new Insets(0, 0, 0, 24));
        
        Label statusLabel = new Label("☐ 未下载");
        statusLabel.getStyleClass().addAll("version-row-status", "version-row-status-pending");
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        row.getChildren().addAll(icon, name, typeLabel, dateLabel, spacer, statusLabel);
        
        row.setOnMouseClicked(e -> {
            logger.info("Clicked version: {}", version.getId());
            showDownloadDialog(version);
        });
        
        return row;
    }
    
    private void showDownloadDialog(VersionInfo version) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/download/DownloadDialog.fxml"));
            Parent root = loader.load();
            
            DownloadDialogController controller = loader.getController();
            controller.setVersion(version);
            
            Stage dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            dialogStage.show();
            dialogStage.centerOnScreen();
            
        } catch (Exception e) {
            logger.error("Failed to show download dialog", e);
        }
    }
    
    @FXML
    private void onRefresh() {
        logger.info("Refresh clicked");
        loadVersions();
    }
    
    @FXML
    private void onFilterAll() {
        setFilter("all");
    }
    
    @FXML
    private void onFilterRelease() {
        setFilter("release");
    }
    
    @FXML
    private void onFilterSnapshot() {
        setFilter("snapshot");
    }
    
    @FXML
    private void onFilterOld() {
        setFilter("old");
    }
}
