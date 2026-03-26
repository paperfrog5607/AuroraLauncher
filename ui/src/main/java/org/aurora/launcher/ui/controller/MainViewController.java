package org.aurora.launcher.ui.controller;

import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalTime;
import javafx.util.Duration;
import org.aurora.launcher.ui.AuroraApplication;
import org.aurora.launcher.ui.input.InputHintsOverlay;
import org.aurora.launcher.ui.input.InputManager;
import org.aurora.launcher.ui.input.KeyboardHelpOverlay;
import org.aurora.launcher.ui.input.GamepadHelpOverlay;
import org.aurora.launcher.ui.router.TabRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class MainViewController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);

    @FXML
    private HBox topBar;

    @FXML
    private HBox bottomNav;

    @FXML
    private ComboBox<String> versionBox;

    @FXML
    private ComboBox<String> accountBox;

    @FXML
    private HBox recentGames;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox homeContent;

    @FXML
    private Button playButton;

    @FXML
    private Button navMenu;

    @FXML
    private Button navHome;

    @FXML
    private Button navLaunch;
    
    @FXML
    private Button navDownload;
    
    @FXML
    private Button navSettings;
    
    @FXML
    private Button navCreator;

    @FXML
    private Button navProfile;
    
    @FXML
    private Button navGame;
    
    @FXML
    private Button navStore;
    
    @FXML
    private Button navCommunity;
    
    @FXML
    private Button navNetwork;
    
    @FXML
    private Button notificationBtn;
    
    @FXML
    private Label notificationBadge;
    
    @FXML
    private VBox notificationCenter;
    
    @FXML
    private Label timeLabel;
    
    @FXML
    private HBox gameGrid;
    
    @FXML
    private Label quickLaunchGame;
    
    @FXML
    private Label quickLaunchInfo;

    private double dragStartX;
    private double dragStartY;
    private boolean isTopBarShown = false;

    @Override
    protected void onInitialize() {
        loadVersions();
        loadAccounts();
        loadRecentGames();
        setupNavButtons();
        setupWindowDrag();
        setupInputHints();
        startTimeUpdate();
        
        // 延迟加载游戏网格（等待 FXML 注入完成）
        Platform.runLater(this::loadGameGrid);
    }
    
    private void loadGameGrid() {
        logger.info("loadGameGrid called, gameGrid = {}", gameGrid);
        if (gameGrid == null) {
            logger.warn("gameGrid is null, skipping game grid load");
            return;
        }
        
        gameGrid.getChildren().clear();
        
        // TODO: 从 Steam + Minecraft 加载游戏列表
        // 目前添加示例游戏卡片
        for (int i = 0; i < 6; i++) {
            VBox card = createGameCard("游戏 " + (i + 1), i == 2);
            gameGrid.getChildren().add(card);
        }
        
        logger.info("Loaded {} game cards", gameGrid.getChildren().size());
    }
    
    private VBox createGameCard(String name, boolean selected) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("game-card");
        if (selected) {
            card.getStyleClass().add("selected");
        }
        card.setAlignment(javafx.geometry.Pos.CENTER);
        
        // 游戏图标
        Label icon = new Label("🎮");
        icon.setStyle("-fx-font-size: 48px;");
        
        // 游戏名称
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("game-card-name");
        
        // 游戏信息
        Label infoLabel = new Label("游玩 10 小时");
        infoLabel.getStyleClass().add("game-card-info");
        
        card.getChildren().addAll(icon, nameLabel, infoLabel);
        
        card.setOnMouseClicked(e -> {
            gameGrid.getChildren().forEach(n -> n.getStyleClass().remove("selected"));
            card.getStyleClass().add("selected");
        });
        
        return card;
    }
    
    private void startTimeUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeLabel != null) {
                LocalTime now = LocalTime.now();
                timeLabel.setText(String.format("%02d:%02d", now.getHour(), now.getMinute()));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void setupInputHints() {
        Platform.runLater(() -> {
            Scene scene = topBar.getScene();
            if (scene != null && scene.getRoot() instanceof AnchorPane) {
                AnchorPane root = (AnchorPane) scene.getRoot();
                
                InputHintsOverlay hintsOverlay = InputHintsOverlay.getInstance();
                hintsOverlay.initialize(root);
                hintsOverlay.showKeyboardHints(false);
                
                KeyboardHelpOverlay helpOverlay = KeyboardHelpOverlay.getInstance();
                helpOverlay.initialize(root);
                
                GamepadHelpOverlay gamepadHelpOverlay = GamepadHelpOverlay.getInstance();
                gamepadHelpOverlay.initialize(root);
                
                InputManager.getInstance().setHintsOverlay(hintsOverlay);
                InputManager.getInstance().setHelpOverlay(helpOverlay);
                InputManager.getInstance().setGamepadHelpOverlay(gamepadHelpOverlay);
                
                scene.widthProperty().addListener((obs, oldVal, newVal) -> {
                    hintsOverlay.updatePosition();
                    helpOverlay.updatePosition();
                    gamepadHelpOverlay.updatePosition();
                });
                scene.heightProperty().addListener((obs, oldVal, newVal) -> {
                    hintsOverlay.updatePosition();
                });
            }
        });
    }

    private void setupWindowResize() {
    }
    
    private void setupNavButtonHover(Button button) {
        if (button == null) return;

        button.setOnMouseEntered(e -> {
            animateNavButton(button, true);
        });

        button.setOnMouseExited(e -> {
            animateNavButton(button, false);
        });
    }
    
    private void animateNavButton(Button button, boolean selected) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), button);
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        
        if (selected) {
            tt.setToY(-20);
            st.setToX(1.15);
            st.setToY(1.15);
            button.setOpacity(1);
        } else {
            tt.setToY(0);
            st.setToX(1.0);
            st.setToY(1.0);
            button.setOpacity(0.6);
        }
        
        ParallelTransition pt = new ParallelTransition(tt, st);
        pt.play();
    }
    
    private void animateNavButtonClick(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setOnFinished(e -> {
            ScaleTransition st2 = new ScaleTransition(Duration.millis(100), button);
            st2.setToX(1.0);
            st2.setToY(1.0);
            st2.play();
        });
        st.play();
    }
    
    private void setupKeyboardNavigation() {
        Platform.runLater(() -> {
            Scene scene = topBar.getScene();
            if (scene != null) {
                scene.setOnKeyPressed(this::handleKeyboardNav);
            }
        });
    }
    
    private void handleKeyboardNav(KeyEvent event) {
        boolean isKeyboardMode = InputManager.getInstance().getCurrentMode() == InputManager.InputMode.KEYBOARD;
        
        switch (event.getCode()) {
            case TAB:
                if (isKeyboardMode) {
                    if (event.isShiftDown()) {
                        switchNavGroup(-1);
                    } else {
                        switchNavGroup(1);
                    }
                    event.consume();
                }
                break;
            case DIGIT1:
            case NUMPAD1:
                if (isKeyboardMode) {
                    selectNavButton(0);
                    event.consume();
                }
                break;
            case DIGIT2:
            case NUMPAD2:
                if (isKeyboardMode) {
                    selectNavButton(1);
                    event.consume();
                }
                break;
            case DIGIT3:
            case NUMPAD3:
                if (isKeyboardMode) {
                    selectNavButton(2);
                    event.consume();
                }
                break;
            case DIGIT4:
            case NUMPAD4:
                if (isKeyboardMode) {
                    selectNavButton(3);
                    event.consume();
                }
                break;
            case F1:
                if (isKeyboardMode) {
                    if (KeyboardHelpOverlay.getInstance().isShowing()) {
                        KeyboardHelpOverlay.getInstance().hide();
                    } else {
                        KeyboardHelpOverlay.getInstance().show();
                    }
                    event.consume();
                }
                break;
            case ESCAPE:
                if (KeyboardHelpOverlay.getInstance().isShowing()) {
                    KeyboardHelpOverlay.getInstance().hide();
                    event.consume();
                }
                break;
            default:
                InputManager.getInstance().handleKeyPress(event);
                break;
        }
    }
    
    private int currentGroup = 0;
    private int[][] navGroups = {
        {0, 1, 2, 3},
        {4, 5, 6, 0}
    };
    private String[] tabIds = {"settings", "launch", "download", "settings", "game", "store", "community", "creator"};
    private String[] navNames = {"菜单", "首页", "下载", "用户", "游戏", "商城", "社区"};
    private Button[] allNavButtons;
    private String[] allNavNames;

    private void setupNavButtons() {
        initNavData();
        currentGroup = 0;
        showNavGroupInstant();
        
        if (navHome != null) {
            navHome.getStyleClass().add("active");
        }
        
        if (navMenu != null) setupNavButtonHover(navMenu);
        if (navHome != null) setupNavButtonHover(navHome);
        if (navDownload != null) setupNavButtonHover(navDownload);
        if (navProfile != null) setupNavButtonHover(navProfile);
        if (navGame != null) setupNavButtonHover(navGame);
        if (navStore != null) setupNavButtonHover(navStore);
        if (navCommunity != null) setupNavButtonHover(navCommunity);
        if (navCreator != null) setupNavButtonHover(navCreator);
        
        setupKeyboardNavigation();
    }
    
    private void initNavData() {
        allNavButtons = new Button[]{navMenu, navHome, navDownload, navProfile, navGame, navStore, navCommunity, navCreator};
        allNavNames = new String[]{"菜单", "首页", "下载", "用户", "游戏", "商城", "社区", "制作"};
    }
    
    private void showNavGroupInstant() {
        if (bottomNav == null) return;
        bottomNav.getChildren().clear();
        
        Button[] currentButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            int actualIndex = navGroups[currentGroup][i];
            currentButtons[i] = allNavButtons[actualIndex];
        }
        
        for (int i = 0; i < 4; i++) {
            Button btn = currentButtons[i];
            if (btn != null) {
                btn.setRotate(0);
                btn.setScaleY(1);
                btn.setOpacity(1);
                bottomNav.getChildren().add(btn);
            }
        }
    }
    
    private void animateNavGroupFlip() {
        if (bottomNav == null) return;
        
        bottomNav.getChildren().clear();
        
        Button[] currentButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            int actualIndex = navGroups[currentGroup][i];
            currentButtons[i] = allNavButtons[actualIndex];
        }
        
        for (int i = 0; i < 4; i++) {
            Button btn = currentButtons[i];
            if (btn != null) {
                btn.setOpacity(0);
                btn.setRotate(-90);
                bottomNav.getChildren().add(btn);
                
                final int index = i;
                javafx.animation.RotateTransition rotateIn = new javafx.animation.RotateTransition(Duration.millis(500), btn);
                rotateIn.setDelay(Duration.millis(80 * index));
                rotateIn.setAxis(new javafx.geometry.Point3D(0, 1, 0));
                rotateIn.setFromAngle(-90);
                rotateIn.setToAngle(0);
                
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.millis(300), btn);
                fadeIn.setDelay(Duration.millis(80 * index));
                fadeIn.setToValue(1);
                
                rotateIn.play();
                fadeIn.play();
            }
        }
    }
    
    private void selectNavButton(int localIndex) {
        int[] currentGroupIndices = navGroups[currentGroup];
        if (localIndex >= currentGroupIndices.length) return;
        
        int actualIndex = currentGroupIndices[localIndex];
        Button button = allNavButtons[actualIndex];
        if (button == null) return;
        
        animateNavButtonClick(button);
        
        for (int i = 0; i < bottomNav.getChildren().size(); i++) {
            if (i == localIndex) {
                animateNavButton(button, true);
            } else {
                Button otherBtn = allNavButtons[navGroups[currentGroup][i]];
                if (otherBtn != null) animateNavButton(otherBtn, false);
            }
        }
        
        String tabId = tabIds[actualIndex];
        if (router != null && tabId != null) {
            router.switchTab(tabId);
        }
        
        logger.info("导航到: {}", navNames[actualIndex]);
    }
    
    private void switchNavGroup(int direction) {
        currentGroup += direction;
        if (currentGroup < 0) currentGroup = navGroups.length - 1;
        if (currentGroup >= navGroups.length) currentGroup = 0;
        
        animateNavGroupFlip();
    }
    
    private void setupWindowDrag() {
        if (topBar == null) return;

        topBar.setOnMousePressed((MouseEvent e) -> {
            Stage stage = AuroraApplication.getInstance().getPrimaryStage();
            if (stage != null) {
                dragStartX = e.getScreenX() - stage.getX();
                dragStartY = e.getScreenY() - stage.getY();
            }
        });

        topBar.setOnMouseDragged((MouseEvent e) -> {
            Stage stage = AuroraApplication.getInstance().getPrimaryStage();
            if (stage != null) {
                stage.setX(e.getScreenX() - dragStartX);
                stage.setY(e.getScreenY() - dragStartY);
            }
        });
    }

    private void loadVersions() {
        if (versionBox == null) return;
        versionBox.getItems().addAll(
            "Minecraft 1.21.4",
            "Minecraft 1.21.3",
            "Minecraft 1.20.6",
            "Minecraft 1.19.4",
            "Minecraft 1.18.2"
        );
        versionBox.getSelectionModel().selectFirst();
    }

    private void loadAccounts() {
        if (accountBox == null) return;
        accountBox.getItems().addAll(
            "玩家 (离线)",
            "Steve (离线)",
            "Alex (离线)"
        );
        accountBox.getSelectionModel().selectFirst();
    }

    private void loadRecentGames() {
        if (recentGames == null) return;
        recentGames.getChildren().clear();
        
        List<String> recentVersions = Arrays.asList("1.21.4", "1.20.6", "1.19.4", "1.18.2", "1.16.5");
        for (String version : recentVersions) {
            VBox card = createRecentCard(version);
            recentGames.getChildren().add(card);
        }
    }

    private VBox createRecentCard(String version) {
        VBox card = new VBox();
        card.setSpacing(4);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("recent-card");
        card.setOnMouseClicked(e -> {
            if (versionBox != null) {
                versionBox.setValue("Minecraft " + version);
            }
            logger.info("Selected recent version: {}", version);
        });

        Label icon = new Label("🎮");
        icon.getStyleClass().add("recent-icon");

        Label name = new Label(version);
        name.getStyleClass().add("recent-name");

        card.getChildren().addAll(icon, name);
        return card;
    }

    @FXML
    private void onLaunch() {
        String selectedVersion = versionBox != null ? versionBox.getValue() : "Minecraft 1.21.4";
        String selectedAccount = accountBox != null ? accountBox.getValue() : "玩家 (离线)";
        logger.info("Launching game: {}, account: {}", selectedVersion, selectedAccount);
    }
    
    @FXML
    private void onLaunchTab() {
        clearNavActive();
        if (navLaunch != null) navLaunch.getStyleClass().add("active");
        
        // 显示主页内容
        contentArea.getChildren().clear();
        if (homeContent != null) {
            contentArea.getChildren().add(homeContent);
        }
        
        showTopBar();
        logger.info("Show home content");
    }
    
    @FXML
    private void onSettingsTab() {
        clearNavActive();
        navSettings.getStyleClass().add("active");
        contentArea.getChildren().clear();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsView.fxml"), resources);
            Node content = loader.load();
            contentArea.getChildren().add(content);
            showTopBar();
            hideBottomNavWithAnimation();
            logger.info("Loaded settings view");
        } catch (Exception e) {
            logger.error("Failed to load settings view", e);
        }
    }
    
    @FXML
    private void onNetworkTab() {
        clearNavActive();
        navNetwork.getStyleClass().add("active");
        contentArea.getChildren().clear();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NetworkView.fxml"), resources);
            Node content = loader.load();
            contentArea.getChildren().add(content);
            showTopBar();
            hideBottomNavWithAnimation();
            logger.info("Loaded network view");
        } catch (Exception e) {
            logger.error("Failed to load network view", e);
        }
    }
    
    @FXML
    private void onQuickLaunch() {
        logger.info("Quick launch clicked");
        onLaunch();
    }
    
    @FXML
    private void onVersionSelect() {
        logger.info("Version select clicked");
    }
    
    @FXML
    private void onMenuToggle() {
        if (router != null) {
            router.switchTab("settings");
        }
    }

    @FXML
    public void onHomeTab() {
        clearNavActive();
        navHome.getStyleClass().add("active");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(homeContent);
        showTopBar();
        showBottomNavWithAnimation();
    }

    public void hideBottomNavWithAnimation() {
        if (bottomNav == null) return;
        FadeTransition ft = new FadeTransition(Duration.millis(200), bottomNav);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            bottomNav.setVisible(false);
            bottomNav.setManaged(false);
        });
        ft.play();
    }

    public void showBottomNavWithAnimation() {
        if (bottomNav == null) return;
        bottomNav.setVisible(true);
        bottomNav.setManaged(true);
        FadeTransition ft = new FadeTransition(Duration.millis(200), bottomNav);
        ft.setToValue(1);
        ft.play();
    }

    @FXML
    private void onDownloadTab() {
        try {
            clearNavActive();
            navDownload.getStyleClass().add("active");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DownloadView.fxml"), resources);
            Node content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
            showTopBar();
            hideBottomNavWithAnimation();
            logger.info("Loaded download view");
        } catch (Exception e) {
            logger.error("Failed to load download view", e);
        }
    }

    @FXML
    public void onCreatorTab() {
        clearNavActive();
        navCreator.getStyleClass().add("active");
        contentArea.getChildren().clear();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreatorView.fxml"), resources);
            Parent creatorView = loader.load();
            CreatorController creatorController = loader.getController();
            creatorController.setMainController(this);
            creatorController.setRouter(router);
            
            contentArea.getChildren().add(creatorView);
            
            hideBottomNavWithAnimation();
            
        } catch (IOException e) {
            logger.error("Failed to load CreatorView", e);
        }
        
        showTopBar();
    }

    public void showTopBar() {
        if (topBar == null) return;
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), topBar);
        slideIn.setToY(0);
        slideIn.play();
        isTopBarShown = true;
    }

    public void hideTopBar() {
        if (topBar == null) return;
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), topBar);
        slideOut.setToY(-50);
        slideOut.play();
        isTopBarShown = false;
    }
    
    private void clearNavActive() {
        if (navLaunch != null) navLaunch.getStyleClass().remove("active");
        if (navDownload != null) navDownload.getStyleClass().remove("active");
        if (navCreator != null) navCreator.getStyleClass().remove("active");
        if (navNetwork != null) navNetwork.getStyleClass().remove("active");
        if (navSettings != null) navSettings.getStyleClass().remove("active");
    }

    @FXML
    private void onMinimize() {
        AuroraApplication app = AuroraApplication.getInstance();
        if (app != null && app.getPrimaryStage() != null) {
            app.getPrimaryStage().setIconified(true);
        }
    }

    @FXML
    private void onMaximize() {
        AuroraApplication app = AuroraApplication.getInstance();
        if (app != null && app.getPrimaryStage() != null) {
            boolean isMaximized = app.getPrimaryStage().isMaximized();
            app.getPrimaryStage().setMaximized(!isMaximized);
        }
    }

    @FXML
    private void onClose() {
        AuroraApplication app = AuroraApplication.getInstance();
        if (app != null && app.getPrimaryStage() != null) {
            app.getPrimaryStage().close();
        }
    }

    @FXML
    private void onNotification() {
        toggleNotificationCenter();
    }
    
    @FXML
    private void onNotificationClick() {
        toggleNotificationCenter();
    }
    
    private boolean notificationCenterVisible = false;
    
    private void toggleNotificationCenter() {
        if (notificationCenter == null) return;
        
        notificationCenterVisible = !notificationCenterVisible;
        
        if (notificationCenterVisible) {
            notificationCenter.setVisible(true);
            notificationCenter.setManaged(true);
        } else {
            notificationCenter.setVisible(false);
            notificationCenter.setManaged(false);
        }
    }
    
    public void updateNotificationBadge(int count) {
        if (notificationBadge != null) {
            if (count > 0) {
                notificationBadge.setText(String.valueOf(count > 99 ? "99+" : count));
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
        }
    }

    @FXML
    private void onTopBarMouseEnter() {
        if (topBar == null) return;
        
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), topBar);
        slideIn.setToY(0);
        slideIn.play();
        isTopBarShown = true;
    }

    @FXML
    private void onTopBarMouseExit() {
        if (topBar == null) return;

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), topBar);
        slideOut.setToY(-50);
        slideOut.play();
        isTopBarShown = false;
    }
}