# UI模块 - 主题与样式系统

## 1. 主题管理器

```java
public class ThemeManager {
    private static ThemeManager instance;
    private final Map<String, Theme> themes = new HashMap<>();
    private Theme currentTheme;
    private Path backgroundImage;
    
    private ThemeManager() {
        loadBuiltInThemes();
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    private void loadBuiltInThemes() {
        themes.put("dark", new Theme("dark", "/css/theme-dark.css", true));
        themes.put("light", new Theme("light", "/css/theme-light.css", false));
        themes.put("auto", new Theme("auto", null, isSystemDarkMode()) {
            @Override
            public String getStylesheet() {
                return isDark() ? "/css/theme-dark.css" : "/css/theme-light.css";
            }
        });
    }
    
    public void loadTheme(String themeName) {
        Theme theme = themes.getOrDefault(themeName, themes.get("dark"));
        this.currentTheme = theme;
        applyTheme(theme);
    }
    
    private void applyTheme(Theme theme) {
        String stylesheet = getClass().getResource(theme.getStylesheet()).toExternalForm();
        
        for (Window window : Window.getWindows()) {
            Scene scene = window.getScene();
            if (scene != null) {
                scene.getStylesheets().removeIf(s -> s.contains("theme-"));
                scene.getStylesheets().add(stylesheet);
            }
        }
    }
    
    public void setBackgroundImage(Path path) {
        this.backgroundImage = path;
        // 更新背景
    }
    
    public void resetBackground() {
        this.backgroundImage = null;
    }
    
    public Theme getCurrentTheme() { return currentTheme; }
    public List<String> getAvailableThemes() { return new ArrayList<>(themes.keySet()); }
    public boolean isDarkMode() { return currentTheme != null && currentTheme.isDark(); }
    
    private boolean isSystemDarkMode() {
        // 检测系统暗色模式
        return true;
    }
}

public class Theme {
    private String name;
    private String stylesheet;
    private boolean dark;
    
    public Theme(String name, String stylesheet, boolean dark) {
        this.name = name;
        this.stylesheet = stylesheet;
        this.dark = dark;
    }
    
    public String getName() { return name; }
    public String getStylesheet() { return stylesheet; }
    public boolean isDark() { return dark; }
}
```

## 2. CSS变量系统

**theme-dark.css:**

```css
.root {
    /* 主色调 - PCL风格紫色 */
    --primary: #8B5CF6;
    --primary-hover: #A78BFA;
    --primary-pressed: #7C3AED;
    --primary-light: rgba(139, 92, 246, 0.1);
    
    /* 背景色 */
    --bg-primary: #1A1A2E;
    --bg-secondary: #16213E;
    --bg-tertiary: #0F3460;
    --bg-card: #1F2937;
    --bg-hover: #374151;
    --bg-selected: #4B5563;
    
    /* 文字色 */
    --text-primary: #F9FAFB;
    --text-secondary: #D1D5DB;
    --text-tertiary: #9CA3AF;
    --text-disabled: #6B7280;
    
    /* 边框色 */
    --border: #374151;
    --border-light: #4B5563;
    --border-focus: var(--primary);
    
    /* 状态色 */
    --success: #10B981;
    --warning: #F59E0B;
    --error: #EF4444;
    --info: #3B82F6;
    
    /* 阴影 */
    --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.4);
    --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.5);
    
    /* 圆角 */
    --radius-sm: 4px;
    --radius-md: 8px;
    --radius-lg: 12px;
    
    /* 过渡 */
    --transition-fast: 0.15s ease;
    --transition-normal: 0.3s ease;
}

/* 滚动条 */
.scroll-bar {
    -fx-background-color: transparent;
}

.scroll-bar .thumb {
    -fx-background-color: var(--bg-hover);
    -fx-background-radius: var(--radius-sm);
}

.scroll-bar .thumb:hover {
    -fx-background-color: var(--bg-selected);
}
```

**theme-light.css:**

