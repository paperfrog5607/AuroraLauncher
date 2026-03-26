package org.aurora.launcher.ui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.ImageCursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.aurora.launcher.ui.AuroraApplication;

public class HomeController extends BaseController {

    @FXML
    private StackPane heroBackground;
    
    @FXML
    private HBox recentGamesCarousel;
    
    @FXML
    private HBox allGamesCarousel;
    
    @FXML
    private HBox steamGamesCarousel;
    
    @FXML
    private HBox minecraftCarousel;

    private double dragStartX;
    private double scrollStartValue;
    private boolean isDragging = false;
    private Timeline momentumTimeline;

    @Override
    protected void onInitialize() {
        loadGames();
    }

    private void loadGames() {
        // TODO: 从后端API或本地加载游戏数据
        // 暂时添加示例卡片
        addSampleGameCards(recentGamesCarousel, 5);
        addSampleGameCards(allGamesCarousel, 10);
        addSampleGameCards(steamGamesCarousel, 6);
        addSampleGameCards(minecraftCarousel, 4);
    }

    private void addSampleGameCards(HBox container, int count) {
        for (int i = 0; i < count; i++) {
            GameCardController card = new GameCardController();
            card.setGameName("游戏 " + (i + 1));
            card.setGameSource(i % 2 == 0 ? "Steam" : "Minecraft");
            card.setStatus(i % 3 == 0 ? "running" : "installed");
            container.getChildren().add(card.getView());
        }
    }

    @FXML
    private void onMouseMoved(MouseEvent event) {
        if (!isDragging && heroBackground != null) {
            // 可选：基于鼠标位置微调背景
        }
    }

    @FXML
    private void onHomeClick() {
        // 已经是首页
    }

    @FXML
    private void onLibraryClick() {
        switchTab("library");
    }

    @FXML
    private void onToolsClick() {
        switchTab("tools");
    }

    @FXML
    private void onCommunityClick() {
        switchTab("community");
    }

    @FXML
    private void onSettingsClick() {
        switchTab("settings");
    }

    public void setHeroImage(String imageUrl) {
        // 设置英雄背景图片
    }
}
