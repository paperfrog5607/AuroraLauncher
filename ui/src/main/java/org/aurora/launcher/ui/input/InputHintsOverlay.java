package org.aurora.launcher.ui.input;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InputHintsOverlay {
    private static final Logger logger = LoggerFactory.getLogger(InputHintsOverlay.class);
    
    private static InputHintsOverlay instance;
    
    private AnchorPane parent;
    private VBox hintsContainer;
    private List<HintItem> hintItems = new ArrayList<>();
    
    private boolean isAnimating = false;
    private int currentHintIndex = 0;
    private long lastHintTime = 0;
    private static final long HINT_INTERVAL = 200;
    
    private double containerWidth = 120;
    private double containerHeight = 180;
    private double positionX = 16;
    private double positionY = 80;
    
    public static InputHintsOverlay getInstance() {
        if (instance == null) {
            instance = new InputHintsOverlay();
        }
        return instance;
    }
    
    private InputHintsOverlay() {}
    
    public void initialize(AnchorPane parent) {
        this.parent = parent;
        
        hintsContainer = new VBox();
        hintsContainer.setSpacing(2);
        hintsContainer.setAlignment(Pos.CENTER_RIGHT);
        hintsContainer.setPadding(new Insets(4, 8, 4, 8));
        hintsContainer.setBackground(new Background(new BackgroundFill(
            Color.rgb(15, 23, 42, 0.9),
            new CornerRadii(4),
            Insets.EMPTY
        )));
        
        hintsContainer.setOpacity(1);
        hintsContainer.setVisible(false); // TEMPORARILY DISABLED
        hintsContainer.setMouseTransparent(true);
        
        parent.getChildren().add(hintsContainer);
        
        updatePosition();
        
        logger.info("InputHintsOverlay initialized");
    }
    
    public void updatePosition() {
        if (parent == null || hintsContainer == null) return;
        
        double x = parent.getWidth() - 250;
        double y = parent.getHeight() - 240;
        
        hintsContainer.setLayoutX(x);
        hintsContainer.setLayoutY(y);
    }
    
    public void showKeyboardHints(boolean animate) {
        hintItems.clear();
        
        ShortcutManager sm = ShortcutManager.getInstance();
        
        hintItems.add(new HintItem("[1-4]", "导航"));
        hintItems.add(new HintItem(formatKey(sm.getKeybind("fullscreen")), "全屏"));
        hintItems.add(new HintItem(formatKey(sm.getKeybind("refresh")), "刷新"));
        hintItems.add(new HintItem(formatKey(sm.getKeybind("help")), "帮助"));
        hintItems.add(new HintItem(formatKey(sm.getKeybind("close")), "返回"));
        hintItems.add(new HintItem(formatKey(sm.getKeybind("confirm")), "确认"));
        
        buildHintUI();
        
        if (animate) {
            hintsContainer.setOpacity(1);
            for (HintItem item : hintItems) {
                item.row.setOpacity(0);
                item.row.setTranslateX(50);
            }
            startIntroAnimation();
        } else {
            hintsContainer.setOpacity(1);
            for (HintItem item : hintItems) {
                item.row.setOpacity(1);
                item.row.setTranslateX(0);
            }
        }
    }
    
    private String formatKey(ShortcutManager.Keybind keybind) {
        if (keybind == null) return "?";
        String key = keybind.getDisplayString();
        if (key.equals("ARROWS")) return "[↑↓←→]";
        if (key.equals("HJKL")) return "[HJKL]";
        if (key.equals("Shift+G")) return "[S+G]";
        return "[" + key + "]";
    }
    
    public void showGamepadHints(boolean animate) {
        hintItems.clear();
        
        hintItems.add(new HintItem("[Ctrl+K]", "切换键盘"));
        hintItems.add(new HintItem("[Start]", "全屏"));
        hintItems.add(new HintItem("[L3]", "导航"));
        hintItems.add(new HintItem("[A]", "选定"));
        hintItems.add(new HintItem("[B]", "确认"));
        hintItems.add(new HintItem("[RB]", "下一项"));
        hintItems.add(new HintItem("[LB]", "上一项"));
        
        buildHintUI();
        
        if (animate) {
            hintsContainer.setOpacity(1);
            for (HintItem item : hintItems) {
                item.row.setOpacity(0);
                item.row.setTranslateX(50);
            }
            startIntroAnimation();
        } else {
            hintsContainer.setOpacity(1);
            for (HintItem item : hintItems) {
                item.row.setOpacity(1);
                item.row.setTranslateX(0);
            }
        }
    }
    
    public void showMouseHints(boolean animate) {
        hintItems.clear();
        
        hintItems.add(new HintItem("[Ctrl+K]", "切换键盘"));
        hintItems.add(new HintItem("[F11]", "全屏"));
        hintItems.add(new HintItem("[鼠标]", "导航"));
        hintItems.add(new HintItem("[单击]", "选定"));
        hintItems.add(new HintItem("[双击]", "确认"));
        hintItems.add(new HintItem("[ESCx2]", "关闭"));
        
        buildHintUI();
        
        if (animate) {
            hintsContainer.setOpacity(1);
            for (HintItem item : hintItems) {
                item.row.setOpacity(0);
                item.row.setTranslateX(50);
            }
            startIntroAnimation();
        } else {
            hintsContainer.setOpacity(1);
            for (HintItem item : hintItems) {
                item.row.setOpacity(1);
                item.row.setTranslateX(0);
            }
        }
    }
    
    private void buildHintUI() {
        hintsContainer.getChildren().clear();
        
        for (HintItem item : hintItems) {
            HBox row = new HBox();
            row.setSpacing(6);
            row.setPadding(new Insets(4, 8, 4, 8));
            row.setMinWidth(180);
            row.setMaxWidth(180);
            
            Label keyLabel = new Label(item.key);
            keyLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #60A5FA;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Consolas', monospace;" +
                "-fx-padding: 2 6 2 6;" +
                "-fx-background-color: #1E293B;" +
                "-fx-background-radius: 4;" +
                "-fx-border-color: #3B82F6;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 4;" +
                "-fx-min-width: 50;"
            );
            
            Label actionLabel = new Label(item.action);
            actionLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #E2E8F0;" +
                "-fx-font-family: 'Segoe UI', sans-serif;" +
                "-fx-hgrow: ALWAYS;"
            );
            
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            row.getChildren().addAll(keyLabel, spacer, actionLabel);
            item.row = row;
            hintsContainer.getChildren().add(row);
        }
    }
    
    private void startIntroAnimation() {
        if (isAnimating) return;
        isAnimating = true;
        currentHintIndex = 0;
        lastHintTime = 0;
        
        hintsContainer.setOpacity(1);
        hintsContainer.setTranslateX(0);
        
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentHintIndex >= hintItems.size()) {
                    stop();
                    isAnimating = false;
                    return;
                }
                
                if (now - lastHintTime >= HINT_INTERVAL * 1_000_000) {
                    lastHintTime = now;
                    animateHintIn(hintItems.get(currentHintIndex));
                    currentHintIndex++;
                }
            }
        };
        timer.start();
    }
    
    private void animateHintIn(HintItem item) {
        if (item.row == null) return;
        
        item.row.setOpacity(0);
        item.row.setScaleX(0.8);
        item.row.setScaleY(0.8);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), item.row);
        scale.setToX(1);
        scale.setToY(1);
        scale.play();
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), item.row);
        fade.setToValue(1);
        fade.play();
    }
    
    public void hide() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), hintsContainer);
        slideOut.setToX(containerWidth);
        slideOut.play();
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), hintsContainer);
        fadeOut.setToValue(0);
        fadeOut.play();
    }
    
    public void show() {
        hintsContainer.setOpacity(1);
        hintsContainer.setTranslateX(0);
    }
    
    private static class HintItem {
        String action;
        String key;
        String icon;
        HBox row;
        
        HintItem(String key, String action) {
            this.key = key;
            this.action = action;
            this.icon = null;
        }
        
        HintItem(String icon, String key, String action) {
            this.icon = icon;
            this.key = key;
            this.action = action;
        }
    }
}