```css
.root {
    /* 主色调 */
    --primary: #7C3AED;
    --primary-hover: #8B5CF6;
    --primary-pressed: #6D28D9;
    --primary-light: rgba(124, 58, 237, 0.1);
    
    /* 背景色 */
    --bg-primary: #F9FAFB;
    --bg-secondary: #F3F4F6;
    --bg-tertiary: #E5E7EB;
    --bg-card: #FFFFFF;
    --bg-hover: #E5E7EB;
    --bg-selected: #D1D5DB;
    
    /* 文字色 */
    --text-primary: #111827;
    --text-secondary: #4B5563;
    --text-tertiary: #9CA3AF;
    --text-disabled: #D1D5DB;
    
    /* 边框色 */
    --border: #E5E7EB;
    --border-light: #F3F4F6;
    --border-focus: var(--primary);
    
    /* 状态色 */
    --success: #10B981;
    --warning: #F59E0B;
    --error: #EF4444;
    --info: #3B82F6;
    
    /* 阴影 */
    --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
    --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.15);
    
    /* 圆角 */
    --radius-sm: 4px;
    --radius-md: 8px;
    --radius-lg: 12px;
}
```

## 3. 主布局样式

**main.css:**

```css
/* 主窗口 */
.main-window {
    -fx-background-color: var(--bg-primary);
}

/* 标题栏 */
.title-bar {
    -fx-background-color: var(--bg-secondary);
    -fx-padding: 8px 12px;
    -fx-spacing: 8px;
    -fx-alignment: center-left;
}

.title-bar .app-title {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: var(--text-primary);
}

.window-controls {
    -fx-spacing: 8px;
}

.window-controls .button {
    -fx-background-radius: 50%;
    -fx-min-width: 12px;
    -fx-min-height: 12px;
    -fx-padding: 0;
    -fx-border-width: 0;
}

.btn-close { -fx-background-color: #EF4444; }
.btn-minimize { -fx-background-color: #F59E0B; }

/* 标签栏 */
.tab-bar {
    -fx-background-color: var(--bg-secondary);
    -fx-padding: 0 12px;
    -fx-spacing: 4px;
    -fx-border-color: var(--border);
    -fx-border-width: 0 0 1px 0;
}

.tab-btn {
    -fx-background-color: transparent;
    -fx-text-fill: var(--text-secondary);
    -fx-padding: 12px 20px;
    -fx-font-size: 14px;
    -fx-border-width: 0 0 2px 0;
    -fx-border-color: transparent;
    -fx-cursor: hand;
}

.tab-btn:hover {
    -fx-text-fill: var(--text-primary);
    -fx-background-color: var(--bg-hover);
}

.tab-btn.active, .tab-active {
    -fx-text-fill: var(--primary);
    -fx-border-color: var(--primary);
}

/* 子标签栏 */
.sub-tab-bar {
    -fx-background-color: var(--bg-secondary);
    -fx-padding: 8px 16px;
    -fx-spacing: 8px;
    -fx-border-color: var(--border);
    -fx-border-width: 0 0 1px 0;
}

.sub-tab-btn {
    -fx-background-color: transparent;
    -fx-text-fill: var(--text-secondary);
    -fx-padding: 8px 16px;
    -fx-font-size: 13px;
    -fx-background-radius: var(--radius-md);
    -fx-cursor: hand;
}

.sub-tab-btn:hover {
    -fx-background-color: var(--bg-hover);
}

.sub-tab-btn.active {
    -fx-background-color: var(--primary);
    -fx-text-fill: white;
}

/* 页面容器 */
.page-container {
    -fx-background-color: var(--bg-primary);
    -fx-fit-to-width: true;
}

.page-content {
    -fx-padding: 16px;
    -fx-spacing: 16px;
}
```

## 4. 卡片样式

**card.css:**

