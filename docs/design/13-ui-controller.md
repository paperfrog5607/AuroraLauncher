# UI模块 - 控制器层

## 1. 控制器结构

```
com.aurora.ui.controller/
├── BaseController.java              # 基础控制器
├── CardController.java              # 卡片控制器(可折叠)
├── MainController.java              # 主控制器
│
├── launch/                          # 启动页
│   ├── LaunchController.java        # 启动页主控制器
│   ├── QuickLaunchCard.java         # 快速启动卡片
│   ├── InstanceListCard.java        # 实例列表卡片
│   └── AnnouncementCard.java        # 公告卡片
│
├── download/                        # 下载页
│   ├── DownloadController.java      # 下载页主控制器
│   ├── VersionController.java       # 游戏版本下载
│   ├── ModController.java           # 模组下载
│   ├── ModpackController.java       # 整合包下载
│   ├── ResourceController.java      # 资源包下载
│   └── ShaderController.java        # 光影下载
│
└── settings/                        # 设置页
    ├── SettingsController.java      # 设置页主控制器
    ├── LaunchSettingsController.java # 启动设置
    ├── DownloadSettingsController.java # 下载设置
    ├── ThemeSettingsController.java # 个性化设置
    ├── AccountSettingsController.java # 账号设置
    └── AboutController.java         # 关于页面
```

## 2. LaunchController (启动页)

```java
public class LaunchController extends BaseController {
    @FXML private VBox quickLaunchCard;
    @FXML private VBox instanceListCard;
    @FXML private VBox announcementCard;
    
    @Override
    public void initialize() {
        loadQuickLaunch();
        loadInstances();
        loadAnnouncements();
    }
    
    private void loadQuickLaunch() {
        // 加载快速启动卡片
    }
    
    private void loadInstances() {
        // 加载实例列表
    }
    
    private void loadAnnouncements() {
        // 加载公告
    }
}
```

## 3. QuickLaunchCard (快速启动卡片)

```java
public class QuickLaunchCard extends CardController {
    @FXML private ImageView avatarView;
    @FXML private Label usernameLabel;
    @FXML private Label accountTypeLabel;
    @FXML private ComboBox<Instance> versionBox;
    @FXML private Button launchButton;
    
    private AccountManager accountManager = ServiceLocator.get(AccountManager.class);
    private InstanceManager instanceManager = ServiceLocator.get(InstanceManager.class);
    
    @Override
    public void initialize() {
        super.initialize();
        loadAccountInfo();
        loadVersionList();
    }
    
    private void loadAccountInfo() {
        Account account = accountManager.getCurrentAccount();
        if (account != null) {
            usernameLabel.setText(account.getUsername());
            accountTypeLabel.setText(t("account.type." + account.getType().name().toLowerCase()));
            if (account.getAvatarUrl() != null) {
                avatarView.setImage(new Image(account.getAvatarUrl()));
            }
        }
    }
    
    private void loadVersionList() {
        List<Instance> instances = instanceManager.getAllInstances();
        versionBox.getItems().setAll(instances);
        versionBox.setCellFactory(list -> new InstanceListCell());
        versionBox.setButtonCell(new InstanceListCell());
        
        if (!instances.isEmpty()) {
            versionBox.getSelectionModel().selectFirst();
        }
    }
    
    @FXML
    private void onLaunch() {
        Instance selected = versionBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showNotification(t("error.noInstance"), NotificationType.WARNING);
            return;
        }
        
        launchButton.setDisable(true);
        launchButton.setText(t("instance.launching"));
        
        instanceManager.launch(selected.getId())
            .thenAccept(v -> Platform.runLater(() -> {
                showNotification(t("instance.launched"), NotificationType.SUCCESS);
                launchButton.setDisable(false);
                launchButton.setText(t("action.launch"));
            }))
            .exceptionally(e -> {
                Platform.runLater(() -> {
                    showError(t("instance.launchFailed"), e);
                    launchButton.setDisable(false);
                    launchButton.setText(t("action.launch"));
                });
                return null;
            });
    }
    
    @FXML
    private void onChangeAccount() {
        router.switchTab("settings", "account");
    }
}
```

## 4. InstanceListCard (实例列表卡片)

