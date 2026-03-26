package org.aurora.launcher.ui.router;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.i18n.I18nManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TabRouter {
    private static final Logger logger = LoggerFactory.getLogger(TabRouter.class);
    private static final Duration ANIMATION_DURATION = Duration.millis(200);
    private static TabRouter instance;

    private final Scene scene;
    private final ResourceBundle resources;
    private final Map<String, TabRoute> routes = new LinkedHashMap<>();
    private String currentTab;
    private String currentSubTab;
    private boolean isAnimating = false;

    public TabRouter(Scene scene) {
        this(scene, null);
    }

    public TabRouter(Scene scene, ResourceBundle resources) {
        this.scene = scene;
        this.resources = resources;
        instance = this;
        registerRoutes();
    }

    public static TabRouter getInstance() {
        return instance;
    }

    private void registerRoutes() {
        route("launch", "LaunchController", "/fxml/LaunchView.fxml");

        TabRoute downloadRoute = route("download", "DownloadController", "/fxml/DownloadView.fxml");
        downloadRoute.subTab("version", "VersionCardController", "/fxml/download/VersionCardView.fxml")
            .subTab("mod", "DownloadModController", "/fxml/download/ModView.fxml")
            .subTab("modpack", "DownloadModpackController", "/fxml/download/ModpackView.fxml")
            .subTab("resource", "DownloadResourceController", "/fxml/download/ResourceView.fxml")
            .subTab("shader", "DownloadShaderController", "/fxml/download/ShaderView.fxml");

        TabRoute settingsRoute = route("settings", "SettingsController", "/fxml/SettingsView.fxml");
        settingsRoute.subTab("launch", "SettingsLaunchController", "/fxml/settings/LaunchView.fxml")
            .subTab("download", "SettingsDownloadController", "/fxml/settings/DownloadView.fxml")
            .subTab("theme", "SettingsThemeController", "/fxml/settings/ThemeView.fxml")
            .subTab("account", "SettingsAccountController", "/fxml/settings/AccountView.fxml")
            .subTab("advanced", "SettingsAdvancedController", "/fxml/settings/AdvancedView.fxml")
            .subTab("file", "SettingsFileController", "/fxml/settings/FileView.fxml")
            .subTab("help", "SettingsHelpController", "/fxml/settings/HelpView.fxml");

        route("creator", "CreatorController", "/fxml/CreatorView.fxml");

        logger.info("Registered {} routes", routes.size());
    }

    public void switchTab(String tabId) {
        switchTab(tabId, null);
    }

    public void switchTab(String tabId, String subTabId) {
        TabRoute route = routes.get(tabId);
        if (route == null) {
            logger.warn("Route not found: {}", tabId);
            return;
        }

        try {
            logger.info("Switching to tab: {}, fxml: {}", tabId, route.getFxml());
            updateTabSelection(tabId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(route.getFxml()));
            if (resources != null) {
                loader.setResources(resources);
            } else {
                I18nManager i18n = I18nManager.getInstance();
                if (i18n != null && i18n.getBundle() != null) {
                    loader.setResources(i18n.getBundle());
                }
            }

            Parent content = loader.load();
            logger.info("FXML loaded successfully");

            BaseController controller = loader.getController();
            logger.info("Controller: {}", controller);
            if (controller != null) {
                controller.setRouter(this);
            }

            BorderPane mainView = findBorderPane(scene.getRoot());
            if (mainView != null) {
                animateContentChange(mainView, content);
            } else {
                logger.error("Could not find BorderPane in scene root");
            }

            currentTab = tabId;
            logger.debug("Switched to tab: {}", tabId);

            if (subTabId != null) {
                switchSubTab(subTabId);
            } else if (route.getDefaultSubTab() != null) {
                switchSubTab(route.getDefaultSubTab());
            }

        } catch (IOException e) {
            logger.error("Failed to switch tab: {}", tabId, e);
            throw new RuntimeException("Failed to switch tab: " + tabId, e);
        }
    }

    public void switchSubTab(String subTabId) {
        TabRoute route = routes.get(currentTab);
        if (route == null) {
            logger.warn("No current tab set");
            return;
        }

        SubTabRoute subRoute = route.getSubTabs().get(subTabId);
        if (subRoute == null) {
            logger.warn("Sub route not found: {}", subTabId);
            return;
        }

        try {
            System.err.println("=== TabRouter.switchSubTab(" + subTabId + ") START ===");
            logger.info("Switching to sub tab: {}, fxml: {}", subTabId, subRoute.getFxml());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(subRoute.getFxml()));
            if (resources != null) {
                loader.setResources(resources);
            } else {
                I18nManager i18n = I18nManager.getInstance();
                if (i18n != null && i18n.getBundle() != null) {
                    loader.setResources(i18n.getBundle());
                }
            }

            System.err.println("Loading FXML...");
            Parent content = loader.load();
            System.err.println("FXML loaded, content=" + content);
            logger.info("Sub tab FXML loaded successfully");

            BaseController controller = loader.getController();
            System.err.println("Controller from loader: " + controller);
            logger.info("Sub tab controller: {}", controller);
            if (controller != null) {
                controller.setRouter(this);
            }
            System.err.println("=== TabRouter.switchSubTab(" + subTabId + ") END ===");

            BorderPane mainRoot = findBorderPane(scene.getRoot());
            Node centerNode = mainRoot != null ? mainRoot.getCenter() : null;
            logger.debug("Main center node type: {}", centerNode != null ? centerNode.getClass().getSimpleName() : "null");
            
            Pane targetContainer = null;
            
            if (centerNode instanceof StackPane stackPane) {
                targetContainer = stackPane;
            } else if (centerNode instanceof BorderPane borderPane) {
                Node innerCenter = borderPane.getCenter();
                if (innerCenter instanceof StackPane stackPane) {
                    targetContainer = stackPane;
                } else if (innerCenter instanceof Pane pane) {
                    targetContainer = pane;
                } else {
                    targetContainer = borderPane;
                }
            } else if (centerNode instanceof Pane pane) {
                targetContainer = pane;
            }
            
            if (targetContainer != null) {
                if (targetContainer instanceof StackPane stackPane) {
                    logger.info("StackPane children before clear: {}", stackPane.getChildren().size());
                    stackPane.getChildren().clear();
                    stackPane.getChildren().add(content);
                    logger.info("StackPane children after add: {}, content={}", stackPane.getChildren().size(), content.getClass().getSimpleName());
                } else if (targetContainer instanceof BorderPane borderPane) {
                    borderPane.setCenter(content);
                } else {
                    targetContainer.getChildren().clear();
                    targetContainer.getChildren().add(content);
                }
                logger.debug("Content added to: {}", targetContainer.getClass().getSimpleName());
            } else {
                logger.error("Could not find target container for sub tab content");
            }

            currentSubTab = subTabId;
            logger.debug("Switched to sub tab: {}", subTabId);

        } catch (IOException e) {
            logger.error("Failed to switch sub tab: {}", subTabId, e);
            throw new RuntimeException("Failed to switch sub tab: " + subTabId, e);
        } catch (ClassCastException e) {
            logger.error("Failed to cast center node", e);
            throw new RuntimeException("Failed to switch sub tab: " + subTabId, e);
        }
    }

    private void updateTabSelection(String tabId) {
        HBox tabBar = (HBox) scene.getRoot().lookup("#tabBar");
        if (tabBar == null) {
            return;
        }

        for (Node node : tabBar.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("tab-active");
                if (btn.getId() != null && btn.getId().equals(tabId)) {
                    btn.getStyleClass().add("tab-active");
                }
            }
        }
    }

    private TabRoute route(String id, String controllerName, String fxml) {
        TabRoute tabRoute = new TabRoute(id, controllerName, fxml);
        routes.put(id, tabRoute);
        return tabRoute;
    }

    public String getCurrentTab() {
        return currentTab;
    }

    public String getCurrentSubTab() {
        return currentSubTab;
    }

    public TabRoute getRoute(String tabId) {
        return routes.get(tabId);
    }

    public Map<String, TabRoute> getRoutes() {
        return new LinkedHashMap<>(routes);
    }

    private BorderPane findBorderPane(Node node) {
        if (node instanceof BorderPane) {
            return (BorderPane) node;
        } else if (node instanceof AnchorPane) {
            for (Node child : ((AnchorPane) node).getChildren()) {
                BorderPane result = findBorderPane(child);
                if (result != null) {
                    return result;
                }
            }
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                BorderPane result = findBorderPane(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private void animateContentChange(BorderPane mainView, Parent newContent) {
        if (isAnimating) {
            mainView.setCenter(newContent);
            return;
        }
        
        isAnimating = true;
        
        Node oldContent = mainView.getCenter();
        
        newContent.setOpacity(0);
        newContent.setTranslateX(30);
        mainView.setCenter(newContent);
        
        FadeTransition fadeIn = new FadeTransition(ANIMATION_DURATION, newContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideIn = new TranslateTransition(ANIMATION_DURATION, newContent);
        slideIn.setFromX(30);
        slideIn.setToX(0);
        
        ParallelTransition enterTransition = new ParallelTransition(fadeIn, slideIn);
        
        if (oldContent != null) {
            FadeTransition fadeOut = new FadeTransition(ANIMATION_DURATION, oldContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            
            TranslateTransition slideOut = new TranslateTransition(ANIMATION_DURATION, oldContent);
            slideOut.setFromX(0);
            slideOut.setToX(-30);
            
            ParallelTransition exitTransition = new ParallelTransition(fadeOut, slideOut);
            
            exitTransition.setOnFinished(e -> {
                enterTransition.play();
            });
            
            exitTransition.play();
        } else {
            enterTransition.play();
        }
        
        enterTransition.setOnFinished(e -> {
            isAnimating = false;
        });
    }
}