```css
/* 卡片基础 */
.card {
    -fx-background-color: var(--bg-card);
    -fx-background-radius: var(--radius-lg);
    -fx-effect: var(--shadow-md);
}

.card-header {
    -fx-padding: 16px;
    -fx-spacing: 8px;
    -fx-alignment: center-left;
    -fx-cursor: hand;
}

.card-title {
    -fx-font-size: 16px;
    -fx-font-weight: bold;
    -fx-text-fill: var(--text-primary);
}

.card-content {
    -fx-padding: 0 16px 16px 16px;
    -fx-spacing: 12px;
}

.expand-icon {
    -fx-cursor: hand;
}

/* 实例项 */
.instance-item {
    -fx-background-color: var(--bg-tertiary);
    -fx-background-radius: var(--radius-md);
    -fx-padding: 12px;
    -fx-spacing: 6px;
    -fx-alignment: center;
    -fx-pref-width: 140px;
    -fx-cursor: hand;
}

.instance-item:hover {
    -fx-background-color: var(--bg-hover);
    -fx-effect: var(--shadow-md);
}

.instance-icon {
    -fx-fit-width: 48px;
    -fx-fit-height: 48px;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2);
}

.instance-name {
    -fx-font-size: 13px;
    -fx-font-weight: bold;
    -fx-text-fill: var(--text-primary);
}

.instance-version {
    -fx-font-size: 11px;
    -fx-text-fill: var(--text-secondary);
}

.instance-loader {
    -fx-font-size: 10px;
    -fx-text-fill: var(--text-tertiary);
}
```

## 5. 按钮样式

**button.css:**

```css
/* 基础按钮 */
.button {
    -fx-background-color: var(--bg-tertiary);
    -fx-text-fill: var(--text-primary);
    -fx-background-radius: var(--radius-md);
    -fx-padding: 8px 16px;
    -fx-font-size: 13px;
    -fx-cursor: hand;
    -fx-border-width: 0;
}

.button:hover {
    -fx-background-color: var(--bg-hover);
}

.button:pressed {
    -fx-background-color: var(--bg-selected);
}

/* 主要按钮 */
.btn-primary {
    -fx-background-color: var(--primary);
    -fx-text-fill: white;
}

.btn-primary:hover {
    -fx-background-color: var(--primary-hover);
}

.btn-primary:pressed {
    -fx-background-color: var(--primary-pressed);
}

/* 小按钮 */
.btn-sm {
    -fx-padding: 4px 12px;
    -fx-font-size: 12px;
}

/* 图标按钮 */
.btn-icon {
    -fx-background-radius: 50%;
    -fx-min-width: 32px;
    -fx-min-height: 32px;
    -fx-padding: 0;
    -fx-background-color: transparent;
}

.btn-icon:hover {
    -fx-background-color: var(--bg-hover);
}

/* 危险按钮 */
.btn-danger {
    -fx-background-color: var(--error);
    -fx-text-fill: white;
}

/* 禁用状态 */
.button:disabled {
    -fx-background-color: var(--bg-tertiary);
    -fx-text-fill: var(--text-disabled);
    -fx-opacity: 0.5;
}
```

## 6. 输入框样式

**input.css:**

```css
/* 文本输入框 */
.text-field {
    -fx-background-color: var(--bg-tertiary);
    -fx-background-radius: var(--radius-md);
    -fx-border-color: transparent;
    -fx-border-width: 2px;
    -fx-border-radius: var(--radius-md);
    -fx-padding: 8px 12px;
    -fx-font-size: 13px;
    -fx-text-fill: var(--text-primary);
    -fx-prompt-text-fill: var(--text-tertiary);
}

.text-field:focused {
    -fx-border-color: var(--primary);
}

/* 下拉框 */
.combo-box {
    -fx-background-color: var(--bg-tertiary);
    -fx-background-radius: var(--radius-md);
}

.combo-box .list-cell {
    -fx-background-color: var(--bg-card);
    -fx-text-fill: var(--text-primary);
}

.combo-box-popup .list-view {
    -fx-background-color: var(--bg-card);
    -fx-effect: var(--shadow-lg);
}

/* 滑块 */
.slider {
    -fx-background-color: transparent;
}

.slider .track {
    -fx-background-color: var(--bg-tertiary);
    -fx-background-radius: var(--radius-sm);
    -fx-pref-height: 6px;
}

.slider .thumb {
    -fx-background-color: var(--primary);
    -fx-background-radius: 50%;
}

/* 复选框 */
.check-box {
    -fx-text-fill: var(--text-primary);
}

.check-box .box {
    -fx-background-color: var(--bg-tertiary);
    -fx-background-radius: var(--radius-sm);
    -fx-border-color: var(--border);
}

.check-box:selected .box {
    -fx-background-color: var(--primary);
    -fx-border-color: var(--primary);
}

.check-box:selected .mark {
    -fx-background-color: white;
}
```

