package org.aurora.launcher.ui.component.dialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.aurora.launcher.modpack.instance.InstanceConfig;
import org.aurora.launcher.ui.component.input.MemorySlider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CreateInstanceDialog extends Dialog<InstanceConfig> {
    
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> versionBox;
    @FXML
    private ComboBox<String> loaderBox;
    @FXML
    private ComboBox<String> loaderVersionBox;
    @FXML
    private MemorySlider memorySlider;
    @FXML
    private Button importButton;
    
    public CreateInstanceDialog() {
        loadFxml();
        setupDialog();
        loadData();
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/CreateInstanceDialog.fxml"));
        loader.setController(this);
        try {
            setDialogPane(new DialogPane());
            getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CreateInstanceDialog.fxml", e);
        }
    }
    
    private void setupDialog() {
        setTitle("创建实例");
        
        ButtonType createButtonType = new ButtonType("创建", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                return buildConfig();
            }
            return null;
        });
        
        Button createButton = (Button) getDialogPane().lookupButton(createButtonType);
        createButton.disableProperty().bind(
            nameField.textProperty().isEmpty()
                .or(versionBox.valueProperty().isNull())
        );
    }
    
    private void loadData() {
        versionBox.getItems().addAll(
            "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20",
            "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19",
            "1.18.2", "1.18.1", "1.18",
            "1.17.1", "1.17",
            "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.16",
            "1.15.2", "1.15.1", "1.15",
            "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14",
            "1.12.2", "1.12.1", "1.12"
        );
        versionBox.getSelectionModel().selectFirst();
        
        loaderBox.getItems().addAll("Vanilla", "Fabric", "Forge", "Quilt");
        loaderBox.getSelectionModel().selectFirst();
        
        loaderBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if ("Vanilla".equals(newVal)) {
                loaderVersionBox.setDisable(true);
                loaderVersionBox.getItems().clear();
            } else {
                loaderVersionBox.setDisable(false);
                loadLoaderVersions(newVal);
            }
        });
        
        versionBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !"Vanilla".equals(loaderBox.getValue())) {
                loadLoaderVersions(loaderBox.getValue());
            }
        });
    }
    
    private void loadLoaderVersions(String loader) {
        loaderVersionBox.getItems().clear();
        loaderVersionBox.getItems().addAll("最新版本", "推荐版本");
    }
    
    private InstanceConfig buildConfig() {
        InstanceConfig config = new InstanceConfig();
        config.setMinecraftVersion(versionBox.getValue());
        
        String loader = loaderBox.getValue();
        if (!"Vanilla".equals(loader)) {
            config.setLoaderType(loader);
            if (loaderVersionBox.getValue() != null) {
                config.setLoaderVersion(loaderVersionBox.getValue());
            }
        }
        
        InstanceConfig.MemoryConfig memoryConfig = new InstanceConfig.MemoryConfig();
        memoryConfig.setMinMB((int) (memorySlider.getMinValue() / (1024 * 1024)));
        memoryConfig.setMaxMB((int) (memorySlider.getValue() / (1024 * 1024)));
        config.setMemory(memoryConfig);
        
        return config;
    }
    
    @FXML
    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择整合包文件");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CurseForge", "*.zip"),
            new FileChooser.ExtensionFilter("Modrinth", "*.mrpack"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        File file = chooser.showOpenDialog(getDialogPane().getScene().getWindow());
        if (file != null) {
            importModpack(file.toPath());
        }
    }
    
    private void importModpack(Path path) {
        // TODO: 实现整合包导入逻辑
    }
}