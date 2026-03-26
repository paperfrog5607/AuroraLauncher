package org.aurora.launcher.ui.controller.launch;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import org.aurora.launcher.modpack.instance.Instance;
import org.aurora.launcher.modpack.instance.InstanceManager;
import org.aurora.launcher.ui.controller.CardController;
import org.aurora.launcher.ui.service.ServiceLocator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class InstanceListCard extends CardController {
    
    @FXML
    private FlowPane instanceGrid;
    
    @FXML
    private Button newInstanceButton;
    
    @FXML
    private Button openFolderButton;
    
    private InstanceManager instanceManager;
    
    @Override
    protected void onInitialize() {
        instanceManager = ServiceLocator.get(InstanceManager.class);
        loadInstances();
    }
    
    private void loadInstances() {
        if (instanceManager == null) {
            return;
        }
        
        List<Instance> instances = instanceManager.getAllInstances();
        instanceGrid.getChildren().clear();
    }
    
    @FXML
    private void onNewInstance() {
    }
    
    @FXML
    private void onOpenFolder() {
        if (instanceManager == null) {
            return;
        }
        
        try {
            File instancesDir = instanceManager.getInstancesDir().toFile();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(instancesDir);
            }
        } catch (IOException e) {
            showError(t("error.openFolder"));
        }
    }
    
    private void launchInstance(Instance instance) {
        // TODO: 实现实例启动逻辑
    }
    
    private void openInstanceSettings(Instance instance) {
        if (router != null) {
            router.switchTab("settings");
        }
    }
    
    private void showError(String message) {
    }
}