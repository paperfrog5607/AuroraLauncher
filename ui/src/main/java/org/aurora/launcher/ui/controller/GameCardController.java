package org.aurora.launcher.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

/**
 * 游戏卡片控制器
 */
public class GameCardController {

    private StackPane view;
    
    @FXML
    private ImageView coverImage;
    
    @FXML
    private Label gameName;
    
    @FXML
    private Label gameSource;
    
    @FXML
    private StackPane statusIndicator;
    
    @FXML
    private StackPane coverOverlay;

    public GameCardController() {
        createView();
    }

    private void createView() {
        view = new StackPane();
        view.setMinSize(200, 280);
        view.setMaxSize(200, 280);
        view.getStyleClass().add("game-card");
        
        VBox content = new VBox();
        content.setSpacing(0);
        
        StackPane coverContainer = new StackPane();
        coverContainer.setMinSize(200, 200);
        coverContainer.setMaxSize(200, 200);
        coverContainer.getStyleClass().add("cover-container");
        
        Rectangle bg = new Rectangle(200, 200);
        bg.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(37, 37, 66)),
            new Stop(1, Color.rgb(26, 26, 46))));
        coverContainer.getChildren().add(bg);
        
        coverImage = new ImageView();
        coverImage.setFitWidth(200);
        coverImage.setFitHeight(200);
        coverImage.setPreserveRatio(false);
        coverImage.setSmooth(true);
        coverContainer.getChildren().add(coverImage);
        
        coverOverlay = new StackPane();
        coverOverlay.setMinSize(200, 200);
        coverOverlay.setMaxSize(200, 200);
        coverOverlay.setOpacity(0);
        Rectangle overlay = new Rectangle(200, 200);
        overlay.setFill(Color.rgb(124, 58, 237, 0.2));
        coverOverlay.getChildren().add(overlay);
        coverContainer.getChildren().add(coverOverlay);
        
        statusIndicator = new StackPane();
        statusIndicator.setMinSize(12, 12);
        statusIndicator.setMaxSize(12, 12);
        statusIndicator.setTranslateX(88);
        statusIndicator.setTranslateY(-88);
        statusIndicator.setVisible(false);
        Rectangle statusDot = new Rectangle(12, 12);
        statusDot.setFill(Color.rgb(16, 185, 129));
        statusDot.setArcWidth(6);
        statusDot.setArcHeight(6);
        statusIndicator.getChildren().add(statusDot);
        coverContainer.getChildren().add(statusIndicator);
        
        VBox infoBox = new VBox();
        infoBox.setSpacing(4);
        infoBox.setMinSize(200, 80);
        infoBox.setMaxSize(200, 80);
        infoBox.setPadding(new javafx.geometry.Insets(12, 12, 12, 12));
        infoBox.getStyleClass().add("game-info");
        infoBox.setFillWidth(true);
        
        gameName = new Label();
        gameName.setMinSize(176, 20);
        gameName.setMaxSize(176, 20);
        gameName.getStyleClass().add("game-name");
        gameName.setText("游戏名称");
        gameName.setTextFill(Color.WHITE);
        gameName.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #FFFFFF;");
        
        gameSource = new Label();
        gameSource.setMinSize(176, 16);
        gameSource.setMaxSize(176, 16);
        gameSource.getStyleClass().add("game-source");
        gameSource.setText("Steam");
        gameSource.setTextFill(Color.rgb(100, 116, 139));
        gameSource.setStyle("-fx-font-size: 11px;");
        
        infoBox.getChildren().addAll(gameName, gameSource);
        
        content.getChildren().addAll(coverContainer, infoBox);
        view.getChildren().add(content);
        
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        view.setOnMouseEntered(e -> {
            view.setStyle("-fx-translate-y: -8px; -fx-scale-x: 1.03; -fx-scale-y: 1.03;");
            if (coverOverlay != null) {
                coverOverlay.setOpacity(1);
            }
        });
        
        view.setOnMouseExited(e -> {
            view.setStyle("");
            if (coverOverlay != null) {
                coverOverlay.setOpacity(0);
            }
        });
        
        view.setOnMousePressed(e -> {
            view.setStyle("-fx-scale-x: 0.98; -fx-scale-y: 0.98;");
        });
        
        view.setOnMouseReleased(e -> {
            view.setStyle("-fx-translate-y: -8px; -fx-scale-x: 1.03; -fx-scale-y: 1.03;");
        });
    }

    public StackPane getView() {
        return view;
    }

    public void setGameName(String name) {
        gameName.setText(name);
    }

    public void setGameSource(String source) {
        gameSource.setText(source);
    }

    public void setCoverImage(String url) {
        try {
            if (url != null && !url.isEmpty()) {
                Image image = new Image(url, 200, 200, false, true);
                coverImage.setImage(image);
            }
        } catch (Exception e) {
            // 忽略图片加载失败
        }
    }

    public void setStatus(String status) {
        statusIndicator.setVisible(true);
        if (status != null) {
            switch (status) {
                case "running":
                    statusIndicator.getChildren().get(0).setStyle("-fx-fill: #10B981;");
                    break;
                case "installed":
                    statusIndicator.getChildren().get(0).setStyle("-fx-fill: #3B82F6;");
                    break;
                case "update":
                    statusIndicator.getChildren().get(0).setStyle("-fx-fill: #F59E0B;");
                    break;
            }
        }
    }

    public void setOnClick(javafx.event.EventHandler<javafx.scene.input.MouseEvent> handler) {
        view.setOnMouseClicked(handler);
    }
}
