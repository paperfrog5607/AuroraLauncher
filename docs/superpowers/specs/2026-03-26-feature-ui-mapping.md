# Aurora 功能-UI 映射文档

> **版本**: 2.0
> **日期**: 2026-03-26
> **UI 风格**: Switch + 底部导航 + 深色主题

---

## 1. UI 布局设计

### 1.1 整体布局 (Switch 风格)

```
┌─────────────────────────────────────────────────────────────┐
│                                             [🔔] [─] [✕]    │  ← 顶栏 (极简)
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                                                             │
│                                                             │
│                      主内容区域                              │
│                                                             │
│                                                             │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│   🏠 启动   ⬇ 下载   🎨 创作   ⚙ 更多                     │  ← 底部 Dock
└─────────────────────────────────────────────────────────────┘
```

### 1.2 通知系统 (Switch 风格)

```
通知弹出 (右上角):
┌─────────────────────────────────┐
│ 🔔 下载完成                      │
│    Minecraft 1.21.4 已安装      │
└─────────────────────────────────┘

通知中心 (点击 🔔 展开):
┌─────────────────────────────────┐
│ 今天                            │
│ ├─ 下载完成: Mod 1.0.0         │
│ └─ 更新可用: Fabric 0.15.8     │
│                                 │
│ 昨天                            │
│ └─ 游戏已启动: 实例 1          │
└─────────────────────────────────┘
```

---

## 2. 功能模块划分

### 模块一：启动页 (Launch)

| 功能 | UI 位置 | FXML | 状态 |
|------|---------|------|------|
| 快速启动卡片 | 主内容区顶部 | QuickLaunchCard.fxml | ✅ |
| 版本选择下拉 | 快速启动卡片内 | QuickLaunchCard.fxml | ✅ |
| 账号显示 | 快速启动卡片内 | QuickLaunchCard.fxml | ✅ |
| 开始游戏按钮 | 快速启动卡片内 | QuickLaunchCard.fxml | ✅ |
| 实例列表 | 主内容区中部 | InstanceListCard.fxml | ✅ |
| 新建实例 | 实例列表卡片内 | InstanceListCard.fxml | ✅ |
| 打开实例目录 | 实例列表卡片内 | InstanceListCard.fxml | ✅ |
| 实例卡片 | 横向滚动网格 | InstanceItem.fxml | ✅ |
| 实例图标 | 实例卡片内 | InstanceItem.fxml | ✅ |
| 实例名称/版本 | 实例卡片内 | InstanceItem.fxml | ✅ |
| 实例启动按钮 | 实例卡片悬停显示 | InstanceItem.fxml | ⬜ |
| 实例编辑菜单 | 右键菜单 | - | ⬜ |
| 实例删除确认 | 对话框 | - | ⬜ |

### 模块二：下载页 (Download)

| 功能 | UI 位置 | FXML | 状态 |
|------|---------|------|------|
| 子标签导航 | 顶部子标签栏 | DownloadView.fxml | ✅ |
| 版本下载 | 版本标签页 | download/VersionView.fxml | ✅ |
| 模组下载 | 模组标签页 | download/ModView.fxml | ✅ |
| 整合包下载 | 整合包标签页 | download/ModpackView.fxml | ✅ |
| 资源包下载 | 资源包标签页 | download/ResourceView.fxml | ✅ |
| 光影下载 | 光影标签页 | download/ShaderView.fxml | ✅ |
| 搜索框 | 各标签页顶部 | - | ✅ |
| 筛选器 | 搜索框旁 | - | ✅ |
| 搜索结果列表 | 主内容区 | SearchResultItem.fxml | ✅ |
| 模组详情弹窗 | 对话框 | dialogs/ModDetailDialog.fxml | ✅ |
| 下载进度条 | 结果项内 | - | ✅ |
| 下载暂停/取消 | 进度条旁 | - | ⬜ |

### 模块三：创作页 (Creator)

| 功能 | UI 位置 | FXML | 状态 |
|------|---------|------|------|
| 子标签导航 | 顶部子标签栏 | CreatorView.fxml | ⬜ 重构 |
| 创建整合包 | 基础标签页 | creator/ModpackCreate.fxml | ⬜ |
| 导入整合包 | 基础标签页 | creator/ModpackImport.fxml | ⬜ |
| 导出整合包 | 基础标签页 | creator/ModpackExport.fxml | ⬜ |
| 分享整合包 | 基础标签页 | creator/ModpackShare.fxml | ⬜ |
| KubeJS 脚本 | 脚本标签页 | creator/KubeJSView.fxml | ⬜ |
| CraftTweaker | 脚本标签页 | creator/CTScriptView.fxml | ⬜ |
| 数据包可视化 | 工具标签页 | creator/DatapackView.fxml | ❌ 缺失 |
| 资源包可视化 | 工具标签页 | creator/ResourcepackView.fxml | ❌ 缺失 |
| 备份管理 | 工具标签页 | creator/BackupView.fxml | ⬜ |
| 验证/修复 | 工具标签页 | creator/VerifyView.fxml | ⬜ |
| 模板管理 | 高级标签页 | creator/TemplateView.fxml | ⬜ |
| 世界生成配置 | 高级标签页 | creator/WorldGenView.fxml | ⬜ |
| 标签管理 | 高级标签页 | creator/TagView.fxml | ⬜ |
| 进度自定义 | 高级标签页 | creator/AdvancementView.fxml | ⬜ |
| 战利品表 | 高级标签页 | creator/LootTableView.fxml | ⬜ |
| 配方自定义 | 高级标签页 | creator/RecipeView.fxml | ⬜ |

### 模块四：更多页 (Settings)

