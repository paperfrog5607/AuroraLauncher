package org.aurora.launcher.ui.input;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GamepadHelpOverlay {
    private static final Logger logger = LoggerFactory.getLogger(GamepadHelpOverlay.class);
    
    private static GamepadHelpOverlay instance;
    
    private AnchorPane parent;
    private VBox helpPanel;
    private boolean isShowing = false;
    
    public static GamepadHelpOverlay getInstance() {
        if (instance == null) {
            instance = new GamepadHelpOverlay();
        }
        return instance;
    }
    
    private GamepadHelpOverlay() {}
    
    public void initialize(AnchorPane parent) {
        this.parent = parent;
        buildUI();
        logger.info("GamepadHelpOverlay initialized");
    }
    
    public void show() {
        if (helpPanel == null || isShowing) return;
        
        isShowing = true;
        recenterPanel();
        
        helpPanel.setVisible(true);
        helpPanel.setOpacity(0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), helpPanel);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    public void hide() {
        if (helpPanel == null || !isShowing) return;
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), helpPanel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            isShowing = false;
            helpPanel.setVisible(false);
        });
        fadeOut.play();
    }
    
    public boolean isShowing() {
        return isShowing;
    }
    
    public void updatePosition() {
        if (helpPanel != null && isShowing) {
            recenterPanel();
        }
    }
    
    private void recenterPanel() {
        if (parent == null || helpPanel == null) return;
        double panelWidth = 500;
        double parentWidth = parent.getWidth();
        double x = (parentWidth - panelWidth) / 2;
        helpPanel.setLayoutX(Math.max(0, x));
    }
    
    private void buildUI() {
        helpPanel = new VBox();
        helpPanel.setVisible(false);
        helpPanel.setMouseTransparent(true);
        
        double panelWidth = 500;
        
        helpPanel.setLayoutX((parent.getWidth() - panelWidth) / 2);
        helpPanel.setLayoutY(20);
        helpPanel.setMinWidth(panelWidth);
        helpPanel.setMaxWidth(panelWidth);
        helpPanel.setPrefWidth(panelWidth);
        
        helpPanel.setBackground(new Background(new BackgroundFill(
            Color.rgb(15, 23, 42, 0.98),
            new CornerRadii(0, 0, 12, 12, false),
            Insets.EMPTY
        )));
        
        VBox headerBox = createHeader();
        VBox controllerView = createControllerView();
        HBox hintsBox = createHintsBox();
        
        helpPanel.getChildren().addAll(headerBox, controllerView, hintsBox);
        
        parent.getChildren().add(helpPanel);
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.setMinHeight(44);
        header.setMaxHeight(44);
        header.setPrefHeight(44);
        header.setPadding(new Insets(0, 16, 0, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1E293B;");
        
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setMinHeight(44);
        titleRow.setMaxHeight(44);
        
        Label icon = new Label("\uD83C\uDFAE");
        icon.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-text-fill: #60A5FA;" +
            "-fx-padding: 0 10 0 0;"
        );
        
        Label title = new Label("\u624B\u67c4\u5feb\u901f\u952e");
        title.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #F1F5F9;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
        
        titleRow.getChildren().addAll(icon, title);
        header.getChildren().add(titleRow);
        
        return header;
    }
    
    private VBox createControllerView() {
        VBox controllerView = new VBox();
        controllerView.setSpacing(15);
        controllerView.setPadding(new Insets(20, 20, 10, 20));
        controllerView.setAlignment(Pos.CENTER);
        
        HBox topRow = new HBox();
        topRow.setSpacing(30);
        topRow.setAlignment(Pos.CENTER);
        
        VBox leftStickBox = createControllerGroup(
            new String[]{"L3", "\u5de6\u6447\u6746", "LEFT STICK"},
            new String[]{"\u4e0a\u4e0b\u5de6\u53f3", "\u5bfc\u822a"},
            true
        );
        
        VBox dpadBox = createControllerGroup(
            new String[]{"\u2191", "\u2193", "\u2190", "\u2192", "D-PAD"},
            new String[]{"\u4e0a\u4e0b\u5de6\u53f3", "D-PAD"},
            true
        );
        
        VBox centerBox = createControllerGroup(
            new String[]{"Start", "Guide", "Select"},
            new String[]{"\u5168\u5c4f/\u9009\u9879", "\u83dc\u5355", "\u8fd4\u56de"},
            false
        );
        
        VBox rightStickBox = createControllerGroup(
            new String[]{"R3", "\u53f3\u6447\u6746"},
            new String[]{"\u786e\u8ba4"},
            true
        );
        
        VBox abxyBox = createABXYGroup();
        
        topRow.getChildren().addAll(leftStickBox, dpadBox, centerBox, rightStickBox, abxyBox);
        
        controllerView.getChildren().add(topRow);
        
        return controllerView;
    }
    
    private VBox createControllerGroup(String[] keys, String[] actions, boolean isHighlighted) {
        VBox group = new VBox();
        group.setSpacing(5);
        group.setAlignment(Pos.CENTER);
        
        HBox keysBox = new HBox();
        keysBox.setSpacing(3);
        keysBox.setAlignment(Pos.CENTER);
        
        String bgColor = isHighlighted ? "#3B82F6" : "#334155";
        String textColor = isHighlighted ? "#FFFFFF" : "#E2E8F0";
        
        for (String key : keys) {
            Label keyLabel = new Label(key);
            keyLabel.setMinWidth(40);
            keyLabel.setPrefWidth(40);
            keyLabel.setMinHeight(35);
            keyLabel.setPrefHeight(35);
            keyLabel.setAlignment(Pos.CENTER);
            keyLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Consolas', monospace;" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 6;"
            );
            keysBox.getChildren().add(keyLabel);
        }
        
        Label actionLabel = new Label(actions.length > 1 ? actions[1] : actions[0]);
        actionLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #94A3B8;"
        );
        
        group.getChildren().addAll(keysBox, actionLabel);
        
        return group;
    }
    
    private VBox createABXYGroup() {
        VBox group = new VBox();
        group.setSpacing(5);
        group.setAlignment( Pos.CENTER);
        
        HBox topRow = new HBox();
        topRow.setSpacing(15);
        topRow.setAlignment(Pos.CENTER);
        
        Label yLabel = new Label("Y");
        yLabel.setMinWidth(35);
        yLabel.setPrefWidth(35);
        yLabel.setMinHeight(35);
        yLabel.setPrefHeight(35);
        yLabel.setAlignment(Pos.CENTER);
        yLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-background-color: #EAB308;" +
            "-fx-background-radius: 17;"
        );
        
        Label xLabel = new Label("X");
        xLabel.setMinWidth(35);
        xLabel.setPrefWidth(35);
        xLabel.setMinHeight(35);
        xLabel.setPrefHeight(35);
        xLabel.setAlignment(Pos.CENTER);
        xLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-background-color: #3B82F6;" +
            "-fx-background-radius: 17;"
        );
        
        Label bLabel = new Label("B");
        bLabel.setMinWidth(35);
        bLabel.setPrefWidth(35);
        bLabel.setMinHeight(35);
        bLabel.setPrefHeight(35);
        bLabel.setAlignment(Pos.CENTER);
        bLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-background-color: #EF4444;" +
            "-fx-background-radius: 17;"
        );
        
        Label aLabel = new Label("A");
        aLabel.setMinWidth(35);
        aLabel.setPrefWidth(35);
        aLabel.setMinHeight(35);
        aLabel.setPrefHeight(35);
        aLabel.setAlignment(Pos.CENTER);
        aLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-background-color: #22C55E;" +
            "-fx-background-radius: 17;"
        );
        
        topRow.getChildren().addAll(yLabel, xLabel);
        
        HBox bottomRow = new HBox();
        bottomRow.setSpacing(15);
        bottomRow.setAlignment(Pos.CENTER);
        
        Label label1 = new Label("");
        label1.setMinWidth(35);
        label1.setPrefWidth(35);
        
        bottomRow.getChildren().addAll(label1, bLabel, aLabel);
        
        VBox abxyLabels = new VBox();
        abxyLabels.setSpacing(3);
        abxyLabels.setAlignment(Pos.CENTER);
        
        HBox labelsRow = new HBox();
        labelsRow.setSpacing(48);
        labelsRow.setAlignment(Pos.CENTER);
        
        Label yLabel2 = new Label("\u4e0a");
        yLabel2.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        Label xLabel2 = new Label("\u5de6");
        xLabel2.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        Label bLabel2 = new Label("\u53f3");
        bLabel2.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        Label aLabel2 = new Label("\u4e0b");
        aLabel2.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        
        labelsRow.getChildren().addAll(yLabel2, xLabel2, bLabel2, aLabel2);
        
        Label actionLabel = new Label("\u786e\u8ba4/\u9009\u62e9");
        actionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");
        
        abxyLabels.getChildren().addAll(topRow, bottomRow, labelsRow, actionLabel);
        
        return abxyLabels;
    }
    
    private HBox createHintsBox() {
        HBox hintsBox = new HBox();
        hintsBox.setMinHeight(50);
        hintsBox.setMaxHeight(50);
        hintsBox.setPrefHeight(50);
        hintsBox.setPadding(new Insets(10, 20, 10, 20));
        hintsBox.setStyle("-fx-background-color: #1E293B;");
        hintsBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox col1 = new VBox();
        col1.setSpacing(4);
        col1.setAlignment(Pos.CENTER_LEFT);
        
        Label line1 = new Label("L3/R3 \u6447\u6746\u6309\u4e0b  |  Start \u5168\u5c4f  |  Select \u9009\u9879  |  Guide \u83dc\u5355");
        line1.setStyle("-fx-font-size: 12px; -fx-text-fill: #E2E8F0; -fx-font-family: 'Segoe UI', sans-serif;");
        
        Label line2 = new Label("RB/LB \u4e0a\u4e0b\u9879  |  A \u786e\u8ba4  |  B \u8fd4\u56de  |  X/Y \u5176\u4ed6");
        line2.setStyle("-fx-font-size: 12px; -fx-text-fill: #E2E8F0; -fx-font-family: 'Segoe UI', sans-serif;");
        
        col1.getChildren().addAll(line1, line2);
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        hintsBox.getChildren().addAll(col1, spacer);
        
        return hintsBox;
    }
}
