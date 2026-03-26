package org.aurora.launcher.ui.controller.creator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.aurora.launcher.ui.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModpackCreateController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ModpackCreateController.class);

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> versionBox;

    @FXML
    private ComboBox<String> loaderBox;

    @FXML
    private ComboBox<String> loaderVersionBox;

    @Override
    protected void onInitialize() {
        setupLoaderSelection();
    }

    private void setupLoaderSelection() {
        loaderBox.getItems().addAll(
            "Vanilla", "Fabric", "Forge", "Quilt", "NeoForge"
        );
        loaderBox.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, newVal) -> {
                updateLoaderVersions(newVal);
            });
    }

    private void updateLoaderVersions(String loader) {
        loaderVersionBox.getItems().clear();
        loaderVersionBox.setDisable(false);

        if (loader == null) {
            loaderVersionBox.setDisable(true);
            return;
        }

        switch (loader) {
            case "Fabric" -> loaderVersionBox.getItems().addAll("0.15.1", "0.16.1", "0.17.0");
            case "Forge" -> loaderVersionBox.getItems().addAll("49.0.30", "47.2.0", "45.0.0");
            case "NeoForge" -> loaderVersionBox.getItems().addAll("20.4.200", "21.0.100");
            case "Quilt" -> loaderVersionBox.getItems().addAll("0.26.0", "0.25.0");
            default -> loaderVersionBox.setDisable(true);
        }
    }

    @FXML
    private void onCreate() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Modpack name is empty");
            return;
        }
        logger.info("Creating modpack: {}", name);
    }

    @FXML
    private void onCancel() {
        switchTab("launch");
    }
}
