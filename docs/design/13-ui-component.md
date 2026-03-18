# UI模块 - 自定义组件库

## 1. 组件结构

```
com.aurora.ui.component/
├── card/
│   ├── Card.java                    # 基础卡片(可折叠)
│   ├── QuickLaunchCard.java         # 快速启动卡片
│   ├── InstanceListCard.java        # 实例列表卡片
│   ├── InstanceItem.java            # 实例项
│   └── AnnouncementCard.java        # 公告卡片
├── item/
│   ├── VersionItem.java             # 版本列表项
│   ├── ModItem.java                 # 模组列表项
│   ├── SearchResultItem.java        # 搜索结果项
│   └── AccountItem.java             # 账号列表项
├── dialog/
│   ├── CreateInstanceDialog.java    # 创建实例对话框
│   ├── DownloadConfigDialog.java    # 下载配置对话框
│   ├── ModDetailDialog.java         # 模组详情对话框
│   └── ConfirmDialog.java           # 确认对话框
├── input/
│   ├── SearchField.java             # 搜索输入框
│   ├── PathField.java               # 路径选择框
│   └── MemorySlider.java            # 内存滑块
├── notification/
│   ├── Notification.java            # 通知组件
│   └── NotificationManager.java     # 通知管理器
└── common/
    ├── IconButton.java              # 图标按钮
    ├── StatusBadge.java             # 状态标签
    └── ProgressButton.java          # 带进度的按钮
```

## 2. Card (基础卡片组件)

```java
public class Card extends VBox {
    @FXML private HBox headerArea;
    @FXML private Label titleLabel;
    @FXML private HBox headerButtons;
    @FXML private StackPane expandIcon;
    @FXML private VBox contentArea;
    
    private BooleanProperty expanded = new SimpleBooleanProperty(true);
    private BooleanProperty canToggle = new SimpleBooleanProperty(true);
    
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
            throw new RuntimeException(e);
        }
    }
    
    private void setupAnimation() {
        expandIcon.visibleProperty().bind(canToggle);
        expandIcon.setOnMouseClicked(e -> toggle());
        
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
        titleLabel.setText(title);
    }
    
    public void setContent(Node content) {
        contentArea.getChildren().setAll(content);
    }
    
    public void addHeaderButton(Node button) {
        headerButtons.getChildren().add(button);
    }
    
    // Properties
    public BooleanProperty expandedProperty() { return expanded; }
    public BooleanProperty canToggleProperty() { return canToggle; }
}
```

**Card.fxml:**

```xml
<VBox fx:root="true" styleClass="card">
    <!-- 标题栏 -->
    <HBox styleClass="card-header" onMouseClicked="#toggle">
        <Label fx:id="titleLabel" styleClass="card-title"/>
        <Region HBox.hgrow="ALWAYS"/>
        <HBox fx:id="headerButtons" spacing="8"/>
        <StackPane fx:id="expandIcon" styleClass="expand-icon">
            <ImageView fitWidth="16" fitHeight="16">
                <Image url="@../../images/icons/chevron-down.png"/>
            </ImageView>
        </StackPane>
    </HBox>
    
    <!-- 内容区 -->
    <VBox fx:id="contentArea" styleClass="card-content"/>
</VBox>
```

## 3. InstanceItem (实例项组件)

