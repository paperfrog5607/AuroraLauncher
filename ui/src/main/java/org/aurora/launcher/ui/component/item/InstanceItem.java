package org.aurora.launcher.ui.component.item;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.aurora.launcher.modpack.instance.Instance;
import org.aurora.launcher.modpack.instance.Instance.InstanceState;
import org.aurora.launcher.ui.component.common.StatusBadge;
import org.aurora.launcher.ui.event.InstanceEvent;

import java.io.IOException;

public class InstanceItem extends VBox {
    
    @FXML
    private ImageView iconView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private Label loaderLabel;
    @FXML
    private Button launchButton;
    @FXML
    private Button settingsButton;
    @FXML
    private HBox statusBadge;
    
    private final Instance instance;
    
    public InstanceItem(Instance instance) {
        this.instance = instance;
        loadFxml();
        initialize();
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/InstanceItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load InstanceItem.fxml", e);
        }
    }
    
    private void initialize() {
        nameLabel.setText(instance.getName());
        versionLabel.setText(instance.getVersion());
        
        if (instance.getLoader() != null) {
            loaderLabel.setText(instance.getLoader().getType().name());
        } else {
            loaderLabel.setText("Vanilla");
        }
        
        if (instance.getIconPath() != null && !instance.getIconPath().isEmpty()) {
            try {
                iconView.setImage(new Image(instance.getIconPath()));
            } catch (Exception e) {
                setDefaultIcon();
            }
        } else {
            setDefaultIcon();
        }
        
        updateStatus();
        
        launchButton.setOnAction(e -> fireEvent(new InstanceEvent(InstanceEvent.LAUNCH, instance)));
        settingsButton.setOnAction(e -> fireEvent(new InstanceEvent(InstanceEvent.SETTINGS, instance)));
        
        setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2) {
                fireEvent(new InstanceEvent(InstanceEvent.LAUNCH, instance));
            }
        });
    }
    
    private void setDefaultIcon() {
        iconView.setImage(new Image(getClass().getResourceAsStream("/images/icons/default-instance.png")));
    }
    
    private void updateStatus() {
        statusBadge.getChildren().clear();
        
        InstanceState state = instance.getState();
        if (state == null) {
            return;
        }
        
        switch (state) {
            case RUNNING:
                statusBadge.getChildren().add(new StatusBadge("运行中", "status-running"));
                break;
            case UPDATING:
                statusBadge.getChildren().add(new StatusBadge("更新中", "status-updating"));
                break;
            case ERROR:
                statusBadge.getChildren().add(new StatusBadge("错误", "status-error"));
                break;
            default:
                break;
        }
    }
    
    public Instance getInstance() {
        return instance;
    }
    
    public void setOnLaunch(javafx.event.EventHandler<InstanceEvent> handler) {
        addEventHandler(InstanceEvent.LAUNCH, handler);
    }
    
    public void setOnSettings(javafx.event.EventHandler<InstanceEvent> handler) {
        addEventHandler(InstanceEvent.SETTINGS, handler);
    }
}