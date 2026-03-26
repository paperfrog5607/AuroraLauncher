package org.aurora.launcher.ui.controller.launch;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.router.TabRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(LaunchController.class);

    @FXML
    private Label versionName;

    @FXML
    private ComboBox<String> versionBox;

    @FXML
    private ComboBox<String> accountBox;

    @FXML
    private HBox recentGames;

    @FXML
    private Button playButton;

    @Override
    protected void onInitialize() {
        logger.info("LaunchController initialized");
        loadVersions();
        loadAccounts();
        loadRecentGames();
        playEnterAnimation();
    }

    private void playEnterAnimation() {
        try {
            javafx.scene.Node root = versionBox.getParent();
            while (root != null && !(root instanceof javafx.scene.layout.BorderPane)) {
                root = root.getParent();
            }
            if (root != null) {
                root.setOpacity(0);
                root.setTranslateY(20);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), root);
                slideUp.setFromY(20);
                slideUp.setToY(0);

                ParallelTransition enter = new ParallelTransition(fadeIn, slideUp);
                enter.play();
            }
        } catch (Exception e) {
            logger.debug("Animation skipped: {}", e.getMessage());
        }
    }

    private void loadVersions() {
        versionBox.getItems().addAll(
            "Minecraft 1.21.4",
            "Minecraft 1.21.3",
            "Minecraft 1.20.6",
            "Minecraft 1.19.4",
            "Minecraft 1.18.2",
            "Minecraft 1.16.5"
        );
        versionBox.getSelectionModel().selectFirst();
    }

    private void loadAccounts() {
        accountBox.getItems().addAll(
            "玩家 (离线)",
            "Steve (离线)",
            "Alex (离线)"
        );
        accountBox.getSelectionModel().selectFirst();
    }

    private void loadRecentGames() {
        recentGames.getChildren().clear();
        
        String[] recentVersions = {"1.21.4", "1.20.6", "1.19.4", "1.18.2"};
        for (String version : recentVersions) {
            VBox card = createRecentCard(version);
            recentGames.getChildren().add(card);
        }
    }

    private VBox createRecentCard(String version) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("recent-card-switch");
        card.setOnMouseClicked(e -> {
            versionBox.setValue("Minecraft " + version);
            logger.info("Selected recent version: {}", version);
        });

        Label icon = new Label("🎮");
        icon.getStyleClass().add("recent-card-icon");

        Label name = new Label(version);
        name.getStyleClass().add("recent-card-name");

        Label status = new Label("✓ 已安装");
        status.getStyleClass().add("recent-card-status");

        card.getChildren().addAll(icon, name, status);

        return card;
    }

    @FXML
    private void onLaunch() {
        String selectedVersion = versionBox.getValue();
        String selectedAccount = accountBox.getValue();
        logger.info("Launching game: {}, account: {}", selectedVersion, selectedAccount);
    }

    @FXML
    private void onDownloadTab() {
        if (router != null) {
            router.switchTab("download");
        }
    }

    @FXML
    private void onOpenFolder() {
        logger.info("Opening game folder");
    }
}
