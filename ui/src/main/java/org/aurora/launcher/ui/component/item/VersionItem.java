package org.aurora.launcher.ui.component.item;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import org.aurora.launcher.launcher.version.VersionInfo;
import org.aurora.launcher.launcher.version.VersionType;
import org.aurora.launcher.ui.event.VersionEvent;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class VersionItem extends HBox {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
    
    @FXML
    private Label versionLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Button downloadButton;
    @FXML
    private MenuButton installButton;
    
    private final VersionInfo version;
    
    public VersionItem(VersionInfo version) {
        this.version = version;
        loadFxml();
        initialize();
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/VersionItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load VersionItem.fxml", e);
        }
    }
    
    private void initialize() {
        versionLabel.setText(version.getId());
        
        VersionType type = version.getType();
        if (type != null) {
            typeLabel.setText(type.name());
            typeLabel.getStyleClass().add("version-type-" + type.name().toLowerCase());
        }
        
        if (version.getReleaseTime() != null) {
            dateLabel.setText(DATE_FORMATTER.format(version.getReleaseTime()));
        }
        
        downloadButton.setOnAction(e -> fireEvent(new VersionEvent(VersionEvent.DOWNLOAD, version)));
        
        MenuItem installFabric = new MenuItem("Install Fabric");
        installFabric.setOnAction(e -> fireEvent(new VersionEvent(VersionEvent.INSTALL_FABRIC, version)));
        
        MenuItem installForge = new MenuItem("Install Forge");
        installForge.setOnAction(e -> fireEvent(new VersionEvent(VersionEvent.INSTALL_FORGE, version)));
        
        installButton.getItems().addAll(installFabric, installForge);
    }
    
    public VersionInfo getVersion() {
        return version;
    }
}