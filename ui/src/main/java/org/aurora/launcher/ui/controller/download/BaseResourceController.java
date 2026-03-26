package org.aurora.launcher.ui.controller.download;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.aurora.launcher.api.unified.UnifiedMod;
import org.aurora.launcher.ui.cache.ImageCache;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.service.ResourceSearchService;
import org.aurora.launcher.ui.service.ServiceLocator;
import org.aurora.launcher.ui.service.UnifiedSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class BaseResourceController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseResourceController.class);
    
    @FXML
    protected TextField searchField;
    
    @FXML
    protected Button searchBtn;
    
    @FXML
    protected ListView<UnifiedMod> resourceList;
    
    @FXML
    protected ProgressIndicator loadingIndicator;
    
    @FXML
    protected Label statusLabel;
    
    @FXML
    protected TitledPane filterPane;
    
    @FXML
    protected CheckBox modrinthCheck;
    
    @FXML
    protected CheckBox curseforgeCheck;
    
    @FXML
    protected CheckBox popularCheck;
    
    @FXML
    protected HBox paginationBox;
    
    @FXML
    protected Button prevPageBtn;
    
    @FXML
    protected Button nextPageBtn;
    
    @FXML
    protected Label pageInfoLabel;
    
    @FXML
    protected TextField pageJumpField;
    
    @FXML
    protected Button jumpBtn;
    
    @FXML
    protected Label totalResultsLabel;
    
    protected ResourceSearchService searchService;
    protected UnifiedSearchService unifiedSearchService;
    
    protected boolean searchModrinth = true;
    protected boolean searchCurseforge = true;
    protected boolean showPopular = true;
    
    protected static final int PAGE_SIZE = 20;
    protected int currentPage = 1;
    protected int totalPages = 1;
    protected List<UnifiedMod> allResults = new ArrayList<>();
    protected String lastQuery = "";
    
    private final PauseTransition debounceTimer = new PauseTransition(Duration.millis(500));
    private String lastSearchQuery = "";
    private CompletableFuture<?> currentSearch;
    
    @Override
    protected void onInitialize() {
        logger.info("BaseResourceController onInitialize, controller class: {}", this.getClass().getSimpleName());
        try {
            searchService = ServiceLocator.get(ResourceSearchService.class);
            logger.info("ResourceSearchService obtained: {}", searchService != null);
        } catch (Exception e) {
            logger.warn("ResourceSearchService not available: {}", e.getMessage());
        }
        
        try {
            unifiedSearchService = ServiceLocator.get(UnifiedSearchService.class);
            logger.info("UnifiedSearchService obtained: {}", unifiedSearchService != null);
        } catch (Exception e) {
            logger.warn("UnifiedSearchService not available: {}", e.getMessage());
        }
        
        setupSourceToggle();
        setupListCells();
        setupDebounce();
        setupPagination();
        loadDefaultContent();
    }
    
    private void setupPagination() {
        if (prevPageBtn != null && nextPageBtn != null) {
            prevPageBtn.setOnAction(e -> onPrevPage());
            nextPageBtn.setOnAction(e -> onNextPage());
        }
        if (pageJumpField != null && jumpBtn != null) {
            jumpBtn.setOnAction(e -> onPageJump());
        }
        updatePaginationUI();
    }
    
    protected void updatePaginationUI() {
        boolean hasResults = !allResults.isEmpty();
        
        if (paginationBox != null) {
            paginationBox.setVisible(hasResults);
        }
        if (prevPageBtn != null) {
            prevPageBtn.setDisable(currentPage <= 1);
        }
        if (nextPageBtn != null) {
            nextPageBtn.setDisable(currentPage >= totalPages);
        }
        if (pageInfoLabel != null) {
            pageInfoLabel.setText(t("pagination.pageInfo", currentPage, totalPages));
        }
        if (totalResultsLabel != null) {
            totalResultsLabel.setText(t("pagination.totalResults", allResults.size()));
        }
    }
    
    @FXML
    protected void onPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            displayCurrentPage();
        }
    }
    
    @FXML
    protected void onNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            displayCurrentPage();
        }
    }
    
    @FXML
    protected void onPageJump() {
        if (pageJumpField == null || pageJumpField.getText().isEmpty()) {
            return;
        }
        try {
            int targetPage = Integer.parseInt(pageJumpField.getText().trim());
            if (targetPage >= 1 && targetPage <= totalPages) {
                currentPage = targetPage;
                displayCurrentPage();
                pageJumpField.setText("");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid page number: {}", pageJumpField.getText());
        }
    }
    
    protected void displayCurrentPage() {
        if (allResults.isEmpty()) {
            resourceList.setItems(FXCollections.emptyObservableList());
            return;
        }
        
        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allResults.size());
        
        if (fromIndex >= allResults.size()) {
            fromIndex = 0;
            toIndex = Math.min(PAGE_SIZE, allResults.size());
            currentPage = 1;
        }
        
        List<UnifiedMod> pageData = allResults.subList(fromIndex, toIndex);
        resourceList.setItems(FXCollections.observableArrayList(pageData));
        resourceList.scrollTo(0);
        
        updatePaginationUI();
        logger.info("Displaying page {} of {}, items {} to {}", currentPage, totalPages, fromIndex + 1, toIndex);
    }
    
    protected void calculateTotalPages() {
        totalPages = Math.max(1, (int) Math.ceil((double) allResults.size() / PAGE_SIZE));
        currentPage = Math.min(currentPage, totalPages);
    }
    
    private void setupSourceToggle() {
        if (modrinthCheck != null && curseforgeCheck != null && popularCheck != null) {
            modrinthCheck.setOnAction(e -> {
                searchModrinth = modrinthCheck.isSelected();
                if (!searchModrinth && !searchCurseforge) {
                    modrinthCheck.setSelected(true);
                    searchModrinth = true;
                }
                executeSearch();
            });
            
            curseforgeCheck.setOnAction(e -> {
                searchCurseforge = curseforgeCheck.isSelected();
                if (!searchModrinth && !searchCurseforge) {
                    curseforgeCheck.setSelected(true);
                    searchCurseforge = true;
                }
                executeSearch();
            });
            
            popularCheck.setOnAction(e -> {
                showPopular = popularCheck.isSelected();
                executeSearch();
            });
        }
    }
    
    protected void setupListCells() {
        resourceList.setCellFactory(list -> new UnifiedResourceListCell());
    }
    
    private void setupDebounce() {
        debounceTimer.setOnFinished(e -> executeSearch());
        
        if (searchField != null) {
            logger.info("Setting up debounce for searchField, current text: '{}'", searchField.getText());
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                logger.debug("Search text changed: '{}' -> '{}'", oldVal, newVal);
                debounceTimer.playFromStart();
            });
        } else {
            logger.warn("searchField is null, debounce not setup!");
        }
    }
    
    protected void loadDefaultContent() {
        executeSearch();
    }
    
    protected abstract CompletableFuture<List<UnifiedMod>> doSearch(String query);
    
    @FXML
    protected void onSearch() {
        debounceTimer.playFromStart();
    }
    
    private void executeSearch() {
        String query = searchField != null ? searchField.getText().trim() : "";
        
        if (query.equals(lastSearchQuery) && !query.isEmpty()) {
            logger.debug("Skipping duplicate search: {}", query);
            return;
        }
        lastSearchQuery = query;
        
        if (currentSearch != null && !currentSearch.isDone()) {
            currentSearch.cancel(true);
        }
        
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
        if (statusLabel != null) {
            statusLabel.setText(t("search.loading"));
        }
        
        if (searchService == null) {
            logger.error("searchService is null!");
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (statusLabel != null) statusLabel.setText("Search service not available");
            return;
        }
        
        logger.info("Searching for: {}", query.isEmpty() ? "(popular)" : query);
        
        currentSearch = doSearch(query.isEmpty() ? "" : query)
            .thenAcceptAsync(results -> {
                logger.info("Got {} results", results.size());
                Platform.runLater(() -> {
                    if (resourceList == null) {
                        logger.error("resourceList is null!");
                        return;
                    }
                    logger.info("=== UPDATE START ===");
                    logger.info("Controller: {}, resourceList id: {}", this.getClass().getSimpleName(), resourceList.hashCode());
                    logger.info("Setting list items, items={}", results.size());
                    
                    allResults = new ArrayList<>(results);
                    currentPage = 1;
                    calculateTotalPages();
                    
                    displayCurrentPage();
                    
                    logger.info("After pagination setup, total pages: {}, current page: {}", totalPages, currentPage);
                    
                    if (!results.isEmpty()) {
                        UnifiedMod first = results.get(0);
                        UnifiedMod last = results.get(results.size() - 1);
                        logger.info("First: {} ({}), Last: {} ({})", 
                            first.getName(), first.getSlug(), 
                            last.getName(), last.getSlug());
                    }
                    
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                    if (statusLabel != null) {
                        statusLabel.setText(results.isEmpty() 
                            ? t("search.noResults") 
                            : t("search.results", results.size()));
                    }
                    logger.info("=== UPDATE END ===");
                });
            })
            .exceptionally(e -> {
                if (!e.getClass().getSimpleName().equals("CancellationException")) {
                    logger.error("Search failed", e);
                    Platform.runLater(() -> {
                        if (loadingIndicator != null) loadingIndicator.setVisible(false);
                        if (statusLabel != null) statusLabel.setText("Error: " + e.getMessage());
                    });
                }
                return null;
            });
    }
    
    protected class UnifiedResourceListCell extends ListCell<UnifiedMod> {
        private final ImageView imageView = new ImageView();
        private String currentIconUrl = null;
        
        public UnifiedResourceListCell() {
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        }
        
        @Override
        protected void updateItem(UnifiedMod mod, boolean empty) {
            super.updateItem(mod, empty);
            
            if (empty || mod == null) {
                setGraphic(null);
                setText(null);
                imageView.setImage(null);
                currentIconUrl = null;
                return;
            }
            
            HBox container = new HBox(10);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPrefHeight(72);
            container.setMaxHeight(72);
            container.setStyle("-fx-padding: 8; -fx-background-color: #1F2937; -fx-background-radius: 8;");
            
            String iconUrl = mod.getIconUrl();
            currentIconUrl = iconUrl;
            
            if (iconUrl != null && !iconUrl.isEmpty()) {
                Image image = ImageCache.getInstance().getOrLoad(iconUrl, 48, 48, loadedImage -> {
                    if (iconUrl.equals(currentIconUrl)) {
                        imageView.setImage(loadedImage);
                    }
                });
                if (image != null && !image.isError()) {
                    imageView.setImage(image);
                } else {
                    imageView.setImage(null);
                }
            } else {
                imageView.setImage(null);
            }
            
            VBox info = new VBox(2);
            info.setAlignment(Pos.CENTER_LEFT);
            info.setPrefWidth(500);
            
            Label name = new Label(mod.getName() != null ? mod.getName() : "未知");
            name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
            
            Label source = new Label(mod.getSourceName().toUpperCase());
            source.setStyle("-fx-font-size: 10px; -fx-text-fill: #8B5CF6; -fx-padding: 2 6; -fx-background-color: #2D2D2D; -fx-background-radius: 4;");
            
            Label downloads = new Label(mod.getFormattedDownloads() + " 次下载");
            downloads.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
            
            HBox titleRow = new HBox(8);
            titleRow.getChildren().addAll(name, source);
            
            Label desc = new Label(mod.getDescription() != null ? mod.getDescription() : "");
            desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
            desc.setMaxWidth(450);
            desc.setWrapText(true);
            
            VBox textInfo = new VBox(2);
            textInfo.getChildren().addAll(titleRow, downloads, desc);
            
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button downloadBtn = new Button("下载");
            downloadBtn.setStyle("-fx-background-color: #8B5CF6; -fx-text-fill: white; -fx-padding: 6 16; -fx-font-size: 13px;");
            downloadBtn.setOnAction(e -> openProjectPage(mod));
            
            container.getChildren().addAll(imageView, textInfo, spacer, downloadBtn);
            setGraphic(container);
            setText(null);
        }
        
        private void openProjectPage(UnifiedMod mod) {
            String url = mod.getPageUrl();
            if (url == null || url.isEmpty()) {
                if ("modrinth".equals(mod.getSource())) {
                    url = "https://modrinth.com/mod/" + mod.getSlug();
                } else if ("curseforge".equals(mod.getSource())) {
                    url = "https://www.curseforge.com/minecraft/mc-mods/" + mod.getSlug();
                }
            }
            try {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (Exception e) {
                logger.error("Failed to open browser", e);
            }
        }
    }
}