```java
public class InstanceItem extends VBox {
    @FXML private ImageView iconView;
    @FXML private Label nameLabel;
    @FXML private Label versionLabel;
    @FXML private Label loaderLabel;
    @FXML private Button launchButton;
    @FXML private Button settingsButton;
    @FXML private HBox statusBadge;
    
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
            throw new RuntimeException(e);
        }
    }
    
    private void initialize() {
        nameLabel.setText(instance.getName());
        versionLabel.setText(instance.getMinecraftVersion());
        loaderLabel.setText(instance.getLoaderInfo());
        
        if (instance.getIconPath() != null) {
            iconView.setImage(new Image(instance.getIconPath()));
        }
        
        // 状态显示
        updateStatus();
        
        // 事件
        launchButton.setOnAction(e -> fireEvent(new InstanceEvent(InstanceEvent.LAUNCH, instance)));
        settingsButton.setOnAction(e -> fireEvent(new InstanceEvent(InstanceEvent.SETTINGS, instance)));
        
        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                fireEvent(new InstanceEvent(InstanceEvent.LAUNCH, instance));
            }
        });
    }
    
    private void updateStatus() {
        statusBadge.getChildren().clear();
        
        InstanceState state = instance.getState();
        switch (state) {
            case RUNNING -> {
                statusBadge.getChildren().add(new StatusBadge("运行中", "status-running"));
            }
            case UPDATING -> {
                statusBadge.getChildren().add(new StatusBadge("更新中", "status-updating"));
            }
            case ERROR -> {
                statusBadge.getChildren().add(new StatusBadge("错误", "status-error"));
            }
        }
    }
    
    public void setOnLaunch(EventHandler<InstanceEvent> handler) {
        addEventHandler(InstanceEvent.LAUNCH, handler);
    }
    
    public void setOnSettings(EventHandler<InstanceEvent> handler) {
        addEventHandler(InstanceEvent.SETTINGS, handler);
    }
}
```

**InstanceItem.fxml:**

```xml
<VBox fx:root="true" styleClass="instance-item">
    <ImageView fx:id="iconView" fitWidth="64" fitHeight="64" styleClass="instance-icon"/>
    <Label fx:id="nameLabel" styleClass="instance-name"/>
    <Label fx:id="versionLabel" styleClass="instance-version"/>
    <Label fx:id="loaderLabel" styleClass="instance-loader"/>
    <HBox fx:id="statusBadge" spacing="4"/>
    <HBox spacing="8" alignment="CENTER">
        <Button fx:id="launchButton" text="%action.launch" styleClass="btn-primary, btn-sm"/>
        <Button fx:id="settingsButton" text="..." styleClass="btn-icon, btn-sm"/>
    </HBox>
</VBox>
```

## 4. VersionItem (版本列表项)

```java
public class VersionItem extends HBox {
    @FXML private Label versionLabel;
    @FXML private Label typeLabel;
    @FXML private Label dateLabel;
    @FXML private Button downloadButton;
    @FXML private MenuButton installButton;
    
    private final VersionInfo version;
    
    public VersionItem(VersionInfo version) {
        this.version = version;
        loadFxml();
        initialize();
    }
    
    private void initialize() {
        versionLabel.setText(version.getId());
        typeLabel.setText(version.getType().name());
        typeLabel.getStyleClass().add("version-type-" + version.getType().name().toLowerCase());
        dateLabel.setText(formatDate(version.getReleaseTime()));
        
        downloadButton.setOnAction(e -> fireEvent(new VersionEvent(VersionEvent.DOWNLOAD, version)));
        
        // 安装模组加载器选项
        MenuItem installFabric = new MenuItem("Install Fabric");
        installFabric.setOnAction(e -> fireEvent(new VersionEvent(VersionEvent.INSTALL_FABRIC, version)));
        
        MenuItem installForge = new MenuItem("Install Forge");
        installForge.setOnAction(e -> fireEvent(new VersionEvent(VersionEvent.INSTALL_FORGE, version)));
        
        installButton.getItems().addAll(installFabric, installForge);
    }
}
```

## 5. SearchResultItem (搜索结果项)

```java
public class SearchResultItem extends HBox {
    @FXML private ImageView iconView;
    @FXML private Label nameLabel;
    @FXML private Label authorLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label downloadsLabel;
    @FXML private HBox tagsBox;
    @FXML private Button viewButton;
    @FXML private Button downloadButton;
    
    private final SearchResult result;
    
    public SearchResultItem(SearchResult result) {
        this.result = result;
        loadFxml();
        initialize();
    }
    
    private void initialize() {
        nameLabel.setText(result.getName());
        authorLabel.setText(result.getAuthor());
        descriptionLabel.setText(result.getDescription());
        downloadsLabel.setText(formatDownloads(result.getDownloads()));
        
        if (result.getIconUrl() != null) {
            iconView.setImage(new Image(result.getIconUrl(), true));
        }
        
        // 标签
        tagsBox.getChildren().clear();
        if (result.getSource() != null) {
            tagsBox.getChildren().add(createTag(result.getSource(), "tag-source"));
        }
        if (result.getLoader() != null) {
            tagsBox.getChildren().add(createTag(result.getLoader(), "tag-loader"));
        }
        
        viewButton.setOnAction(e -> showDetail());
        downloadButton.setOnAction(e -> download());
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
        if (downloads < 1000) return String.valueOf(downloads);
        if (downloads < 1000000) return String.format("%.1fK", downloads / 1000.0);
        return String.format("%.1fM", downloads / 1000000.0);
    }
}
```

