package org.aurora.launcher.ui.controller.launch;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.aurora.launcher.account.model.Account;
import org.aurora.launcher.account.session.SessionManager;
import org.aurora.launcher.launcher.launch.GameLauncher;
import org.aurora.launcher.launcher.launch.LaunchOptions;
import org.aurora.launcher.launcher.launch.LaunchProfile;
import org.aurora.launcher.launcher.version.VersionInfo;
import org.aurora.launcher.launcher.version.VersionManager;
import org.aurora.launcher.ui.controller.CardController;
import org.aurora.launcher.ui.service.ServiceLocator;
import org.aurora.launcher.ui.service.VersionDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class QuickLaunchCard extends CardController {
    
    private static final Logger logger = LoggerFactory.getLogger(QuickLaunchCard.class);
    
    @FXML
    private ImageView avatarView;
    
    @FXML
    private Label usernameLabel;
    
    @FXML
    private Label accountTypeLabel;
    
    @FXML
    private ComboBox<VersionInfo> versionBox;
    
    @FXML
    private Button launchButton;
    
    private SessionManager sessionManager;
    private VersionManager versionManager;
    private GameLauncher gameLauncher;
    private VersionDownloadService downloadService;
    
    @Override
    protected void onInitialize() {
        try {
            sessionManager = ServiceLocator.get(SessionManager.class);
            versionManager = ServiceLocator.get(VersionManager.class);
            gameLauncher = ServiceLocator.get(GameLauncher.class);
            downloadService = ServiceLocator.get(VersionDownloadService.class);
        } catch (Exception e) {
            logger.error("Failed to get services", e);
        }
        
        loadAccountInfo();
        loadVersionList();
    }
    
    private void loadAccountInfo() {
        try {
            Account account = sessionManager.getCurrentSession();
            if (account != null) {
                usernameLabel.setText(account.getDisplayName());
                accountTypeLabel.setText(account.getType().name().toLowerCase().contains("offline") ? "离线账户" : "在线账户");
            } else {
                usernameLabel.setText(t("account.notLoggedIn"));
                accountTypeLabel.setText("");
            }
        } catch (Exception e) {
            logger.error("Failed to load account info", e);
            usernameLabel.setText(t("account.notLoggedIn"));
        }
    }
    
    private void loadVersionList() {
        try {
            if (versionManager != null) {
                versionManager.getAvailableVersions().thenAccept(versions -> {
                    Platform.runLater(() -> {
                        versionBox.getItems().clear();
                        int downloadedCount = 0;
                        for (VersionInfo version : versions) {
                            if (downloadService != null && downloadService.isVersionDownloaded(version.getId())) {
                                versionBox.getItems().add(version);
                                downloadedCount++;
                            }
                        }
                        if (!versionBox.getItems().isEmpty()) {
                            versionBox.getSelectionModel().selectFirst();
                            launchButton.setDisable(false);
                        } else {
                            launchButton.setDisable(true);
                        }
                        logger.info("Loaded {} downloaded versions", downloadedCount);
                    });
                });
            }
        } catch (Exception e) {
            logger.error("Failed to load version list", e);
        }
    }
    
    @FXML
    private void onLaunch() {
        VersionInfo selected = versionBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        
        launchButton.setDisable(true);
        launchButton.setText(t("instance.launching"));
        
        if (gameLauncher != null && sessionManager.getCurrentSession() != null) {
            LaunchProfile profile = new LaunchProfile();
            profile.setInstanceId(selected.getId());
            profile.setVersion(selected);
            profile.setAccount(sessionManager.getCurrentSession());
            
            LaunchOptions options = new LaunchOptions();
            
            CompletableFuture<Process> future = gameLauncher.launch(profile, options);
            future.thenAccept(process -> {
                Platform.runLater(() -> {
                    logger.info("Game launched successfully");
                    launchButton.setDisable(false);
                    launchButton.setText(t("action.launch"));
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    logger.error("Launch failed: {}", throwable.getMessage());
                    launchButton.setDisable(false);
                    launchButton.setText(t("action.launch"));
                });
                return null;
            });
        } else {
            launchButton.setDisable(false);
            launchButton.setText(t("action.launch"));
            logger.error("Game launcher not initialized or no account");
        }
    }
    
    @FXML
    private void onChangeAccount() {
        if (router != null) {
            router.switchTab("settings", "account");
        }
    }
    
    @FXML
    private void onRefreshVersions() {
        loadVersionList();
    }
    
    @FXML
    private void onVersionSelected() {
    }
}