```java
public class InstanceListCard extends CardController {
    @FXML private FlowPane instanceGrid;
    @FXML private Button newInstanceButton;
    @FXML private Button openFolderButton;
    
    private InstanceManager instanceManager = ServiceLocator.get(InstanceManager.class);
    
    @Override
    public void initialize() {
        super.initialize();
        loadInstances();
    }
    
    private void loadInstances() {
        List<Instance> instances = instanceManager.getAllInstances();
        instanceGrid.getChildren().clear();
        
        for (Instance instance : instances) {
            InstanceItem item = new InstanceItem(instance);
            item.setOnLaunch(e -> launchInstance(instance));
            item.setOnEdit(e -> openInstanceSettings(instance));
            instanceGrid.getChildren().add(item);
        }
    }
    
    @FXML
    private void onNewInstance() {
        CreateInstanceDialog dialog = new CreateInstanceDialog();
        Optional<InstanceConfig> result = dialog.showAndWait();
        result.ifPresent(config -> {
            instanceManager.create(config)
                .thenAccept(i -> Platform.runLater(this::loadInstances));
        });
    }
    
    @FXML
    private void onOpenFolder() {
        try {
            Desktop.getDesktop().open(instanceManager.getInstancesDir().toFile());
        } catch (IOException e) {
            showError(t("error.openFolder"), e);
        }
    }
    
    private void launchInstance(Instance instance) {
        instanceManager.launch(instance.getId());
    }
    
    private void openInstanceSettings(Instance instance) {
        router.switchTab("settings");
        // 打开实例设置
    }
}
```

## 5. AnnouncementCard (公告卡片)

```java
public class AnnouncementCard extends CardController {
    @FXML private ListView<Announcement> announcementList;
    
    @Override
    public void initialize() {
        super.initialize();
        loadAnnouncements();
    }
    
    private void loadAnnouncements() {
        // 从服务器加载公告
        // 或读取本地缓存
    }
}
```

## 6. DownloadController (下载页主控制器)

```java
public class DownloadController extends BaseController {
    @FXML private HBox subTabBar;
    @FXML private StackPane subContentArea;
    
    private String currentSubTab = "version";
    
    @Override
    public void initialize() {
        switchSubTab("version");
    }
    
    @FXML
    private void onVersionTab() { switchSubTab("version"); }
    
    @FXML
    private void onModTab() { switchSubTab("mod"); }
    
    @FXML
    private void onModpackTab() { switchSubTab("modpack"); }
    
    @FXML
    private void onResourceTab() { switchSubTab("resource"); }
    
    @FXML
    private void onShaderTab() { switchSubTab("shader"); }
    
    private void switchSubTab(String tabId) {
        updateSubTabSelection(tabId);
        router.switchSubTab(tabId);
        currentSubTab = tabId;
    }
    
    private void updateSubTabSelection(String tabId) {
        for (Node node : subTabBar.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("active");
            }
        }
        subTabBar.getChildren().stream()
            .filter(n -> n.getId() != null && n.getId().equals(tabId))
            .findFirst()
            .ifPresent(n -> n.getStyleClass().add("active"));
    }
}
```

## 7. VersionController (游戏版本下载)

```java
public class VersionController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ListView<VersionInfo> releaseList;
    @FXML private ListView<VersionInfo> snapshotList;
    @FXML private ProgressIndicator loadingIndicator;
    
    private VersionManifestService versionService = ServiceLocator.get(VersionManifestService.class);
    private GameInstaller gameInstaller = ServiceLocator.get(GameInstaller.class);
    
    @Override
    public void initialize() {
        loadVersions();
    }
    
    private void loadVersions() {
        loadingIndicator.setVisible(true);
        
        versionService.getManifest().thenAccept(manifest -> {
            Platform.runLater(() -> {
                List<VersionInfo> releases = manifest.getVersions().stream()
                    .filter(v -> v.getType() == VersionType.RELEASE)
                    .collect(Collectors.toList());
                
                List<VersionInfo> snapshots = manifest.getVersions().stream()
                    .filter(v -> v.getType() == VersionType.SNAPSHOT)
                    .collect(Collectors.toList());
                
                releaseList.getItems().setAll(releases);
                snapshotList.getItems().setAll(snapshots);
                loadingIndicator.setVisible(false);
            });
        });
    }
    
    @FXML
    private void onDownload(VersionInfo version) {
        DownloadConfigDialog dialog = new DownloadConfigDialog(version);
        Optional<InstallOptions> result = dialog.showAndWait();
        
        result.ifPresent(options -> {
            gameInstaller.install(version, options)
                .thenAccept(v -> Platform.runLater(() -> 
                    showNotification(t("download.complete", version.getId()), NotificationType.SUCCESS)));
        });
    }
    
    @FXML
    private void onInstallFabric(VersionInfo version) {
        // 安装Fabric
    }
    
    @FXML
    private void onInstallForge(VersionInfo version) {
        // 安装Forge
    }
}
```