## 6. CreateInstanceDialog (创建实例对话框)

```java
public class CreateInstanceDialog extends Dialog<InstanceConfig> {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> versionBox;
    @FXML private ComboBox<String> loaderBox;
    @FXML private ComboBox<String> loaderVersionBox;
    @FXML private MemorySlider memorySlider;
    @FXML private Button importButton;
    
    private final VersionManifestService versionService = ServiceLocator.get(VersionManifestService.class);
    
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
            throw new RuntimeException(e);
        }
    }
    
    private void setupDialog() {
        setTitle(t("instance.create.title"));
        
        ButtonType createButtonType = new ButtonType(t("action.create"), ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                return buildConfig();
            }
            return null;
        });
        
        // 验证
        Button createButton = (Button) getDialogPane().lookupButton(createButtonType);
        createButton.disableProperty().bind(
            nameField.textProperty().isEmpty()
                .or(versionBox.valueProperty().isNull())
        );
    }
    
    private void loadData() {
        versionService.getManifest().thenAccept(manifest -> {
            Platform.runLater(() -> {
                List<String> releases = manifest.getVersions().stream()
                    .filter(v -> v.getType() == VersionType.RELEASE)
                    .map(VersionInfo::getId)
                    .collect(Collectors.toList());
                versionBox.getItems().setAll(releases);
                versionBox.getSelectionModel().selectFirst();
            });
        });
        
        loaderBox.getItems().addAll("Vanilla", "Fabric", "Forge", "Quilt");
        loaderBox.getSelectionModel().selectFirst();
        
        versionBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                loadLoaderVersions(newVal);
            }
        });
    }
    
    private void loadLoaderVersions(String mcVersion) {
        String loader = loaderBox.getValue();
        if (loader == null || "Vanilla".equals(loader)) {
            loaderVersionBox.setDisable(true);
            loaderVersionBox.getItems().clear();
            return;
        }
        
        loaderVersionBox.setDisable(false);
        // 加载对应加载器版本
    }
    
    private InstanceConfig buildConfig() {
        return InstanceConfig.builder()
            .name(nameField.getText())
            .minecraftVersion(versionBox.getValue())
            .loaderType(loaderBox.getValue())
            .loaderVersion(loaderVersionBox.getValue())
            .maxMemory(memorySlider.getValue())
            .build();
    }
    
    @FXML
    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(t("instance.import.selectFile"));
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CurseForge", "*.zip"),
            new FileChooser.ExtensionFilter("Modrinth", "*.mrpack"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = chooser.showOpenDialog(getDialogPane().getScene().getWindow());
        if (file != null) {
            importModpack(file.toPath());
        }
    }
    
    private void importModpack(Path path) {
        // 导入整合包
    }
}
```

## 7. ModDetailDialog (模组详情对话框)

