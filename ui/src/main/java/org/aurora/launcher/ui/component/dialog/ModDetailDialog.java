package org.aurora.launcher.ui.component.dialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.aurora.launcher.mod.search.ModSearchResult;

import java.io.IOException;

public class ModDetailDialog extends Dialog<Void> {
    
    @FXML
    private ImageView iconView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label sourceLabel;
    @FXML
    private Label downloadsLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ListView<String> versionsList;
    @FXML
    private Hyperlink pageLink;
    @FXML
    private Button downloadButton;
    
    private final ModSearchResult result;
    
    public ModDetailDialog(ModSearchResult result) {
        this.result = result;
        loadFxml();
        loadData();
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/ModDetailDialog.fxml"));
        loader.setController(this);
        try {
            setDialogPane(new DialogPane());
            getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load ModDetailDialog.fxml", e);
        }
        
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }
    
    private void loadData() {
        nameLabel.setText(result.getName());
        authorLabel.setText(result.getAuthor());
        sourceLabel.setText(result.getSource() != null ? result.getSource() : "Unknown");
        downloadsLabel.setText(formatDownloads(result.getDownloads()));
        descriptionArea.setText(result.getDescription());
        
        if (result.getIconUrl() != null && !result.getIconUrl().isEmpty()) {
            try {
                iconView.setImage(new Image(result.getIconUrl(), true));
            } catch (Exception e) {
                setDefaultIcon();
            }
        } else {
            setDefaultIcon();
        }
        
        if (result.getPageUrl() != null && !result.getPageUrl().isEmpty()) {
            pageLink.setText(result.getPageUrl());
            pageLink.setOnAction(e -> {
                if (getDialogPane().getScene() != null) {
                    getDialogPane().getScene().getWindow().hide();
                }
                openBrowser(result.getPageUrl());
            });
        } else {
            pageLink.setVisible(false);
        }
        
        loadVersions();
    }
    
    private void setDefaultIcon() {
        iconView.setImage(new Image(getClass().getResourceAsStream("/images/icons/default-mod.png")));
    }
    
    private void loadVersions() {
        versionsList.getItems().clear();
        
        if (result.getVersions() != null && !result.getVersions().isEmpty()) {
            versionsList.getItems().addAll(result.getVersions());
        } else {
            versionsList.getItems().add("暂无版本信息");
        }
    }
    
    private void openBrowser(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            }
        } catch (Exception e) {
            // Ignore browser errors
        }
    }
    
    @FXML
    private void onDownload() {
        String selected = versionsList.getSelectionModel().getSelectedItem();
        if (selected != null && !"暂无版本信息".equals(selected)) {
            // TODO: 实现下载逻辑
        }
    }
    
    private String formatDownloads(long downloads) {
        if (downloads < 1000) {
            return String.valueOf(downloads);
        }
        if (downloads < 1000000) {
            return String.format("%.1fK", downloads / 1000.0);
        }
        return String.format("%.1fM", downloads / 1000000.0);
    }
}