## 8. ModController (模组下载)

```java
public class ModController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sourceBox;
    @FXML private ComboBox<String> categoryBox;
    @FXML private ComboBox<String> versionBox;
    @FXML private ComboBox<String> loaderBox;
    @FXML private ListView<SearchResult> resultList;
    @FXML private Pagination pagination;
    @FXML private ProgressIndicator loadingIndicator;
    
    private UnifiedSearch searchService = ServiceLocator.get(UnifiedSearch.class);
    private String currentQuery;
    private int currentPage = 0;
    
    @Override
    public void initialize() {
        setupFilters();
    }
    
    private void setupFilters() {
        sourceBox.getItems().addAll(t("search.source.all"), "Modrinth", "CurseForge");
        sourceBox.getSelectionModel().selectFirst();
        
        versionBox.getItems().addAll(getMcVersions());
        loaderBox.getItems().addAll("Fabric", "Forge", "Quilt");
    }
    
    @FXML
    private void onSearch() {
        currentQuery = searchField.getText();
        if (currentQuery.isEmpty()) return;
        
        loadingIndicator.setVisible(true);
        
        SearchOptions options = new SearchOptions()
            .query(currentQuery)
            .gameVersion(versionBox.getValue())
            .loader(loaderBox.getValue())
            .offset(currentPage * 20);
        
        searchService.search(currentQuery, options)
            .thenAccept(results -> Platform.runLater(() -> {
                resultList.getItems().setAll(results);
                loadingIndicator.setVisible(false);
            }));
    }
    
    @FXML
    private void onDownload(SearchResult result) {
        ModDetailDialog dialog = new ModDetailDialog(result);
        dialog.showAndWait();
    }
}
```

## 9. SettingsController (设置页主控制器)

```java
public class SettingsController extends BaseController {
    @FXML private HBox subTabBar;
    @FXML private StackPane subContentArea;
    
    @Override
    public void initialize() {
        switchSubTab("launch");
    }
    
    @FXML
    private void onLaunchTab() { router.switchSubTab("launch"); }
    
    @FXML
    private void onDownloadTab() { router.switchSubTab("download"); }
    
    @FXML
    private void onThemeTab() { router.switchSubTab("theme"); }
    
    @FXML
    private void onAccountTab() { router.switchSubTab("account"); }
    
    @FXML
    private void onAboutTab() { router.switchSubTab("about"); }
}
```

## 10. LaunchSettingsController (启动设置)

