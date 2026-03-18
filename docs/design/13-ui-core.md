# UI模块 - 核心架构

## 1. Application入口

```java
public class AuroraApplication extends Application {
    private static AuroraApplication instance;
    private Stage primaryStage;
    private TabRouter router;
    private ThemeManager themeManager;
    private I18nManager i18nManager;
    
    @Override
    public void start(Stage stage) {
        instance = this;
        this.primaryStage = stage;
        
        initializeServices();
        themeManager.loadTheme("dark");
        i18nManager.setLocale(Locale.CHINESE);
        configureStage(stage);
        showMainWindow();
    }
    
    private void configureStage(Stage stage) {
        stage.setTitle("Aurora Launcher");
        stage.setMinWidth(800);
        stage.setMinHeight(550);
        stage.initStyle(StageStyle.UNDECORATED);
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - 1000) / 2);
        stage.setY((screenBounds.getHeight() - 650) / 2);
    }
    
    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            loader.setResources(i18nManager.getBundle());
            
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 650);
            scene.getStylesheets().add(themeManager.getStylesheet());
            
            primaryStage.setScene(scene);
            primaryStage.show();
            
            router = new TabRouter(scene);
            router.switchTab("launch");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main view", e);
        }
    }
    
    public static AuroraApplication getInstance() {
        return instance;
    }
}
```

## 2. 标签路由系统

```java
public class TabRouter {
    private final Scene scene;
    private final Map<String, TabRoute> routes = new LinkedHashMap<>();
    private String currentTab;
    private String currentSubTab;
    
    public TabRouter(Scene scene) {
        this.scene = scene;
        registerRoutes();
    }
    
    private void registerRoutes() {
        // 主标签: 启动
        route("launch", "LaunchController", "/fxml/LaunchView.fxml");
        
        // 主标签: 下载
        route("download", "DownloadController", "/fxml/DownloadView.fxml")
            .subTab("version", "DownloadVersionController", "/fxml/download/VersionView.fxml")
            .subTab("mod", "DownloadModController", "/fxml/download/ModView.fxml")
            .subTab("modpack", "DownloadModpackController", "/fxml/download/ModpackView.fxml")
            .subTab("resource", "DownloadResourceController", "/fxml/download/ResourceView.fxml")
            .subTab("shader", "DownloadShaderController", "/fxml/download/ShaderView.fxml");
        
        // 主标签: 更多(设置)
        route("settings", "SettingsController", "/fxml/SettingsView.fxml")
            .subTab("launch", "SettingsLaunchController", "/fxml/settings/LaunchView.fxml")
            .subTab("download", "SettingsDownloadController", "/fxml/settings/DownloadView.fxml")
            .subTab("theme", "SettingsThemeController", "/fxml/settings/ThemeView.fxml")
            .subTab("account", "SettingsAccountController", "/fxml/settings/AccountView.fxml")
            .subTab("about", "SettingsAboutController", "/fxml/settings/AboutView.fxml");
    }
    
    public void switchTab(String tabId) {
        switchTab(tabId, null);
    }
    
    public void switchTab(String tabId, String subTabId) {
        TabRoute route = routes.get(tabId);
        if (route == null) return;
        
        try {
            // 更新标签选中状态
            updateTabSelection(tabId);
            
            // 加载主内容
            FXMLLoader loader = new FXMLLoader(getClass().getResource(route.getFxml()));
            loader.setResources(I18nManager.getInstance().getBundle());
            Parent content = loader.load();
            
            BaseController controller = loader.getController();
            controller.setRouter(this);
            
            // 更新内容区
            BorderPane mainView = (BorderPane) scene.getRoot();
            mainView.setCenter(content);
            
            currentTab = tabId;
            
            // 如果有子标签，切换子标签
            if (subTabId != null) {
                switchSubTab(subTabId);
            } else if (route.getDefaultSubTab() != null) {
                switchSubTab(route.getDefaultSubTab());
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to switch tab: " + tabId, e);
        }
    }
    
    public void switchSubTab(String subTabId) {
        TabRoute route = routes.get(currentTab);
        if (route == null) return;
        
        SubTabRoute subRoute = route.getSubTabs().get(subTabId);
        if (subRoute == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(subRoute.getFxml()));
            loader.setResources(I18nManager.getInstance().getBundle());
            Parent content = loader.load();
            
            BaseController controller = loader.getController();
            controller.setRouter(this);
            
            // 更新子内容区
            BorderPane mainContent = (BorderPane) ((BorderPane) scene.getRoot()).getCenter();
            mainContent.setCenter(content);
            
            currentSubTab = subTabId;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to switch sub tab: " + subTabId, e);
        }
    }
    
    private void updateTabSelection(String tabId) {
        // 更新顶部标签的选中样式
        HBox tabBar = (HBox) scene.getRoot().lookup("#tabBar");
        for (Node node : tabBar.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("tab-active");
                if (btn.getId().equals(tabId)) {
                    btn.getStyleClass().add("tab-active");
                }
            }
        }
    }
    
    private TabRouter route(String id, String controller, String fxml) {
        routes.put(id, new TabRoute(id, controller, fxml));
        return this;
    }
}

public class TabRoute {
    private String id;
    private String controller;
    private String fxml;
    private Map<String, SubTabRoute> subTabs = new LinkedHashMap<>();
    private String defaultSubTab;
    
    public TabRoute subTab(String id, String controller, String fxml) {
        subTabs.put(id, new SubTabRoute(id, controller, fxml));
        if (defaultSubTab == null) defaultSubTab = id;
        return this;
    }
}

public class SubTabRoute {
    private String id;
    private String controller;
    private String fxml;
}
```

