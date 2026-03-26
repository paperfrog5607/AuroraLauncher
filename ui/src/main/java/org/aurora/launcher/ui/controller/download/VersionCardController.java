package org.aurora.launcher.ui.controller.download;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.aurora.launcher.api.mojang.VersionInfo;
import org.aurora.launcher.ui.controller.BaseController;
import org.aurora.launcher.ui.service.ServiceLocator;
import org.aurora.launcher.ui.service.VersionDownloadService;
import org.aurora.launcher.ui.service.VersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VersionCardController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(VersionCardController.class);
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    
    private static final String[] MC_ICON_URLS = {
        "https://bmclapi2.bangbang93.com/assets/icon/GrassBlock.png",
        "https://mcp表皮.cdn.bangbang93.com/assets/icon/GrassBlock.png",
        "https://gamepedia.cursecdn.com/minecraft_gamepedia/3/35/GrassBlock.png"
    };
    
    private static final int LOAD_TIMEOUT_SECONDS = 8;
    private static final int MAX_RETRIES = 2;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TilePane versionGrid;
    
    @FXML
    private ChoiceBox<String> typeFilter;
    
    @FXML
    private Label versionCountLabel;
    
    @FXML
    private VBox loadingContainer;
    
    @FXML
    private ProgressIndicator loadingIndicator;
    
    @FXML
    private Label loadingLabel;
    
    private VersionService versionService;
    private VersionDownloadService downloadService;
    private List<VersionInfo> allVersions;
    private String currentFilter = "all";
    
    private Image mcImage;
    private boolean imageLoaded = false;
    private final ExecutorService iconLoader = Executors.newCachedThreadPool();
    
    @Override
    protected void onInitialize() {
        logger.info("VersionCardController onInitialize called");
        
        showLoading("正在加载图标...");
        loadMinecraftIconWithRetry();
        
        try {
            versionService = ServiceLocator.get(VersionService.class);
            downloadService = ServiceLocator.get(VersionDownloadService.class);
        } catch (Exception e) {
            logger.warn("Services not available: {}", e.getMessage());
        }
        
        setupTypeFilter();
        showLoading("正在获取版本列表...");
        loadVersions();
    }
    
    private void showLoading(String message) {
        if (loadingContainer != null) {
            loadingLabel.setText(message);
            loadingContainer.setVisible(true);
            loadingContainer.setManaged(true);
        }
    }
    
    private void hideLoading() {
        if (loadingContainer != null) {
            loadingContainer.setVisible(false);
            loadingContainer.setManaged(false);
        }
    }
    
    private void loadMinecraftIconWithRetry() {
        iconLoader.submit(() -> {
            AtomicInteger urlIndex = new AtomicInteger(0);
            loadIconRecursive(urlIndex, 0);
        });
    }
    
    private void loadIconRecursive(AtomicInteger urlIndex, int retryCount) {
        if (imageLoaded) return;
        
        int index = urlIndex.get();
        if (index >= MC_ICON_URLS.length) {
            logger.warn("All icon URLs failed, using placeholder");
            return;
        }
        
        String url = MC_ICON_URLS[index];
        logger.info("Loading Minecraft icon from: {}", url);
        
        try {
            Image icon = new Image(url, 64, 64, true, true, true);
            
            Worker.State state = icon.getProgress() == 1.0 ? Worker.State.SUCCEEDED : null;
            
            if (icon.exceptionProperty().get() != null) {
                throw icon.exceptionProperty().get();
            }
            
            icon.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.doubleValue() == 1.0 && !imageLoaded) {
                    Platform.runLater(() -> {
                        mcImage = icon;
                        imageLoaded = true;
                        logger.info("Minecraft icon loaded successfully from {}", url);
                        updateAllCardsWithIcon();
                    });
                }
            });
            
            icon.exceptionProperty().addListener((obs, oldVal, ex) -> {
                if (ex != null && !imageLoaded) {
                    logger.warn("Failed to load icon from {}: {}", url, ex.getMessage());
                    
                    if (retryCount < MAX_RETRIES) {
                        logger.info("Retrying ({} / {})...", retryCount + 1, MAX_RETRIES);
                        try {
                            Thread.sleep(1000 * (retryCount + 1));
                            loadIconRecursive(urlIndex, retryCount + 1);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        urlIndex.incrementAndGet();
                        if (index < MC_ICON_URLS.length - 1) {
                            loadIconRecursive(urlIndex, 0);
                        } else {
                            logger.warn("All icon URLs exhausted, using placeholder");
                        }
                    }
                }
            });
            
        } catch (Exception e) {
            logger.warn("Exception loading icon from {}: {}", url, e.getMessage());
            if (retryCount < MAX_RETRIES) {
                try {
                    Thread.sleep(1000 * (retryCount + 1));
                    loadIconRecursive(urlIndex, retryCount + 1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } else {
                urlIndex.incrementAndGet();
                if (index < MC_ICON_URLS.length - 1) {
                    loadIconRecursive(urlIndex, 0);
                }
            }
        }
    }
    
    private void updateAllCardsWithIcon() {
        if (versionGrid == null || !imageLoaded || mcImage == null) return;
        
        versionGrid.getChildren().forEach(node -> {
            if (node instanceof VBox card) {
                if (!card.getChildren().isEmpty() && card.getChildren().get(0) instanceof StackPane iconPane) {
                    iconPane.getChildren().clear();
                    ImageView imageView = new ImageView(mcImage);
                    imageView.setFitWidth(56);
                    imageView.setFitHeight(56);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    iconPane.getChildren().add(imageView);
                }
            }
        });
    }
    
    private void setupTypeFilter() {
        typeFilter.setItems(FXCollections.observableArrayList(
            "全部版本",
            "正式版",
            "快照版",
            "历史版"
        ));
        typeFilter.getSelectionModel().selectFirst();
        typeFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switch (newVal) {
                    case "全部版本":
                        currentFilter = "all";
                        break;
                    case "正式版":
                        currentFilter = "release";
                        break;
                    case "快照版":
                        currentFilter = "snapshot";
                        break;
                    case "历史版":
                        currentFilter = "old";
                        break;
                    default:
                        currentFilter = "all";
                }
                filterVersions();
            }
        });
    }
    
    @FXML
    private void onSearch() {
        filterVersions();
    }
    
    private void filterVersions() {
        if (allVersions == null) return;
        
        String query = searchField.getText().toLowerCase().trim();
        
        List<VersionInfo> filtered = allVersions.stream()
            .filter(v -> {
                if (!query.isEmpty() && !v.getId().toLowerCase().contains(query)) {
                    return false;
                }
                if ("all".equals(currentFilter)) return true;
                if ("old".equals(currentFilter)) {
                    return !"release".equals(v.getType()) && !"snapshot".equals(v.getType());
                }
                return currentFilter.equals(v.getType());
            })
            .limit(100)
            .toList();
        
        displayVersions(filtered);
    }
    
    private void loadVersions() {
        if (versionService == null) {
            logger.error("VersionService is null!");
            hideLoading();
            return;
        }
        
        versionService.getVersionList()
            .thenAcceptAsync(versions -> {
                allVersions = versions;
                Platform.runLater(() -> {
                    hideLoading();
                    filterVersions();
                });
            })
            .exceptionally(e -> {
                logger.error("Failed to load versions", e);
                Platform.runLater(() -> {
                    hideLoading();
                    if (versionCountLabel != null) {
                        versionCountLabel.setText("加载失败");
                    }
                });
                return null;
            });
    }
    
    private void displayVersions(List<VersionInfo> versions) {
        versionGrid.getChildren().clear();
        
        for (VersionInfo version : versions) {
            VBox card = createVersionCard(version);
            versionGrid.getChildren().add(card);
        }
        
        if (versionCountLabel != null) {
            versionCountLabel.setText("共 " + versions.size() + " 个版本");
        }
    }
    
    private VBox createVersionCard(VersionInfo version) {
        VBox card = new VBox();
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setSpacing(6);
        card.setPrefSize(180, 180);
        card.setMaxSize(180, 180);
        card.setStyle("-fx-background-color: #374151; -fx-background-radius: 12; -fx-padding: 12; -fx-cursor: hand;");
        
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(56, 56);
        iconPane.setStyle("-fx-background-color: #10B981; -fx-background-radius: 8;");

        ImageView iconView = new ImageView();
        iconView.setFitWidth(56);
        iconView.setFitHeight(56);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);

        Label iconLabel = new Label("MC");
        iconLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        if (mcImage != null && imageLoaded) {
            iconView.setImage(mcImage);
            iconPane.getChildren().add(iconView);
        } else {
            iconPane.getChildren().add(iconLabel);
        }
        
        Label nameLabel = new Label(version.getId());
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(160);
        nameLabel.setAlignment(javafx.geometry.Pos.CENTER);
        
        String type = version.getType();
        String typeText;
        String typeColor;
        
        switch (type) {
            case "release":
                typeText = "● 正式版";
                typeColor = "#10B981";
                break;
            case "snapshot":
                typeText = "◆ 快照版";
                typeColor = "#F59E0B";
                break;
            default:
                typeText = "■ 历史版";
                typeColor = "#EF4444";
                break;
        }
        
        Label typeLabel = new Label(typeText);
        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + typeColor + ";");
        
        String dateStr = "--";
        if (version.getReleaseTime() != null) {
            try {
                LocalDateTime date = LocalDateTime.parse(version.getReleaseTime().replace("Z", ""));
                dateStr = date.format(DATE_FORMAT);
            } catch (Exception e) {
            }
        }
        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #9CA3AF;");
        
        card.getChildren().addAll(iconPane, nameLabel, typeLabel, dateLabel);
        
        card.setOnMouseClicked(e -> openVersionDetail(version));
        
        return card;
    }
    
    private void openVersionDetail(VersionInfo version) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/download/VersionDetailView.fxml"));
            javafx.stage.Stage detailStage = new javafx.stage.Stage();
            detailStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            detailStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            detailStage.setTitle("Minecraft " + version.getId());
            
            VBox root = loader.load();
            
            VersionDetailController controller = loader.getController();
            controller.setVersion(version);
            controller.setDownloadService(downloadService);
            controller.setParentStage(detailStage);
            
            detailStage.setScene(new javafx.scene.Scene(root));
            detailStage.setAlwaysOnTop(true);
            detailStage.show();
            
        } catch (Exception e) {
            logger.error("Failed to open version detail", e);
        }
    }
}