```java
public class LaunchSettingsController extends BaseController {
    // Java设置
    @FXML private TextField javaPathField;
    @FXML private Button detectJavaButton;
    @FXML private Button browseJavaButton;
    
    // 内存设置
    @FXML private ComboBox<String> memoryPresetBox;
    @FXML private Slider minMemorySlider;
    @FXML private Slider maxMemorySlider;
    @FXML private Label minMemoryLabel;
    @FXML private Label maxMemoryLabel;
    
    // JVM参数
    @FXML private TextArea jvmArgsField;
    
    private ConfigManager configManager = ServiceLocator.get(ConfigManager.class);
    private JavaManager javaManager = ServiceLocator.get(JavaManager.class);
    
    @Override
    public void initialize() {
        loadSettings();
    }
    
    private void loadSettings() {
        AuroraConfig config = configManager.getConfig();
        
        javaPathField.setText(config.getJavaPath());
        
        memoryPresetBox.getItems().addAll(
            t("memory.preset.auto"),
            t("memory.preset.low"),
            t("memory.preset.standard"),
            t("memory.preset.high")
        );
        memoryPresetBox.setValue(config.getMemoryPreset());
        
        minMemorySlider.setValue(config.getMinMemory() / 1024.0 / 1024.0);
        maxMemorySlider.setValue(config.getMaxMemory() / 1024.0 / 1024.0);
        
        jvmArgsField.setText(config.getJvmArgs());
    }
    
    @FXML
    private void onDetectJava() {
        List<JavaVersion> versions = javaManager.detectInstalled();
        if (versions.isEmpty()) {
            showNotification(t("java.notFound"), NotificationType.WARNING);
        } else {
            JavaVersion selected = javaManager.findBest(null);
            javaPathField.setText(selected.getPath());
        }
    }
    
    @FXML
    private void onBrowseJava() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(t("java.select"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            javaPathField.setText(file.getAbsolutePath());
        }
    }
    
    @FXML
    private void onSave() {
        AuroraConfig config = configManager.getConfig();
        config.setJavaPath(javaPathField.getText());
        config.setMemoryPreset(memoryPresetBox.getValue());
        config.setMinMemory((long) (minMemorySlider.getValue() * 1024 * 1024));
        config.setMaxMemory((long) (maxMemorySlider.getValue() * 1024 * 1024));
        config.setJvmArgs(jvmArgsField.getText());
        
        configManager.save();
        showNotification(t("settings.saved"), NotificationType.SUCCESS);
    }
}
```

## 11. DownloadSettingsController (下载设置)

```java
public class DownloadSettingsController extends BaseController {
    @FXML private Spinner<Integer> concurrentSpinner;
    @FXML private CheckBox proxyEnabledBox;
    @FXML private TextField proxyHostField;
    @FXML private TextField proxyPortField;
    @FXML private ComboBox<String> mirrorBox;
    
    private ConfigManager configManager = ServiceLocator.get(ConfigManager.class);
    
    @Override
    public void initialize() {
        loadSettings();
    }
    
    private void loadSettings() {
        AuroraConfig config = configManager.getConfig();
        
        concurrentSpinner.getValueFactory().setValue(config.getMaxConcurrentDownloads());
        proxyEnabledBox.setSelected(config.isProxyEnabled());
        proxyHostField.setText(config.getProxyHost());
        proxyPortField.setText(String.valueOf(config.getProxyPort()));
        
        mirrorBox.getItems().addAll(
            t("mirror.auto"),
            t("mirror.mojang"),
            t("mirror.bmclapi"),
            t("mirror.mcbsc")
        );
    }
    
    @FXML
    private void onSave() {
        AuroraConfig config = configManager.getConfig();
        config.setMaxConcurrentDownloads(concurrentSpinner.getValue());
        config.setProxyEnabled(proxyEnabledBox.isSelected());
        config.setProxyHost(proxyHostField.getText());
        config.setProxyPort(Integer.parseInt(proxyPortField.getText()));
        
        configManager.save();
        showNotification(t("settings.saved"), NotificationType.SUCCESS);
    }
}
```

## 12. ThemeSettingsController (个性化设置)

```java
public class ThemeSettingsController extends BaseController {
    @FXML private ComboBox<String> themeBox;
    @FXML private CheckBox darkModeBox;
    @FXML private CheckBox enableAnimationBox;
    @FXML private Button selectBackgroundButton;
    @FXML private ImageView backgroundPreview;
    @FXML private ComboBox<String> fontFamilyBox;
    
    private ThemeManager themeManager = ThemeManager.getInstance();
    private ConfigManager configManager = ServiceLocator.get(ConfigManager.class);
    
    @Override
    public void initialize() {
        loadThemes();
        loadSettings();
    }
    
    private void loadThemes() {
        themeBox.getItems().addAll(themeManager.getAvailableThemes());
    }
    
    private void loadSettings() {
        AuroraConfig config = configManager.getConfig();
        themeBox.setValue(config.getTheme());
        darkModeBox.setSelected(config.isDarkMode());
        enableAnimationBox.setSelected(config.isAnimationEnabled());
    }
    
    @FXML
    private void onThemeChange() {
        String theme = themeBox.getValue();
        themeManager.loadTheme(theme);
        configManager.getConfig().setTheme(theme);
    }
    
    @FXML
    private void onSelectBackground() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            themeManager.setBackgroundImage(file.toPath());
            backgroundPreview.setImage(new Image(file.toURI().toString()));
        }
    }
    
    @FXML
    private void onResetBackground() {
        themeManager.resetBackground();
        backgroundPreview.setImage(null);
    }
}
```