## 3. 基础控制器

```java
public abstract class BaseController implements Initializable {
    protected TabRouter router;
    protected Map<String, Object> params;
    
    @FXML
    protected void initialize() {}
    
    public void setRouter(TabRouter router) {
        this.router = router;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    protected String t(String key) {
        return I18nManager.getInstance().get(key);
    }
    
    protected void switchTab(String tabId) {
        router.switchTab(tabId);
    }
    
    protected void switchTab(String tabId, String subTabId) {
        router.switchTab(tabId, subTabId);
    }
}

public abstract class CardController extends BaseController {
    @FXML private StackPane expandIcon;
    @FXML private VBox contentArea;
    
    private boolean expanded = true;
    
    @Override
    public void initialize() {
        super.initialize();
        setupCardToggle();
    }
    
    private void setupCardToggle() {
        if (expandIcon != null) {
            expandIcon.setOnMouseClicked(e -> toggleCard());
        }
    }
    
    public void toggleCard() {
        expanded = !expanded;
        contentArea.setVisible(expanded);
        contentArea.setManaged(expanded);
        
        RotateTransition rotate = new RotateTransition(Duration.millis(200), expandIcon);
        rotate.setToAngle(expanded ? 0 : 180);
        rotate.play();
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        contentArea.setVisible(expanded);
        contentArea.setManaged(expanded);
    }
}
```

## 4. 主视图结构

**MainView.fxml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<BorderPane xmlns:fx="http://javafx.com/fxml" 
            stylesheets="@../css/main.css"
            styleClass="main-window">
    
    <!-- 顶部: 标题栏 + 标签栏 -->
    <top>
        <VBox>
            <!-- 标题栏 -->
            <HBox styleClass="title-bar">
                <ImageView fitWidth="24" fitHeight="24">
                    <image><Image url="@../images/logo.png"/></image>
                </ImageView>
                <Label text="Aurora Launcher" styleClass="app-title"/>
                <Region HBox.hgrow="ALWAYS"/>
                <HBox styleClass="window-controls">
                    <Button onAction="#minimize" styleClass="btn-minimize"/>
                    <Button onAction="#close" styleClass="btn-close"/>
                </HBox>
            </HBox>
            
            <!-- 标签栏 -->
            <HBox id="tabBar" styleClass="tab-bar">
                <Button id="launch" text="%tab.launch" onAction="#onLaunchTab" 
                        styleClass="tab-btn, tab-active"/>
                <Button id="download" text="%tab.download" onAction="#onDownloadTab" 
                        styleClass="tab-btn"/>
                <Button id="settings" text="%tab.settings" onAction="#onSettingsTab" 
                        styleClass="tab-btn"/>
            </HBox>
        </VBox>
    </top>
    
    <!-- 中央: 内容区 -->
    <center>
        <StackPane fx:id="contentArea"/>
    </center>
    
</BorderPane>
```

## 5. 标签页视图

**LaunchView.fxml (启动页):**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ScrollPane xmlns:fx="http://javafx.com/fxml" 
            styleClass="page-container"
            fitToWidth="true">
    
    <VBox spacing="15" styleClass="page-content">
        
        <!-- 快速启动卡片 -->
        <fx:include source="components/QuickLaunchCard.fxml"/>
        
        <!-- 实例列表卡片 -->
        <fx:include source="components/InstanceListCard.fxml"/>
        
        <!-- 公告卡片 -->
        <fx:include source="components/AnnouncementCard.fxml"/>
        
    </VBox>
</ScrollPane>
```

