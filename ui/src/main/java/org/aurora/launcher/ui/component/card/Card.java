package org.aurora.launcher.ui.component.card;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;

public class Card extends VBox {
    
    @FXML
    private HBox headerArea;
    @FXML
    private Label titleLabel;
    @FXML
    private HBox headerButtons;
    @FXML
    private StackPane expandIcon;
    @FXML
    private VBox contentArea;
    
    private final BooleanProperty expanded = new SimpleBooleanProperty(true);
    private final BooleanProperty canToggle = new SimpleBooleanProperty(true);
    private final StringProperty title = new SimpleStringProperty("");
    
    public Card() {
        loadFxml();
        setupAnimation();
    }
    
    public Card(String title) {
        this();
        setTitle(title);
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/Card.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Card.fxml", e);
        }
    }
    
    private void setupAnimation() {
        expandIcon.visibleProperty().bind(canToggle);
        expandIcon.setOnMouseClicked(e -> toggle());
        
        headerArea.setOnMouseClicked(e -> {
            if (e.getTarget() == headerArea || e.getTarget() == titleLabel) {
                toggle();
            }
        });
        
        expanded.addListener((obs, old, newVal) -> {
            RotateTransition rotate = new RotateTransition(Duration.millis(200), expandIcon);
            rotate.setToAngle(newVal ? 0 : 180);
            rotate.play();
            
            if (newVal) {
                expand(contentArea);
            } else {
                collapse(contentArea);
            }
        });
        
        titleLabel.textProperty().bind(title);
    }
    
    private void expand(VBox content) {
        content.setVisible(true);
        content.setManaged(true);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), content);
        scale.setFromY(0);
        scale.setToY(1);
        scale.play();
    }
    
    private void collapse(VBox content) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), content);
        scale.setFromY(1);
        scale.setToY(0);
        scale.setOnFinished(e -> {
            content.setVisible(false);
            content.setManaged(false);
        });
        scale.play();
    }
    
    public void toggle() {
        if (canToggle.get()) {
            expanded.set(!expanded.get());
        }
    }
    
    public void setTitle(String title) {
        this.title.set(title);
    }
    
    public String getTitle() {
        return title.get();
    }
    
    public void setContent(Node content) {
        contentArea.getChildren().setAll(content);
    }
    
    public void addContent(Node... nodes) {
        contentArea.getChildren().addAll(nodes);
    }
    
    public void clearContent() {
        contentArea.getChildren().clear();
    }
    
    public void addHeaderButton(Node button) {
        headerButtons.getChildren().add(button);
    }
    
    public void removeHeaderButton(Node button) {
        headerButtons.getChildren().remove(button);
    }
    
    public VBox getContentArea() {
        return contentArea;
    }
    
    public HBox getHeaderButtons() {
        return headerButtons;
    }
    
    public BooleanProperty expandedProperty() {
        return expanded;
    }
    
    public boolean isExpanded() {
        return expanded.get();
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded.set(expanded);
    }
    
    public BooleanProperty canToggleProperty() {
        return canToggle;
    }
    
    public boolean isCanToggle() {
        return canToggle.get();
    }
    
    public void setCanToggle(boolean canToggle) {
        this.canToggle.set(canToggle);
    }
    
    public StringProperty titleProperty() {
        return title;
    }
}