```java
public class ModDetailDialog extends Dialog<Void> {
    @FXML private ImageView iconView;
    @FXML private Label nameLabel;
    @FXML private Label authorLabel;
    @FXML private Label sourceLabel;
    @FXML private Label downloadsLabel;
    @FXML private TextArea descriptionArea;
    @FXML private ListView<String> versionsList;
    @FXML private Hyperlink pageLink;
    @FXML private Button downloadButton;
    
    private final SearchResult result;
    private final UnifiedSearch searchService = ServiceLocator.get(UnifiedSearch.class);
    
    public ModDetailDialog(SearchResult result) {
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
            throw new RuntimeException(e);
        }
        
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }
    
    private void loadData() {
        nameLabel.setText(result.getName());
        authorLabel.setText(result.getAuthor());
        sourceLabel.setText(result.getSource());
        downloadsLabel.setText(formatDownloads(result.getDownloads()));
        descriptionArea.setText(result.getDescription());
        
        if (result.getIconUrl() != null) {
            iconView.setImage(new Image(result.getIconUrl(), true));
        }
        
        pageLink.setText(result.getPageUrl());
        pageLink.setOnAction(e -> HostServices.getHostServices().showDocument(result.getPageUrl()));
        
        loadVersions();
    }
    
    private void loadVersions() {
        searchService.getVersions(result.getId(), result.getSource())
            .thenAccept(versions -> Platform.runLater(() -> {
                versionsList.getItems().setAll(
                    versions.stream()
                        .map(v -> String.format("%s (%s)", v.getVersionNumber(), 
                            String.join(", ", v.getGameVersions())))
                        .collect(Collectors.toList())
                );
            }));
    }
    
    @FXML
    private void onDownload() {
        String selected = versionsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // 下载模组
        }
    }
    
    private String formatDownloads(long downloads) {
        if (downloads < 1000) return String.valueOf(downloads);
        if (downloads < 1000000) return String.format("%.1fK", downloads / 1000.0);
        return String.format("%.1fM", downloads / 1000000.0);
    }
}
```

## 8. NotificationManager (通知管理)

```java
public class NotificationManager {
    private static NotificationManager instance;
    private final StackPane container;
    private final VBox notificationsBox;
    private final int maxVisible = 3;
    private final Queue<Notification> queue = new LinkedList<>();
    
    private NotificationManager(StackPane container) {
        this.container = container;
        this.notificationsBox = createNotificationsBox();
        container.getChildren().add(notificationsBox);
    }
    
    public static void initialize(StackPane container) {
        instance = new NotificationManager(container);
    }
    
    public static NotificationManager getInstance() {
        return instance;
    }
    
    private VBox createNotificationsBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_RIGHT);
        box.setPadding(new Insets(10));
        StackPane.setAlignment(box, Pos.TOP_RIGHT);
        return box;
    }
    
    public void show(String message, NotificationType type) {
        Platform.runLater(() -> {
            Notification notification = new Notification(message, type);
            
            if (notificationsBox.getChildren().size() >= maxVisible) {
                queue.offer(notification);
            } else {
                display(notification);
            }
        });
    }
    
    private void display(Notification notification) {
        notificationsBox.getChildren().add(notification);
        
        notification.setOnClose(() -> {
            notificationsBox.getChildren().remove(notification);
            
            if (!queue.isEmpty()) {
                display(queue.poll());
            }
        });
        
        notification.show();
    }
    
    public void info(String message) { show(message, NotificationType.INFO); }
    public void success(String message) { show(message, NotificationType.SUCCESS); }
    public void warning(String message) { show(message, NotificationType.WARNING); }
    public void error(String message) { show(message, NotificationType.ERROR); }
}
```

## 9. Notification (通知组件)

```java
public class Notification extends HBox {
    @FXML private ImageView iconView;
    @FXML private Label messageLabel;
    @FXML private Button closeButton;
    
    private final NotificationType type;
    private Runnable onClose;
    
    public Notification(String message, NotificationType type) {
        this.type = type;
        loadFxml();
        messageLabel.setText(message);
        getStyleClass().addAll("notification", "notification--" + type.name().toLowerCase());
        
        closeButton.setOnAction(e -> close());
    }
    
    private void loadFxml() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/Notification.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void show() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> close());
        delay.play();
    }
    
    public void close() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), this);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            if (onClose != null) onClose.run();
        });
        fadeOut.play();
    }
    
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }
}

public enum NotificationType {
    INFO, SUCCESS, WARNING, ERROR
}
```

## 10. MemorySlider (内存滑块)