**DownloadView.fxml (下载页):**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<BorderPane xmlns:fx="http://javafx.com/fxml" styleClass="page-container">
    
    <!-- 子标签栏 -->
    <top>
        <HBox styleClass="sub-tab-bar">
            <Button text="%download.version" onAction="#onVersionTab" styleClass="sub-tab-btn, active"/>
            <Button text="%download.mod" onAction="#onModTab" styleClass="sub-tab-btn"/>
            <Button text="%download.modpack" onAction="#onModpackTab" styleClass="sub-tab-btn"/>
            <Button text="%download.resource" onAction="#onResourceTab" styleClass="sub-tab-btn"/>
            <Button text="%download.shader" onAction="#onShaderTab" styleClass="sub-tab-btn"/>
        </HBox>
    </top>
    
    <!-- 子内容区 -->
    <center>
        <StackPane fx:id="subContentArea"/>
    </center>
    
</BorderPane>
```

**SettingsView.fxml (设置页):**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<BorderPane xmlns:fx="http://javafx.com/fxml" styleClass="page-container">
    
    <!-- 子标签栏 -->
    <top>
        <HBox styleClass="sub-tab-bar">
            <Button text="%settings.launch" onAction="#onLaunchTab" styleClass="sub-tab-btn, active"/>
            <Button text="%settings.download" onAction="#onDownloadTab" styleClass="sub-tab-btn"/>
            <Button text="%settings.theme" onAction="#onThemeTab" styleClass="sub-tab-btn"/>
            <Button text="%settings.account" onAction="#onAccountTab" styleClass="sub-tab-btn"/>
            <Button text="%settings.about" onAction="#onAboutTab" styleClass="sub-tab-btn"/>
        </HBox>
    </top>
    
    <!-- 子内容区 -->
    <center>
        <StackPane fx:id="subContentArea"/>
    </center>
    
</BorderPane>
```

## 6. 依赖注入

```java
public class ServiceLocator {
    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
    
    public static <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        return (T) services.get(type);
    }
    
    public static void initialize() {
        register(ConfigManager.class, new ConfigManager());
        register(InstanceManager.class, new InstanceManager());
        register(ModManager.class, new ModManager());
        register(DownloadEngine.class, new DownloadEngine());
        register(AccountManager.class, new AccountManager());
        register(ModrinthClient.class, new ModrinthClient());
        register(CurseForgeClient.class, new CurseForgeClient());
    }
}
```

## 7. 事件总线

```java
public class UiEventBus {
    private final EventBus eventBus = new EventBus();
    
    public void register(Object listener) {
        eventBus.register(listener);
    }
    
    public void post(Object event) {
        Platform.runLater(() -> eventBus.post(event));
    }
}

// 事件类型
public class InstanceLaunchEvent {
    private String instanceId;
}

public class DownloadProgressEvent {
    private String taskId;
    private double progress;
}

public class NotificationEvent {
    private String message;
    private NotificationType type;
}
```

## 8. 资源目录结构

```
src/main/resources/
├── fxml/
│   ├── MainView.fxml
│   ├── LaunchView.fxml
│   ├── DownloadView.fxml
│   ├── SettingsView.fxml
│   ├── components/
│   │   ├── QuickLaunchCard.fxml
│   │   ├── InstanceListCard.fxml
│   │   ├── AnnouncementCard.fxml
│   │   └── ...
│   ├── download/
│   │   ├── VersionView.fxml
│   │   ├── ModView.fxml
│   │   ├── ModpackView.fxml
│   │   ├── ResourceView.fxml
│   │   └── ShaderView.fxml
│   └── settings/
│       ├── LaunchView.fxml
│       ├── DownloadView.fxml
│       ├── ThemeView.fxml
│       ├── AccountView.fxml
│       └── AboutView.fxml
├── css/
│   ├── main.css
│   ├── theme-dark.css
│   ├── theme-light.css
│   └── components/
│       ├── card.css
│       ├── button.css
│       └── ...
├── i18n/
│   ├── messages_zh_CN.properties
│   └── messages_en_US.properties
└── images/
    ├── logo.png
    ├── icons/
    └── ...
```