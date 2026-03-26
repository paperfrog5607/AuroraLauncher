package org.aurora.launcher.ui.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestApp extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Click the button:");
        Button btn = new Button("Click Me");
        btn.setOnAction(e -> {
            label.setText("Button clicked!");
            System.out.println("Button clicked!");
        });
        
        VBox root = new VBox(10, label, btn);
        root.setStyle("-fx-padding: 20; -fx-background-color: #2a2a2a;");
        label.setStyle("-fx-text-fill: white;");
        btn.setStyle("-fx-background-color: #8B5CF6; -fx-text-fill: white;");
        
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Test App");
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}