## 13. AccountSettingsController (账号设置)

```java
public class AccountSettingsController extends BaseController {
    @FXML private ImageView avatarView;
    @FXML private Label usernameLabel;
    @FXML private Label accountTypeLabel;
    @FXML private ListView<Account> accountList;
    @FXML private Button loginMicrosoftButton;
    @FXML private Button loginOfflineButton;
    @FXML private Button logoutButton;
    @FXML private Button refreshSkinButton;
    
    private AccountManager accountManager = ServiceLocator.get(AccountManager.class);
    
    @Override
    public void initialize() {
        loadCurrentAccount();
        loadAccountList();
    }
    
    private void loadCurrentAccount() {
        Account current = accountManager.getCurrentAccount();
        if (current != null) {
            usernameLabel.setText(current.getUsername());
            accountTypeLabel.setText(t("account.type." + current.getType().name().toLowerCase()));
            if (current.getAvatarUrl() != null) {
                avatarView.setImage(new Image(current.getAvatarUrl()));
            }
        }
    }
    
    private void loadAccountList() {
        accountList.setItems(accountManager.getAllAccounts());
        accountList.setCellFactory(list -> new AccountListCell());
    }
    
    @FXML
    private void onLoginMicrosoft() {
        loginMicrosoftButton.setDisable(true);
        
        accountManager.loginMicrosoft()
            .thenAccept(account -> Platform.runLater(() -> {
                loadCurrentAccount();
                loadAccountList();
                showNotification(t("account.loginSuccess"), NotificationType.SUCCESS);
                loginMicrosoftButton.setDisable(false);
            }))
            .exceptionally(e -> {
                Platform.runLater(() -> {
                    showError(t("account.loginFailed"), e);
                    loginMicrosoftButton.setDisable(false);
                });
                return null;
            });
    }
    
    @FXML
    private void onLoginOffline() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(t("account.offline.title"));
        dialog.setHeaderText(t("account.offline.header"));
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(username -> {
            Account account = accountManager.createOfflineAccount(username);
            accountManager.setCurrentAccount(account);
            loadCurrentAccount();
            loadAccountList();
        });
    }
    
    @FXML
    private void onLogout() {
        accountManager.logout();
        loadCurrentAccount();
        showNotification(t("account.loggedOut"), NotificationType.INFO);
    }
    
    @FXML
    private void onRefreshSkin() {
        accountManager.refreshSkin()
            .thenRun(() -> Platform.runLater(this::loadCurrentAccount));
    }
    
    @FXML
    private void onSwitchAccount() {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            accountManager.setCurrentAccount(selected);
            loadCurrentAccount();
        }
    }
    
    @FXML
    private void onDeleteAccount() {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            accountManager.deleteAccount(selected.getId());
            loadAccountList();
        }
    }
}
```

## 14. AboutController (关于页面)

```java
public class AboutController extends BaseController {
    @FXML private Label versionLabel;
    @FXML private Label buildLabel;
    @FXML private Hyperlink githubLink;
    @FXML private Hyperlink websiteLink;
    @FXML private TextArea licenseArea;
    @FXML private Button checkUpdateButton;
    
    @Override
    public void initialize() {
        loadInfo();
    }
    
    private void loadInfo() {
        versionLabel.setText(t("about.version", BuildInfo.VERSION));
        buildLabel.setText(t("about.build", BuildInfo.BUILD_TIME));
        
        githubLink.setText("GitHub");
        githubLink.setOnAction(e -> HostServices.getHostServices().showDocument("https://github.com/aurora/launcher"));
        
        websiteLink.setText(t("about.website"));
    }
    
    @FXML
    private void onCheckUpdate() {
        checkUpdateButton.setDisable(true);
        
        UpdateChecker.check()
            .thenAccept(update -> Platform.runLater(() -> {
                if (update.hasUpdate()) {
                    showUpdateDialog(update);
                } else {
                    showNotification(t("update.latest"), NotificationType.INFO);
                }
                checkUpdateButton.setDisable(false);
            }));
    }
    
    @FXML
    private void onExportLogs() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("aurora-logs.zip");
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            LogExporter.export(file.toPath());
        }
    }
}
```