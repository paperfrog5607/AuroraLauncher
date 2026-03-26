# Aurora UI 重构设计文档

> **日期**: 2026-03-26
> **版本**: 2.0
> **风格**: Switch + PCL + 现代化

---

## 1. 设计理念

### 1.1 核心原则
- **简洁**：界面不混乱，层次清晰
- **现代化**：符合当下设计趋势
- **品牌特色**：Aurora 紫色 + 霓虹发光

### 1.2 设计参考
- **Switch UI**：玻璃效果、横向滚动、大图标网格
- **PCL 布局**：顶部标签导航、卡片式设计
- **现代化**：深色主题、渐变、发光效果

---

## 2. 视觉规范

### 2.1 颜色系统

```css
:root {
    /* 主色 - Aurora 紫色 */
    --primary: #8B5CF6;
    --primary-hover: #A78BFA;
    --primary-pressed: #7C3AED;
    --primary-glow: rgba(139, 92, 246, 0.5);
    
    /* 背景 - 深色渐变 */
    --bg-primary: #0F0F1A;
    --bg-secondary: #1A1A2E;
    --bg-card: rgba(30, 30, 50, 0.8);
    --bg-glass: rgba(26, 26, 46, 0.7);
    
    /* 文字 */
    --text-primary: #FFFFFF;
    --text-secondary: #B4B4C8;
    --text-tertiary: #6B6B80;
    
    /* 霓虹发光 */
    --neon-glow: 0 0 20px var(--primary-glow);
    --neon-glow-strong: 0 0 30px var(--primary-glow), 0 0 60px var(--primary-glow);
    
    /* 玻璃效果 */
    --glass-bg: rgba(26, 26, 46, 0.7);
    --glass-border: rgba(139, 92, 246, 0.2);
    --glass-blur: blur(20px);
}
```

### 2.2 圆角系统
```css
--radius-sm: 8px;
--radius-md: 12px;
--radius-lg: 16px;
--radius-xl: 24px;
```

### 2.3 阴影系统
```css
--shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.3);
--shadow-md: 0 4px 16px rgba(0, 0, 0, 0.4);
--shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.5);
--shadow-glow: 0 0 20px rgba(139, 92, 246, 0.4);
```

---

## 3. 布局规范

### 3.1 整体布局
```
┌─────────────────────────────────────────────────┐
│  [Logo]   启动  下载  更多          [─] [×]     │  ← 玻璃顶栏 (48px)
├─────────────────────────────────────────────────┤
│                                                 │
│                                                 │
│              主内容区域                          │
│                                                 │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 3.2 顶栏设计
- 高度：48px
- 背景：玻璃效果（半透明 + 模糊）
- 底部细线：1px primary 渐变
- 左侧：Logo
- 中间：导航标签
- 右侧：窗口控制按钮

### 3.3 导航标签
- 文字为主，无图标
- 选中态：底部 2px 发光指示条
- 悬停态：文字变亮 + 轻微发光

---

## 4. 组件规范

### 4.1 玻璃卡片
```css
.glass-card {
    -fx-background-color: var(--glass-bg);
    -fx-background-radius: var(--radius-lg);
    -fx-border-color: var(--glass-border);
    -fx-border-width: 1px;
    -fx-border-radius: var(--radius-lg);
    -fx-effect: var(--shadow-glow);
}
```

### 4.2 霓虹按钮
```css
.neon-button {
    -fx-background-color: var(--primary);
    -fx-text-fill: white;
    -fx-background-radius: var(--radius-md);
    -fx-padding: 12px 24px;
    -fx-cursor: hand;
}

.neon-button:hover {
    -fx-effect: var(--neon-glow);
}

.neon-button:pressed {
    -fx-background-color: var(--primary-pressed);
}
```

### 4.3 图标卡片（横向滚动）
```css
.icon-card {
    -fx-background-color: var(--bg-card);
    -fx-background-radius: var(--radius-lg);
    -fx-min-width: 140px;
    -fx-min-height: 160px;
    -fx-cursor: hand;
    -fx-transition: all 0.2s ease;
}

.icon-card:hover {
    -fx-background-color: var(--bg-secondary);
    -fx-effect: var(--neon-glow);
    -fx-scale-x: 1.02;
    -fx-scale-y: 1.02;
}
```

### 4.4 输入框
```css
.glass-input {
    -fx-background-color: var(--bg-card);
    -fx-background-radius: var(--radius-md);
    -fx-border-color: transparent;
    -fx-border-width: 2px;
    -fx-border-radius: var(--radius-md);
    -fx-padding: 10px 14px;
    -fx-text-fill: var(--text-primary);
}

