package org.aurora.launcher.ui.controller.download;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.aurora.launcher.ui.service.VersionDownloadService;
import org.aurora.launcher.ui.service.VersionDownloadService.DownloadProgress;
import org.aurora.launcher.ui.service.VersionDownloadService.DownloadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class DownloadProgressController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadProgressController.class);
    
    @FXML
    private Label versionLabel;
    
    @FXML
    private Label fileNameLabel;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label progressPercentLabel;
    
    @FXML
    private Label progressSizeLabel;
    
    @FXML
    private Label speedLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button backgroundButton;
    
    private VersionDownloadService downloadService;
    private String versionId;
    private org.aurora.launcher.api.mojang.VersionInfo versionInfo;
    private java.util.concurrent.CompletableFuture<DownloadResult> downloadFuture;
    private long lastUpdateTime = 0;
    private long lastBytes = 0;
    private static final DecimalFormat DF = new DecimalFormat("#.##");
    
    public void initialize() {
        logger.info("DownloadProgressController initialized");
    }
    
    public void setDownloadService(VersionDownloadService service) {
        this.downloadService = service;
    }
    
    public void setVersionInfo(org.aurora.launcher.api.mojang.VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
        this.versionId = versionInfo.getId();
        if (versionLabel != null) {
            versionLabel.setText("Minecraft " + versionId);
        }
    }
    
    public void startDownload() {
        if (downloadService == null || versionInfo == null) {
            logger.error("Cannot start download: service or versionInfo is null");
            return;
        }
        
        logger.info("Starting download for version: {}", versionId);
        updateStatus("正在连接服务器...");
        
        downloadFuture = downloadService.downloadVersion(versionInfo, this::onProgressUpdate);
        
        downloadFuture.thenAccept(result -> {
            Platform.runLater(() -> {
                if (result.success) {
                    onDownloadSuccess();
                } else {
                    onDownloadFailed(result.error);
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                onDownloadFailed(e.getMessage());
            });
            return null;
        });
    }
    
    private void onProgressUpdate(DownloadProgress progress) {
        Platform.runLater(() -> {
            fileNameLabel.setText("正在下载: " + progress.stage);
            
            double percent = progress.progress * 100;
            progressBar.setProgress(progress.progress);
            progressPercentLabel.setText(String.format("%.1f%%", percent));
            
            String sizeStr = formatSize(progress.current) + " / " + formatSize(progress.total);
            progressSizeLabel.setText(sizeStr);
            
            long now = System.currentTimeMillis();
            if (now - lastUpdateTime >= 500) {
                long timeDiff = now - lastUpdateTime;
                long bytesDiff = progress.current - lastBytes;
                if (timeDiff > 0 && bytesDiff > 0) {
                    double speed = (bytesDiff * 1000.0) / timeDiff;
                    speedLabel.setText("下载速度: " + formatSpeed(speed));
                }
                lastUpdateTime = now;
                lastBytes = progress.current;
            }
            
            updateStatus("下载中...");
        });
    }
    
    private void onDownloadSuccess() {
        logger.info("Download completed successfully for version: {}", versionId);
        progressPercentLabel.setText("100%");
        progressBar.setProgress(1.0);
        fileNameLabel.setText("下载完成!");
        speedLabel.setText("下载速度: --");
        updateStatus("下载完成!");
        
        cancelButton.setText("关闭");
        backgroundButton.setText("启动游戏");
        backgroundButton.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-padding: 8 24; -fx-font-size: 13px; -fx-background-radius: 6;");
    }
    
    private void onDownloadFailed(String error) {
        logger.error("Download failed for version {}: {}", versionId, error);
        speedLabel.setText("下载速度: 0 KB/s");
        updateStatus("下载失败: " + error);
        
        cancelButton.setText("关闭");
        backgroundButton.setText("重试");
        backgroundButton.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-padding: 8 24; -fx-font-size: 13px; -fx-background-radius: 6;");
    }
    
    private void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }
    
    @FXML
    private void onCancel() {
        logger.info("Cancel button clicked");
        if (downloadFuture != null && !downloadFuture.isDone()) {
            downloadService.cancelDownload(versionId);
        }
        closeWindow();
    }
    
    @FXML
    private void onBackground() {
        logger.info("Background button clicked");
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
    
    private String formatSize(long bytes) {
        if (bytes < 0) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return DF.format(bytes / 1024.0) + " KB";
        if (bytes < 1024 * 1024 * 1024) return DF.format(bytes / (1024.0 * 1024)) + " MB";
        return DF.format(bytes / (1024.0 * 1024 * 1024)) + " GB";
    }
    
    private String formatSpeed(double bytesPerSecond) {
        if (bytesPerSecond < 1024) return DF.format(bytesPerSecond) + " B/s";
        if (bytesPerSecond < 1024 * 1024) return DF.format(bytesPerSecond / 1024) + " KB/s";
        return DF.format(bytesPerSecond / (1024 * 1024)) + " MB/s";
    }
}