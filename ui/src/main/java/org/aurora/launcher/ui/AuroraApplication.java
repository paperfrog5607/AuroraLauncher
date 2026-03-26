package org.aurora.launcher.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.aurora.launcher.ui.input.InputManager;
import org.aurora.launcher.ui.router.TabRouter;
import org.aurora.launcher.ui.service.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuroraApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(AuroraApplication.class);
    private static AuroraApplication instance;
    private Stage primaryStage;
    private TabRouter router;
    private ResourceBundle resources;
    private boolean isFullScreen = false;

    @Override
    public void init() throws Exception {
        super.init();
        resources = ResourceBundle.getBundle("i18n.messages", Locale.CHINESE);
    }

    @Override
    public void start(Stage stage) {
        try {
            System.err.println("=== AuroraApplication.start() BEGIN ===");
            instance = this;
            this.primaryStage = stage;
            
            System.err.println("Calling initializeServices()...");
            initializeServices();
            System.err.println("initializeServices() complete");
            
            System.err.println("Calling configureStage()...");
            configureStage(stage);
            System.err.println("configureStage() complete");
            
            System.err.println("Calling showMainWindow()...");
            showMainWindow();
            System.err.println("showMainWindow() complete");
            System.err.println("=== AuroraApplication.start() END ===");
        } catch (Throwable t) {
            System.err.println("=== THROWABLE IN start() ===");
            System.err.println("Message: " + t.getMessage());
            t.printStackTrace(System.err);
            System.err.println("=== END THROWABLE IN start() ===");
            throw new RuntimeException(t);
        }
    }

    private void initializeServices() {
        try {
            System.err.println("ServiceLocator.initialize() starting...");
            ServiceLocator.initialize();
            System.err.println("ServiceLocator.initialize() complete");
            logger.info("Services initialized");
        } catch (Exception e) {
            System.err.println("=== ERROR IN initializeServices() ===");
            e.printStackTrace(System.err);
            throw e;
        }
    }

    private void configureStage(Stage stage) {
        stage.setTitle("Aurora Launcher");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.initStyle(StageStyle.UNDECORATED);
        
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/AuroraLauncher.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            logger.warn("Failed to load application icon", e);
        }
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = (screenBounds.getWidth() - 1280) / 2;
        double y = Math.max(0, (screenBounds.getHeight() - 800) / 2);
        stage.setX(screenBounds.getMinX() + x);
        stage.setY(screenBounds.getMinY() + y);
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(screenBounds.getWidth());
        stage.setMaxHeight(screenBounds.getHeight());
        logger.info("Window positioned at ({}, {}), size: 1280x800", stage.getX(), stage.getY());
    }

    private void showMainWindow() {
        try {
            logger.info("Loading MainView.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"), resources);
            Parent root = loader.load();
            logger.info("FXML loaded successfully");
            
            Scene scene = new Scene(root, 1280, 800);
            scene.getStylesheets().addAll(
                getClass().getResource("/css/theme-dark.css").toExternalForm(),
                getClass().getResource("/css/main.css").toExternalForm(),
                getClass().getResource("/css/button.css").toExternalForm(),
                getClass().getResource("/css/card.css").toExternalForm(),
                getClass().getResource("/css/input.css").toExternalForm(),
                getClass().getResource("/css/notification.css").toExternalForm()
            );
            logger.info("Scene configured");
            
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F11) {
                    toggleFullScreen();
                    event.consume();
                } else if (event.getCode() == KeyCode.ESCAPE && isFullScreen) {
                    exitFullScreen();
                    event.consume();
                } else if (event.isControlDown() && event.getCode() == KeyCode.F) {
                    logger.info("Ctrl+F pressed - search triggered");
                    event.consume();
                }
            });
            
            primaryStage.setScene(scene);
            primaryStage.show();
            
            router = new TabRouter(scene, resources);
            
            InputManager.getInstance().initialize(scene);
            
            logger.info("Main window initialized");
        } catch (Exception e) {
            logger.error("Failed to load main view", e);
            System.err.println("=== STARTUP ERROR ===");
            e.printStackTrace(System.err);
            System.err.println("=== END STARTUP ERROR ===");
            throw new RuntimeException("Failed to load main view", e);
        }
    }
    
    public void toggleFullScreen() {
        if (primaryStage != null) {
            isFullScreen = !isFullScreen;
            primaryStage.setFullScreen(isFullScreen);
            logger.info("Fullscreen toggled: {}", isFullScreen);
        }
    }
    
    public void exitFullScreen() {
        if (primaryStage != null && isFullScreen) {
            isFullScreen = false;
            primaryStage.setFullScreen(false);
            logger.info("Exited fullscreen");
        }
    }
    
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public void stop() throws Exception {
        ServiceLocator.shutdown();
        logger.info("Application stopped");
    }

    public static AuroraApplication getInstance() {
        return instance;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public TabRouter getRouter() {
        return router;
    }

    public ResourceBundle getResources() {
        return resources;
    }

    public static void main(String[] args) {
        launch(args);
    }
}