.glass-input:focused {
    -fx-border-color: var(--primary);
    -fx-effect: var(--shadow-glow);
}
```

---

## 5. 页面规范

### 5.1 LaunchView（启动页）
```
┌─────────────────────────────────────────────────┐
│                                                 │
│   ┌─────────────────────────────────────────┐   │
│   │  ▶ 开始游戏           [版本选择 ▼]     │   │  ← 快速启动卡片
│   │  玩家名称 | 微软账号                      │   │
│   └─────────────────────────────────────────┘   │
│                                                 │
│   我的实例                        [+] [📁]      │
│   ──────────────────────────────────────────   │
│   ← [实例1] [实例2] [实例3] [实例4] →         │  ← 横向滚动
│                                                 │
└─────────────────────────────────────────────────┘
```

### 5.2 DownloadView（下载中心）
```
┌─────────────────────────────────────────────────┐
│  [版本] [模组] [整合包] [资源包] [光影]         │  ← 子标签
├─────────────────────────────────────────────────┤
│  🔍 搜索...                    [筛选 ▼]        │
├─────────────────────────────────────────────────┤
│                                                 │
│   ← [项目1] [项目2] [项目3] [项目4] →           │  ← 横向滚动网格
│                                                 │
└─────────────────────────────────────────────────┘
```

### 5.3 SettingsView（设置页）
```
┌─────────────────────────────────────────────────┐
│  [启动] [下载] [主题] [账号] [关于]             │  ← 子标签
├─────────────────────────────────────────────────┤
│                                                 │
│   ┌─────────────────────────────────────────┐   │
│   │  Java 路径                               │   │
│   │  [___________] [检测] [浏览]            │   │
│   └─────────────────────────────────────────┘   │
│                                                 │
│   ┌─────────────────────────────────────────┐   │
│   │  内存设置                               │   │
│   │  ──●────────────────────── 4GB         │   │
│   └─────────────────────────────────────────┘   │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 5.4 CreatorView（整合包制作）
```
┌─────────────────────────────────────────────────┐
│  [基础] [脚本] [备份] [工具] [高级]             │  ← 子标签
├─────────────────────────────────────────────────┤
│                                                 │
│   ┌────────────┐  ┌────────────────────────┐   │
│   │            │  │                        │   │
│   │  文件树    │  │    可视化编辑器        │   │
│   │            │  │                        │   │
│   │            │  │                        │   │
│   └────────────┘  └────────────────────────┘   │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 5.5 NetworkView（P2P 联机）
```
┌─────────────────────────────────────────────────┐
│                                                 │
│   ┌─────────────────────────────────────────┐   │
│   │  创建房间              加入房间          │   │
│   │  ─────────────────────────────────────  │   │
│   │  [房间号: ABC123]     [输入房间号...]   │   │
│   │  [UUID链接]          [加入]            │   │
│   └─────────────────────────────────────────┘   │
│                                                 │
│   在线玩家：3                                   │
│   ─────────────────────────────────────────    │
│   [玩家1] [玩家2] [玩家3]                        │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 6. 动画规范

### 6.1 过渡动画
```css
/* 悬停放大 */
.icon-card:hover {
    -fx-transition: all 0.2s ease;
}

/* 渐入 */
.fade-in {
    -fx-opacity: 0;
    -fx-transition: opacity 0.3s ease;
}

.fade-in.show {
    -fx-opacity: 1;
}
```

### 6.2 霓虹脉冲（可选）
```css
@keyframes neon-pulse {
    0%, 100% { -fx-effect: 0 0 20px rgba(139, 92, 246, 0.4); }
    50% { -fx-effect: 0 0 30px rgba(139, 92, 246, 0.6); }
}
```

---

## 7. 实现清单

### 第一批：基础框架
- [ ] theme-dark.css 重构
- [ ] 玻璃效果 CSS 组件
- [ ] 霓虹发光效果
- [ ] MainView.fxml 重构
- [ ] 导航系统

### 第二批：功能页面
- [ ] LaunchView.fxml
- [ ] DownloadView.fxml
- [ ] SettingsView.fxml

### 第三批：高级功能
- [ ] CreatorView.fxml
- [ ] NetworkView.fxml

### 第四批：大排查
- [ ] 清理无用文件
- [ ] 清理无用代码
- [ ] 验证所有功能正常

---

## 8. 技术说明

### 8.1 JavaFX CSS 限制
JavaFX CSS 不支持：
- `backdrop-filter`（模糊效果）
- `box-shadow` 多层

解决方案：
- 使用 `-fx-effect: dropshadow()` 模拟
- 背景半透明用 RGBA 颜色

### 8.2 性能考虑
- 避免过度使用阴影（影响渲染性能）
- 动画时长控制在 200-300ms
- 横向滚动使用 `ScrollPane` + 自定义滚动逻辑
