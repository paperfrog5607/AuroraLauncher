package org.aurora.launcher.ui.component.notification;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;

public class Notification extends HBox {
    
    @FXML
    private ImageView iconView;
    @FXML
    private Label messageLabel;
    @FXML
    private Button closeButton;
    
    private final NotificationType type;
    private Runnable onClose;
    
    public Notification(String message, NotificationType type) {
        this.type = type;
        loadFxml();
        messageLabel.setText(message);
        getStyleClass().addAll("notification", "notification--" + type.name().toLowerCase());
        closeButton.setOnAction(e -> close());
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/Notification.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void show() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> close());
        delay.play();
    }
    
    public void close() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            if (onClose != null) {
                onClose.run();
            }
        });
        fadeOut.play();
    }
    
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }
    
    public NotificationType getType() {
        return type;
    }
}