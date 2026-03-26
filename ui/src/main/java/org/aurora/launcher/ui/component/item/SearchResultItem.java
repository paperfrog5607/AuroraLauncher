package org.aurora.launcher.ui.component.item;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import org.aurora.launcher.mod.search.ModSearchResult;
import org.aurora.launcher.ui.component.dialog.ModDetailDialog;
import org.aurora.launcher.ui.event.SearchEvent;

import java.io.IOException;

public class SearchResultItem extends HBox {
    
    @FXML
    private javafx.scene.image.ImageView iconView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label downloadsLabel;
    @FXML
    private HBox tagsBox;
    @FXML
    private Button viewButton;
    @FXML
    private Button downloadButton;
    
    private final ModSearchResult result;
    
    public SearchResultItem(ModSearchResult result) {
        this.result = result;
        loadFxml();
        initialize();
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/SearchResultItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SearchResultItem.fxml", e);
        }
    }
    
    private void initialize() {
        nameLabel.setText(result.getName());
        authorLabel.setText(result.getAuthor());
        descriptionLabel.setText(result.getDescription());
        downloadsLabel.setText(formatDownloads(result.getDownloads()));
        
        if (result.getIconUrl() != null && !result.getIconUrl().isEmpty()) {
            try {
                iconView.setImage(new Image(result.getIconUrl(), true));
            } catch (Exception e) {
                setDefaultIcon();
            }
        } else {
            setDefaultIcon();
        }
        
        tagsBox.getChildren().clear();
        if (result.getSource() != null && !result.getSource().isEmpty()) {
            tagsBox.getChildren().add(createTag(result.getSource(), "tag-source"));
        }
        if (result.getCategories() != null && !result.getCategories().isEmpty()) {
            tagsBox.getChildren().add(createTag(result.getCategories().get(0), "tag-category"));
        }
        
        viewButton.setOnAction(e -> showDetail());
        downloadButton.setOnAction(e -> download());
    }
    
    private void setDefaultIcon() {
        iconView.setImage(new Image(getClass().getResourceAsStream("/images/icons/default-mod.png")));
    }
    
    private Label createTag(String text, String styleClass) {
        Label tag = new Label(text);
        tag.getStyleClass().addAll("tag", styleClass);
        return tag;
    }
    
    private void showDetail() {
        ModDetailDialog dialog = new ModDetailDialog(result);
        dialog.showAndWait();
    }
    
    private void download() {
        fireEvent(new SearchEvent(SearchEvent.DOWNLOAD, result));
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
    
    public ModSearchResult getResult() {
        return result;
    }
}