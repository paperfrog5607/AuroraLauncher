package org.aurora.launcher.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * 游戏详情控制器
 */
public class GameDetailController extends BaseController {

    @FXML
    private ImageView heroImage;
    
    @FXML
    private ImageView gameIcon;
    
    @FXML
    private Label gameName;
    
    @FXML
    private Label gameDeveloper;
    
    @FXML
    private Label gameSource;
    
    @FXML
    private Label gameGenre;
    
    @FXML
    private Label gameDescription;
    
    @FXML
    private Label gameVersion;
    
    @FXML
    private VBox modList;
    
    @FXML
    private VBox saveList;
    
    @FXML
    private StackPane heroBackground;

    @Override
    protected void onInitialize() {
        // TODO: 从路由参数或全局状态获取游戏数据
        loadGameData();
    }

    private void loadGameData() {
        // 模拟数据
        gameName.setText("我的世界");
        gameDeveloper.setText("Mojang Studios");
        gameSource.setText("Minecraft");
        gameGenre.setText("沙盒建造");
        gameDescription.setText("《我的世界》（Minecraft）是一款沙盒建造游戏，"
                + "玩家可以在一个由方块组成的3D世界中自由探索、收集资源、建造建筑，"
                + "或者与朋友一起冒险。游戏拥有无限的世界生成系统，"
                + "玩家可以挖掘地下矿脉、种植农作物、养殖动物，"
                + "同时需要抵御各种夜间出现的怪物。");
        gameVersion.setText("当前版本: 1.21.4");
        
        loadMods();
    }

    private void loadMods() {
        String[] mods = {
            "OptiFine - 性能优化和光影支持",
            "Sodium - 渲染引擎优化",
            "Fabric API - 模组API",
            "JourneyMap - 小地图",
            "JEI - 物品合成表"
        };
        
        for (String mod : mods) {
            Label modLabel = new Label("📦 " + mod);
            modLabel.setStyle("-fx-text-fill: #A0AEC0; -fx-font-size: 13px; -fx-padding: 8 12;");
            modLabel.setOnMouseClicked(e -> {
                // TODO: 打开mod详情
            });
            modList.getChildren().add(modLabel);
        }
    }

    public void setGameData(String gameId) {
        // TODO: 根据gameId加载游戏数据
    }

    @FXML
    private void onBackClick() {
        switchTab("library");
    }

    @FXML
    private void onPlayClick() {
        // TODO: 启动游戏
    }
}
