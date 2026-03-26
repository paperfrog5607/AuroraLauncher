package org.aurora.launcher.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * 游戏库控制器
 */
public class LibraryController extends BaseController {

    @FXML
    private GridPane gamesGrid;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label filterBtn;
    
    @FXML
    private Label sortBtn;
    
    @FXML
    private ScrollPane gamesScroll;

    @Override
    protected void onInitialize() {
        loadGames();
        setupSearch();
    }

    private void loadGames() {
        // 从后端API或本地加载游戏
        addSampleGames();
    }

    private void addSampleGames() {
        String[] games = {
            "我的世界", "泰拉瑞亚", "星露谷物语", "饥荒", "缺氧",
            "森林", "冰汽时代", "雀魂", "APEX", "赛博朋克2077",
            "巫师3", "老头环", "霍格沃茨之遗", "博德之门3", "星空"
        };
        
        String[] sources = {"Minecraft", "Steam", "Steam", "Steam", "Steam", "Steam", "Epic", "Steam", "Steam", "Steam", "Steam", "Steam", "Steam", "Steam", "Steam"};
        
        int cols = 5;
        for (int i = 0; i < games.length; i++) {
            int row = i / cols;
            int col = i % cols;
            
            GameCardController card = new GameCardController();
            card.setGameName(games[i]);
            card.setGameSource(sources[i]);
            card.setStatus(i % 3 == 0 ? "running" : (i % 3 == 1 ? "installed" : "update"));
            card.setOnClick(e -> onGameClick(card));
            
            gamesGrid.add(card.getView(), col, row);
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, newVal) -> {
            filterGames(newVal);
        });
    }

    private void filterGames(String query) {
        // 根据搜索词过滤游戏
        // TODO: 实现搜索过滤
    }

    private void onGameClick(GameCardController card) {
        // 跳转到游戏详情页
        switchTab("gameDetail");
    }

    @FXML
    private void onBackClick() {
        switchTab("home");
    }

    @FXML
    private void onHomeClick() {
        switchTab("home");
    }

    @FXML
    private void onLibraryClick() {
        // 已经是游戏库
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
}