## 7. 列表样式

**list.css:**

```css
.list-view {
    -fx-background-color: transparent;
    -fx-border-color: transparent;
}

.list-cell {
    -fx-background-color: transparent;
    -fx-text-fill: var(--text-primary);
    -fx-padding: 8px 12px;
    -fx-border-color: transparent;
    -fx-border-width: 0 0 1px 0;
}

.list-cell:hover {
    -fx-background-color: var(--bg-hover);
}

.list-cell:selected {
    -fx-background-color: var(--primary-light);
}

.list-cell:empty {
    -fx-background-color: transparent;
}
```

## 8. 通知样式

**notification.css:**

```css
.notification {
    -fx-background-radius: var(--radius-md);
    -fx-padding: 12px 16px;
    -fx-spacing: 10px;
    -fx-effect: var(--shadow-lg);
    -fx-min-width: 280px;
}

.notification--info {
    -fx-background-color: var(--info);
}

.notification--success {
    -fx-background-color: var(--success);
}

.notification--warning {
    -fx-background-color: var(--warning);
}

.notification--error {
    -fx-background-color: var(--error);
}

.notification .message {
    -fx-text-fill: white;
    -fx-font-size: 13px;
}

.notification .button {
    -fx-background-color: rgba(255, 255, 255, 0.2);
    -fx-text-fill: white;
}
```

## 9. 标签样式

**tag.css:**

```css
.tag {
    -fx-background-radius: var(--radius-sm);
    -fx-padding: 2px 8px;
    -fx-font-size: 11px;
    -fx-text-fill: var(--text-primary);
}

.tag-source {
    -fx-background-color: var(--primary-light);
    -fx-text-fill: var(--primary);
}

.tag-loader {
    -fx-background-color: rgba(16, 185, 129, 0.1);
    -fx-text-fill: var(--success);
}

.tag-version {
    -fx-background-color: rgba(59, 130, 246, 0.1);
    -fx-text-fill: var(--info);
}

/* 状态标签 */
.status-badge {
    -fx-background-radius: var(--radius-sm);
    -fx-padding: 2px 6px;
    -fx-font-size: 10px;
    -fx-text-fill: white;
}

.status-running {
    -fx-background-color: var(--success);
}

.status-updating {
    -fx-background-color: var(--warning);
}

.status-error {
    -fx-background-color: var(--error);
}
```

## 10. 对话框样式

**dialog.css:**

```css
.dialog-pane {
    -fx-background-color: var(--bg-card);
    -fx-background-radius: var(--radius-lg);
    -fx-effect: var(--shadow-lg);
}

.dialog-pane .content {
    -fx-padding: 16px;
}

.dialog-pane .button-bar {
    -fx-padding: 12px 16px;
    -fx-background-color: var(--bg-secondary);
    -fx-background-radius: 0 0 var(--radius-lg) var(--radius-lg);
}
```

## 11. 动画

```css
/* 过渡动画 */
.card {
    -fx-transition: transform var(--transition-normal),
                    shadow var(--transition-normal);
}

.card:hover {
    -fx-effect: var(--shadow-lg);
}

/* 淡入动画 */
.fade-in {
    -fx-opacity: 0;
}

.fade-in.active {
    -fx-opacity: 1;
    -fx-transition: opacity var(--transition-normal);
}
```