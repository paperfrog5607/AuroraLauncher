package org.aurora.launcher.ui.component.notification;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import java.util.LinkedList;
import java.util.Queue;

public class NotificationManager {
    
    private static NotificationManager instance;
    private final StackPane container;
    private final VBox notificationsBox;
    private final int maxVisible = 3;
    private final Queue<Notification> queue = new LinkedList<>();
    
    private NotificationManager(StackPane container) {
        this.container = container;
        this.notificationsBox = createNotificationsBox();
        container.getChildren().add(notificationsBox);
    }
    
    public static void initialize(StackPane container) {
        instance = new NotificationManager(container);
    }
    
    public static NotificationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NotificationManager not initialized. Call initialize() first.");
        }
        return instance;
    }
    
    private VBox createNotificationsBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_RIGHT);
        box.setPadding(new Insets(10));
        StackPane.setAlignment(box, Pos.TOP_RIGHT);
        return box;
    }
    
    public void show(String message, NotificationType type) {
        Platform.runLater(() -> {
            Notification notification = new Notification(message, type);
            
            if (notificationsBox.getChildren().size() >= maxVisible) {
                queue.offer(notification);
            } else {
                display(notification);
            }
        });
    }
    
    private void display(Notification notification) {
        notificationsBox.getChildren().add(notification);
        
        notification.setOnClose(() -> {
            notificationsBox.getChildren().remove(notification);
            
            if (!queue.isEmpty()) {
                display(queue.poll());
            }
        });
        
        notification.show();
    }
    
    public void info(String message) {
        show(message, NotificationType.INFO);
    }
    
    public void success(String message) {
        show(message, NotificationType.SUCCESS);
    }
    
    public void warning(String message) {
        show(message, NotificationType.WARNING);
    }
    
    public void error(String message) {
        show(message, NotificationType.ERROR);
    }
    
    public void clear() {
        notificationsBox.getChildren().clear();
        queue.clear();
    }
}