```java
public class MemorySlider extends VBox {
    @FXML private Slider minSlider;
    @FXML private Slider maxSlider;
    @FXML private Label minLabel;
    @FXML private Label maxLabel;
    @FXML private ComboBox<String> presetBox;
    
    private final long systemMemoryMB;
    
    public MemorySlider() {
        this.systemMemoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        loadFxml();
        initialize();
    }
    
    private void initialize() {
        long maxAllowed = (long) (systemMemoryMB * 0.8);
        
        minSlider.setMin(512);
        minSlider.setMax(maxAllowed);
        minSlider.setValue(2048);
        
        maxSlider.setMin(512);
        maxSlider.setMax(maxAllowed);
        maxSlider.setValue(4096);
        
        minSlider.valueProperty().addListener((obs, old, newVal) -> {
            minLabel.setText(formatMemory(newVal.longValue()));
            if (newVal.longValue() > maxSlider.getValue()) {
                maxSlider.setValue(newVal.longValue());
            }
        });
        
        maxSlider.valueProperty().addListener((obs, old, newVal) -> {
            maxLabel.setText(formatMemory(newVal.longValue()));
            if (newVal.longValue() < minSlider.getValue()) {
                minSlider.setValue(newVal.longValue());
            }
        });
        
        presetBox.getItems().addAll(
            t("memory.preset.auto"),
            t("memory.preset.low"),
            t("memory.preset.standard"),
            t("memory.preset.high")
        );
        
        presetBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            applyPreset(newVal);
        });
    }
    
    private void applyPreset(String preset) {
        long[] values = switch (preset) {
            case "memory.preset.low" -> new long[]{1024, 2048};
            case "memory.preset.standard" -> new long[]{2048, 4096};
            case "memory.preset.high" -> new long[]{4096, 8192};
            default -> calculateAutoMemory();
        };
        minSlider.setValue(values[0]);
        maxSlider.setValue(values[1]);
    }
    
    private long[] calculateAutoMemory() {
        long half = systemMemoryMB / 2;
        return new long[]{Math.max(1024, half / 2), Math.min(half, 8192)};
    }
    
    public long getMinValue() {
        return (long) minSlider.getValue() * 1024 * 1024;
    }
    
    public long getValue() {
        return (long) maxSlider.getValue() * 1024 * 1024;
    }
    
    public void setValue(long bytes) {
        maxSlider.setValue(bytes / 1024.0 / 1024.0);
    }
    
    private String formatMemory(long mb) {
        if (mb >= 1024) {
            return String.format("%.1f GB", mb / 1024.0);
        }
        return mb + " MB";
    }
}
```

## 11. 自定义事件

```java
public class InstanceEvent extends Event {
    public static final EventType<InstanceEvent> ANY = new EventType<>(Event.ANY, "INSTANCE");
    public static final EventType<InstanceEvent> LAUNCH = new EventType<>(ANY, "LAUNCH");
    public static final EventType<InstanceEvent> SETTINGS = new EventType<>(ANY, "SETTINGS");
    public static final EventType<InstanceEvent> DELETE = new EventType<>(ANY, "DELETE");
    
    private final Instance instance;
    
    public InstanceEvent(EventType<? extends Event> eventType, Instance instance) {
        super(eventType);
        this.instance = instance;
    }
    
    public Instance getInstance() { return instance; }
}

public class VersionEvent extends Event {
    public static final EventType<VersionEvent> ANY = new EventType<>(Event.ANY, "VERSION");
    public static final EventType<VersionEvent> DOWNLOAD = new EventType<>(ANY, "DOWNLOAD");
    public static final EventType<VersionEvent> INSTALL_FABRIC = new EventType<>(ANY, "INSTALL_FABRIC");
    public static final EventType<VersionEvent> INSTALL_FORGE = new EventType<>(ANY, "INSTALL_FORGE");
    
    private final VersionInfo version;
    
    public VersionEvent(EventType<? extends Event> eventType, VersionInfo version) {
        super(eventType);
        this.version = version;
    }
    
    public VersionInfo getVersion() { return version; }
}

public class SearchEvent extends Event {
    public static final EventType<SearchEvent> ANY = new EventType<>(Event.ANY, "SEARCH");
    public static final EventType<SearchEvent> DOWNLOAD = new EventType<>(ANY, "DOWNLOAD");
    public static final EventType<SearchEvent> DETAIL = new EventType<>(ANY, "DETAIL");
    
    private final SearchResult result;
    
    public SearchEvent(EventType<? extends Event> eventType, SearchResult result) {
        super(eventType);
        this.result = result;
    }
    
    public SearchResult getResult() { return result; }
}
```