| 功能 | UI 位置 | FXML | 状态 |
|------|---------|------|------|
| 子标签导航 | 顶部子标签栏 | SettingsView.fxml | ✅ |
| Java 路径设置 | 启动标签页 | settings/LaunchView.fxml | ✅ |
| 内存设置滑块 | 启动标签页 | components/MemorySlider.fxml | ✅ |
| JVM 参数设置 | 启动标签页 | settings/LaunchView.fxml | ✅ |
| 下载并发数 | 下载标签页 | settings/DownloadView.fxml | ✅ |
| 代理设置 | 下载标签页 | settings/DownloadView.fxml | ✅ |
| 镜像源选择 | 下载标签页 | settings/DownloadView.fxml | ✅ |
| 主题切换 | 主题标签页 | settings/ThemeView.fxml | ✅ |
| 背景壁纸设置 | 主题标签页 | settings/ThemeView.fxml | ✅ |
| 动画开关 | 主题标签页 | settings/ThemeView.fxml | ⬜ |
| 账号登录 | 账号标签页 | settings/AccountView.fxml | ✅ |
| 微软登录 | 账号标签页 | settings/AccountView.fxml | ✅ |
| 离线账号 | 账号标签页 | settings/AccountView.fxml | ✅ |
| 账号切换 | 账号标签页 | settings/AccountView.fxml | ✅ |
| 版本信息 | 关于标签页 | settings/AboutView.fxml | ✅ |
| 检查更新 | 关于标签页 | settings/AboutView.fxml | ⬜ |
| 导出日志 | 关于标签页 | settings/AboutView.fxml | ⬜ |

### 模块五：网络联机 (Network)

| 功能 | UI 位置 | FXML | 状态 |
|------|---------|------|------|
| 创建房间 | 主内容区 | NetworkView.fxml | ✅ |
| 加入房间 | 主内容区 | NetworkView.fxml | ✅ |
| 房间号输入 | 加入区域 | NetworkView.fxml | ✅ |
| UUID 链接输入 | 加入区域 | NetworkView.fxml | ✅ |
| 房间成员列表 | 主内容区 | NetworkView.fxml | ✅ |
| 踢出成员 | 成员列表 | - | ⬜ |
| 房间设置 | 设置按钮 | - | ⬜ |
| 聊天功能 | 主内容区 | - | ⬜ |

### 模块六：通知系统 (Notification)

| 功能 | UI 位置 | FXML | 状态 |
|------|---------|------|------|
| 通知图标 | 顶栏右侧 | MainView.fxml | ⬜ 新增 |
| 通知弹出 | 右上角浮动 | NotificationPopup.fxml | ❌ 缺失 |
| 通知中心 | 点击展开面板 | NotificationCenter.fxml | ❌ 缺失 |
| 通知列表 | 通知中心内 | NotificationItem.fxml | ❌ 缺失 |
| 清除通知 | 通知中心底部 | - | ⬜ |
| 通知分类 | 通知中心内 | - | ⬜ |

### 模块七：对话框组件

| 功能 | FXML | 状态 |
|------|------|------|
| 新建实例向导 | dialogs/CreateInstanceDialog.fxml | ✅ |
| 模组详情 | dialogs/ModDetailDialog.fxml | ✅ |
| 确认对话框 | dialogs/ConfirmDialog.fxml | ⬜ |
| 进度对话框 | dialogs/ProgressDialog.fxml | ⬜ |
| 错误对话框 | dialogs/ErrorDialog.fxml | ⬜ |

---

## 3. 底部导航 Dock 设计

### 3.1 导航项

| 图标 | 名称 | 页面 | 快捷键 |
|------|------|------|--------|
| 🏠 | 启动 | LaunchView | F1 |
| ⬇ | 下载 | DownloadView | F2 |
| 🎨 | 创作 | CreatorView | F3 |
| ⚙ | 更多 | SettingsView | F4 |

### 3.2 Dock 样式

```css
.dock {
    -fx-background-color: rgba(26, 26, 46, 0.95);
    -fx-background-radius: 24px 24px 0 0;
    -fx-padding: 12px 32px;
    -fx-border-color: rgba(139, 92, 246, 0.2);
    -fx-border-width: 1px 0 0 0;
    -fx-spacing: 48px;
    -fx-alignment: center;
}

.dock-item {
    -fx-background-color: transparent;
    -fx-text-fill: #B4B4C8;
    -fx-padding: 8px 16px;
    -fx-background-radius: 12px;
    -fx-cursor: hand;
    -fx-spacing: 6px;
    -fx-alignment: center;
}

.dock-item:hover {
    -fx-background-color: rgba(139, 92, 246, 0.15);
    -fx-text-fill: #FFFFFF;
}

.dock-item.active {
    -fx-background-color: #8B5CF6;
    -fx-text-fill: white;
}
```

---

## 4. 实现优先级

### P0 - 核心必须
1. 底部导航 Dock 重构
2. 通知系统 UI
3. 启动页完善
4. 下载页完善

### P1 - 重要功能
1. 创作页可视化编辑器
2. 网络联机完善
3. 设置页完善

### P2 - 增强体验
1. 对话框组件
2. 快捷键系统
3. 动画效果

---

## 5. 缺失功能统计

| 类别 | 缺失数量 |
|------|----------|
| FXML 文件 | 6 个 |
| Controller | 4 个 |
| CSS 组件 | 2 个 |

### 缺失文件清单

**创作页**:
- creator/DatapackView.fxml ❌
- creator/ResourcepackView.fxml ❌
- creator/DatapackController.java ❌
- creator/ResourcepackController.java ❌

**通知系统**:
- NotificationPopup.fxml ❌
- NotificationCenter.fxml ❌
- NotificationItem.fxml ❌

**CSS**:
- notification.css (需重构)
- dock.css ❌ (新增)