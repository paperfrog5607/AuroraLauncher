package org.aurora.launcher.ui.input;

import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardHelpOverlay {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardHelpOverlay.class);
    
    private static KeyboardHelpOverlay instance;
    
    private AnchorPane parent;
    private VBox helpPanel;
    private boolean isShowing = false;
    
    public static KeyboardHelpOverlay getInstance() {
        if (instance == null) {
            instance = new KeyboardHelpOverlay();
        }
        return instance;
    }
    
    private KeyboardHelpOverlay() {}
    
    public void initialize(AnchorPane parent) {
        this.parent = parent;
        buildUI();
        logger.info("KeyboardHelpOverlay initialized");
    }
    
    public void show() {
        if (helpPanel == null || isShowing) return;
        
        isShowing = true;
        
        recenterPanel();
        
        helpPanel.setVisible(true);
        helpPanel.setTranslateY(-getPanelHeight());
        helpPanel.setOpacity(1);
        
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), helpPanel);
        slideIn.setToY(0);
        slideIn.play();
    }
    
    private void recenterPanel() {
        if (parent == null || helpPanel == null) return;
        double panelWidth = 680;
        double parentWidth = parent.getWidth();
        double x = (parentWidth - panelWidth) / 2;
        helpPanel.setLayoutX(Math.max(0, x));
    }
    
    public void hide() {
        if (helpPanel == null || !isShowing) return;
        
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), helpPanel);
        slideOut.setToY(-getPanelHeight());
        slideOut.play();
        
        slideOut.setOnFinished(e -> {
            isShowing = false;
            helpPanel.setVisible(false);
        });
    }
    
    public boolean isShowing() {
        return isShowing;
    }
    
    public void updatePosition() {
        if (helpPanel != null && isShowing) {
            recenterPanel();
        }
    }
    
    private double getPanelHeight() {
        return 420;
    }
    
    private void buildUI() {
        helpPanel = new VBox();
        helpPanel.setVisible(false);
        helpPanel.setMouseTransparent(true);
        
        double panelWidth = 680;
        double panelHeight = getPanelHeight();
        
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
        java.util.Set<String> highlightedKeys = getHighlightedKeys();
        GridPane keyboardGrid = createKeyboardGrid(highlightedKeys);
        HBox hintsBox = createHintsBox(highlightedKeys);
        
        helpPanel.getChildren().addAll(headerBox, keyboardGrid, hintsBox);
        
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
        
        Label icon = new Label("\u2328");
        icon.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-text-fill: #60A5FA;" +
            "-fx-padding: 0 10 0 0;"
        );
        
        Label title = new Label("\u952e\u76d8\u5feb\u6377\u952e");
        title.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #F1F5F9;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Button closeBtn = new Button("\u00d7");
        closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #64748B;" +
            "-fx-font-size: 20px;" +
            "-fx-padding: 8 12;" +
            "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> hide());
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
            "-fx-background-color: #EF4444;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 20px;" +
            "-fx-padding: 8 12;" +
            "-fx-cursor: hand;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #64748B;" +
            "-fx-font-size: 20px;" +
            "-fx-padding: 8 12;" +
            "-fx-cursor: hand;"
        ));
        
        titleRow.getChildren().addAll(icon, title, spacer, closeBtn);
        header.getChildren().add(titleRow);
        
        return header;
    }
    
    private java.util.Set<String> getHighlightedKeys() {
        java.util.Set<String> keys = new java.util.HashSet<>();
        ShortcutManager sm = ShortcutManager.getInstance();
        
        if (sm.getKeybind("fullscreen") != null) {
            keys.add(sm.getKeybind("fullscreen").getPrimary());
        }
        if (sm.getKeybind("help") != null) {
            keys.add(sm.getKeybind("help").getPrimary());
        }
        if (sm.getKeybind("refresh") != null) {
            keys.add(sm.getKeybind("refresh").getPrimary());
        }
        if (sm.getKeybind("search") != null) {
            keys.add("/");
        }
        if (sm.getKeybind("navGroup1") != null) {
            keys.add("1");
        }
        if (sm.getKeybind("navGroup2") != null) {
            keys.add("2");
        }
        if (sm.getKeybind("navGroup3") != null) {
            keys.add("3");
        }
        if (sm.getKeybind("navGroup4") != null) {
            keys.add("4");
        }
        if (sm.getKeybind("close") != null) {
            keys.add(sm.getKeybind("close").getPrimary());
        }
        if (sm.getKeybind("navigate") != null) {
            String nav = sm.getKeybind("navigate").getPrimary();
            if (nav.equals("ARROWS")) {
                keys.add("UP");
                keys.add("DOWN");
                keys.add("LEFT");
                keys.add("RIGHT");
            }
        }
        if (sm.getKeybind("navigateHJKL") != null) {
            keys.add("H");
            keys.add("J");
            keys.add("K");
            keys.add("L");
        }
        if (sm.getKeybind("next") != null) {
            keys.add(sm.getKeybind("next").getPrimary());
        }
        if (sm.getKeybind("previous") != null) {
            keys.add(sm.getKeybind("previous").getPrimary());
        }
        if (sm.getKeybind("confirm") != null) {
            keys.add(sm.getKeybind("confirm").getPrimary());
        }
        if (sm.getKeybind("select") != null) {
            keys.add(sm.getKeybind("select").getPrimary());
        }
        if (sm.getKeybind("jumpFirst") != null) {
            keys.add(sm.getKeybind("jumpFirst").getPrimary());
        }
        if (sm.getKeybind("jumpLast") != null) {
            keys.add("Shift");
            keys.add("G");
        }
        
        return keys;
    }
    
    private GridPane createKeyboardGrid(java.util.Set<String> highlightedKeys) {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(16, 20, 12, 20));
        grid.setAlignment(Pos.CENTER);
        
        int row = 0;
        
        Label escKey = createKey("ESC", highlightedKeys.contains("ESC"));
        Label f1Key = createKey("F1", highlightedKeys.contains("F1"));
        Label f2Key = createKey("F2", false);
        Label f3Key = createKey("F3", false);
        Label f4Key = createKey("F4", false);
        Label f5Key = createKey("F5", false);
        Label f6Key = createKey("F6", false);
        Label f7Key = createKey("F7", false);
        Label f8Key = createKey("F8", false);
        Label f9Key = createKey("F9", false);
        Label f10Key = createKey("F10", false);
        Label f11Key = createKey("F11", highlightedKeys.contains("F11"));
        Label f12Key = createKey("F12", false);
        
        grid.addRow(row++, escKey, f1Key, f2Key, f3Key, f4Key, f5Key, f6Key, f7Key, f8Key, f9Key, f10Key, f11Key, f12Key);
        
        Label tildeKey = createKey("~", false);
        Label num1Key = createKey("1", highlightedKeys.contains("1"));
        Label num2Key = createKey("2", highlightedKeys.contains("2"));
        Label num3Key = createKey("3", highlightedKeys.contains("3"));
        Label num4Key = createKey("4", highlightedKeys.contains("4"));
        Label num5Key = createKey("5", false);
        Label num6Key = createKey("6", false);
        Label num7Key = createKey("7", false);
        Label num8Key = createKey("8", false);
        Label num9Key = createKey("9", false);
        Label num0Key = createKey("0", false);
        Label minusKey = createKey("-", false);
        Label equalsKey = createKey("=", false);
        Label backspaceKey = createKey("\u2190 Backspace", false);
        
        grid.addRow(row++, tildeKey, num1Key, num2Key, num3Key, num4Key, num5Key, num6Key, num7Key, num8Key, num9Key, num0Key, minusKey, equalsKey, backspaceKey);
        
        Label tabKey = createKey("Tab", highlightedKeys.contains("Tab"));
        Label qKey = createKey("Q", false);
        Label wKey = createKey("W", false);
        Label eKey = createKey("E", false);
        Label rKey = createKey("R", highlightedKeys.contains("R"));
        Label tKey = createKey("T", false);
        Label yKey = createKey("Y", false);
        Label uKey = createKey("U", false);
        Label iKey = createKey("I", false);
        Label oKey = createKey("O", false);
        Label pKey = createKey("P", false);
        Label lbracketKey = createKey("[", false);
        Label rbracketKey = createKey("]", false);
        Label backslashKey = createKey("\\", false);
        
        grid.addRow(row++, tabKey, qKey, wKey, eKey, rKey, tKey, yKey, uKey, iKey, oKey, pKey, lbracketKey, rbracketKey, backslashKey);
        
        Label capsKey = createKey("Caps", false);
        Label aKey = createKey("A", false);
        Label sKey = createKey("S", false);
        Label dKey = createKey("D", false);
        Label fKey = createKey("F", false);
        Label gKey = createKey("G", highlightedKeys.contains("G"));
        Label hKey = createKey("H", highlightedKeys.contains("H"));
        Label jKey = createKey("J", highlightedKeys.contains("J"));
        Label kKey = createKey("K", highlightedKeys.contains("K"));
        Label lKey = createKey("L", highlightedKeys.contains("L"));
        Label semicolonKey = createKey(";", false);
        Label quoteKey = createKey("'", false);
        Label enterKey = createKey("Enter", highlightedKeys.contains("Enter"));
        
        grid.addRow(row++, capsKey, aKey, sKey, dKey, fKey, gKey, hKey, jKey, kKey, lKey, semicolonKey, quoteKey, enterKey);
        
        Label lshiftKey = createKey("Shift", highlightedKeys.contains("Shift"));
        Label zKey = createKey("Z", false);
        Label xKey = createKey("X", false);
        Label cKey = createKey("C", false);
        Label vKey = createKey("V", false);
        Label bKey = createKey("B", false);
        Label nKey = createKey("N", false);
        Label mKey = createKey("M", false);
        Label commaKey = createKey(",", false);
        Label periodKey = createKey(".", false);
        Label slashKey = createKey("/", highlightedKeys.contains("/"));
        Label rshiftKey = createKey("Shift", highlightedKeys.contains("Shift"));
        
        grid.addRow(row++, lshiftKey, zKey, xKey, cKey, vKey, bKey, nKey, mKey, commaKey, periodKey, slashKey, rshiftKey);
        
        Label arrowUpKey = createKey("\u2191", highlightedKeys.contains("UP"));
        Label arrowLeftKey = createKey("\u2190", highlightedKeys.contains("LEFT"));
        Label arrowDownKey = createKey("\u2193", highlightedKeys.contains("DOWN"));
        Label arrowRightKey = createKey("\u2192", highlightedKeys.contains("RIGHT"));
        Label fnKey = createKey("Fn", false);
        Label insertKey = createKey("Insert", false);
        Label homeKey = createKey("Home", false);
        Label pgupKey = createKey("PgUp", false);
        Label deleteKey = createKey("Delete", false);
        Label endKey = createKey("End", false);
        Label pgdnKey = createKey("PgDn", false);
        
        grid.addRow(row++, arrowUpKey, arrowLeftKey, arrowDownKey, arrowRightKey, fnKey, insertKey, homeKey, pgupKey, deleteKey, endKey, pgdnKey);
        
        Label ctrlKey = createKey("Ctrl", false);
        Label winKey = createKey("\u2318", false);
        Label altKey = createKey("Alt", false);
        Label spaceKey = createKey("Space", false);
        Label alt2Key = createKey("Alt", false);
        Label win2Key = createKey("\u2318", false);
        Label menuKey = createKey("\u2630", false);
        Label ctrl2Key = createKey("Ctrl", false);
        
        grid.addRow(row++, ctrlKey, winKey, altKey, spaceKey, alt2Key, win2Key, menuKey, ctrl2Key);
        
        return grid;
    }
    
    private Label createKey(String text, boolean highlighted) {
        Label key = new Label(text);
        key.setAlignment(Pos.CENTER);
        key.setMinWidth(44);
        key.setPrefWidth(44);
        key.setMinHeight(38);
        key.setPrefHeight(38);
        
        if (highlighted) {
            key.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Consolas', monospace;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-background-color: #3B82F6;" +
                "-fx-background-radius: 6;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.4), 4, 0, 0, 2);"
            );
        } else {
            key.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Consolas', monospace;" +
                "-fx-text-fill: #E2E8F0;" +
                "-fx-background-color: #334155;" +
                "-fx-background-radius: 6;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 2, 0, 0, 1);"
            );
        }
        
        return key;
    }
    
    private HBox createHintsBox(java.util.Set<String> highlightedKeys) {
        HBox hintsBox = new HBox();
        hintsBox.setMinHeight(90);
        hintsBox.setMaxHeight(90);
        hintsBox.setPrefHeight(90);
        hintsBox.setPadding(new Insets(10, 20, 10, 20));
        hintsBox.setStyle("-fx-background-color: #1E293B;");
        hintsBox.setAlignment(Pos.CENTER_LEFT);
        
        ShortcutManager sm = ShortcutManager.getInstance();
        
        VBox col1 = new VBox();
        col1.setSpacing(4);
        col1.setAlignment(Pos.CENTER_LEFT);
        
        StringBuilder line1Builder = new StringBuilder();
        appendKeybind(line1Builder, sm.getKeybind("close"), "\u8fd4\u56de");
        line1Builder.append("  |  ");
        appendKeybind(line1Builder, sm.getKeybind("fullscreen"), "\u5168\u5c4f");
        line1Builder.append("  |  ");
        appendKeybind(line1Builder, sm.getKeybind("refresh"), "\u5237\u65b0");
        line1Builder.append("  |  ");
        appendKeybind(line1Builder, sm.getKeybind("search"), "\u641c\u7d22");
        line1Builder.append("  |  ");
        appendKeybind(line1Builder, sm.getKeybind("help"), "\u5e2e\u52a9");
        Label line1 = new Label(line1Builder.toString());
        line1.setStyle("-fx-font-size: 12px; -fx-text-fill: #E2E8F0; -fx-font-family: 'Segoe UI', sans-serif;");
        
        StringBuilder line2Builder = new StringBuilder();
        appendKeybind(line2Builder, sm.getKeybind("navigate"), "\u5bfc\u822a");
        line2Builder.append("  |  ");
        appendKeybind(line2Builder, sm.getKeybind("navigateHJKL"), "\u5feb\u901f\u5bfc\u822a");
        Label line2 = new Label(line2Builder.toString());
        line2.setStyle("-fx-font-size: 12px; -fx-text-fill: #E2E8F0; -fx-font-family: 'Segoe UI', sans-serif;");
        
        StringBuilder line3Builder = new StringBuilder();
        appendKeybind(line3Builder, sm.getKeybind("select"), "\u9009\u5b9a");
        line3Builder.append("  |  ");
        appendKeybind(line3Builder, sm.getKeybind("confirm"), "\u786e\u8ba4");
        line3Builder.append("  |  ");
        appendKeybind(line3Builder, sm.getKeybind("jumpFirst"), "\u9996\u9879");
        line3Builder.append("  |  ");
        appendKeybind(line3Builder, sm.getKeybind("jumpLast"), "\u672b\u9879");
        Label line3 = new Label(line3Builder.toString());
        line3.setStyle("-fx-font-size: 12px; -fx-text-fill: #E2E8F0; -fx-font-family: 'Segoe UI', sans-serif;");
        
        StringBuilder line4Builder = new StringBuilder();
        appendKeybind(line4Builder, sm.getKeybind("navGroup1"), "\u83dc\u5355");
        line4Builder.append("  |  ");
        appendKeybind(line4Builder, sm.getKeybind("navGroup2"), "\u9996\u9875");
        line4Builder.append("  |  ");
        appendKeybind(line4Builder, sm.getKeybind("navGroup3"), "\u4e0b\u8f7d");
        line4Builder.append("  |  ");
        appendKeybind(line4Builder, sm.getKeybind("navGroup4"), "\u8bbe\u7f6e");
        line4Builder.append("  |  Tab \u5207\u6362\u7ec4");
        Label line4 = new Label(line4Builder.toString());
        line4.setStyle("-fx-font-size: 12px; -fx-text-fill: #60A5FA; -fx-font-family: 'Segoe UI', sans-serif;");
        
        col1.getChildren().addAll(line1, line2, line3, line4);
        
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        hintsBox.getChildren().addAll(col1, spacer);
        
        return hintsBox;
    }
    
    private void appendKeybind(StringBuilder sb, ShortcutManager.Keybind keybind, String action) {
        if (keybind == null) return;
        String key = keybind.getPrimary();
        if (key.equals("ARROWS")) {
            sb.append("\u2191\u2193\u2190\u2192");
        } else if (key.equals("HJKL")) {
            sb.append("H J K L");
        } else if (key.equals("Shift+G")) {
            sb.append("Shift+G");
        } else {
            sb.append(key);
        }
        if (action != null) {
            sb.append(" ").append(action);
        }
    }
}
