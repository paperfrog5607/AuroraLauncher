# 通知系统设计文档

> **风格**: Nintendo Switch 通知中心
> **日期**: 2026-03-26

---

## 1. 设计参考

### Switch 通知特点
- 右上角通知图标 (🔔)
- 弹出通知从右上角滑入
- 点击图标展开通知中心
- 通知按日期分组
- 支持清除单个/全部通知
- 不同类型通知有不同图标

---

## 2. UI 设计

### 2.1 通知图标 (顶栏)

```
┌─────────────────────────────────────────────────┐
│                                    [🔔(3)] [─][✕]│
└─────────────────────────────────────────────────┘
```

- 位置：顶栏右侧，窗口控制按钮左边
- 样式：图标 + 红点数字徽章
- 点击：展开通知中心

### 2.2 通知弹出 (Toast)

```
                    ┌──────────────────────────────┐
                    │ ⬇ Minecraft 1.21.4          │
                    │   下载完成                   │
                    │                [×]           │
                    └──────────────────────────────┘
```

- 位置：右上角，从屏幕外滑入
- 动画：滑入 300ms，停留 3s，滑出 300ms
- 可点击关闭
- 点击内容执行对应操作

### 2.3 通知中心面板

```
┌──────────────────────────────────────┐
│  通知                      [全部清除] │
├──────────────────────────────────────┤
│  今天                                │
│  ┌────────────────────────────────┐  │
│  │ ⬇ 下载完成                     │  │
│  │    Minecraft 1.21.4            │  │
│  │    10:30                       │  │
│  └────────────────────────────────┘  │
│  ┌────────────────────────────────┐  │
│  │ 🔄 更新可用                     │  │
│  │    Fabric Loader 0.15.8        │  │
│  │    09:15                       │  │
│  └────────────────────────────────┘  │
│                                      │
│  昨天                                │
│  ┌────────────────────────────────┐  │
│  │ 🎮 游戏已启动                   │  │
│  │    我的整合包                   │  │
│  │    14:20                       │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
```

- 位置：顶栏下方，从右侧滑入
- 宽度：320px
- 高度：最大 400px，超出滚动
- 背景：半透明深色 + 模糊

---

## 3. 通知类型

| 类型 | 图标 | 说明 |
|------|------|------|
| DOWNLOAD | ⬇ | 下载相关 |
| UPDATE | 🔄 | 更新可用 |
| GAME | 🎮 | 游戏启动/关闭 |
| SUCCESS | ✅ | 操作成功 |
| ERROR | ❌ | 错误提示 |
| WARNING | ⚠ | 警告信息 |
| INFO | ℹ | 一般信息 |
| FRIEND | 👤 | 好友相关 (P2P) |

---

## 4. 数据结构

```java
public class Notification {
    private String id;
    private NotificationType type;
    private String title;
    private String message;
    private Instant timestamp;
    private boolean read;
    private String action;      // 点击后执行的操作
    private String actionData;  // 操作参数
}

public enum NotificationType {
    DOWNLOAD, UPDATE, GAME, SUCCESS, ERROR, WARNING, INFO, FRIEND
}
```

---

## 5. 交互流程

### 5.1 显示通知

```
NotificationManager.show(Notification notification)
    ↓
检查当前显示数量 (< 3)
    ↓
创建 NotificationToast
    ↓
添加到 toastContainer (右上角)
    ↓
播放滑入动画
    ↓
3秒后播放滑出动画
    ↓
移除 toast
```

### 5.2 点击通知图标

```
点击 🔔
    ↓
检查通知中心是否打开
    ├─ 否 → 打开通知中心
    │       滑入动画
    │       标记所有为已读
    └─ 是 → 关闭通知中心
            滑出动画
```

### 5.3 点击通知项

```
点击通知项
    ↓
执行 notification.action
    ├─ "openInstance" → 打开实例详情
    ├─ "openDownload" → 打开下载页
    ├─ "openRoom" → 打开联机房间
    └─ null → 无操作
    ↓
关闭通知中心
```

---

## 6. 文件结构

```
ui/src/main/
├── java/org/aurora/launcher/ui/
│   ├── notification/
│   │   ├── Notification.java
│   │   ├── NotificationType.java
│   │   ├── NotificationManager.java
│   │   ├── NotificationToast.java
│   │   └── NotificationCenterController.java
│   └── ...
│
└── resources/
    ├── fxml/
    │   └── components/
    │       ├── NotificationToast.fxml
    │       └── NotificationCenter.fxml
    │
    └── css/
        └── notification.css
```

---

## 7. API 接口

```java
// 显示通知
NotificationManager.show(NotificationType type, String title, String message);

// 显示通知 + 操作
NotificationManager.show(NotificationType type, String title, String message, String action, String actionData);

// 获取所有通知
List<Notification> NotificationManager.getAll();

// 获取未读数量
int NotificationManager.getUnreadCount();

// 清除所有
void NotificationManager.clearAll();

// 标记已读
void NotificationManager.markAsRead(String id);
```

---

## 8. 使用示例

```java
// 下载完成
NotificationManager.show(
    NotificationType.DOWNLOAD,
    "下载完成",
    "Minecraft 1.21.4 已安装",
    "openInstance",
    "instance-uuid"
);

// 更新可用
NotificationManager.show(
    NotificationType.UPDATE,
    "更新可用",
    "Fabric Loader 0.15.8"
);

// 游戏启动
NotificationManager.show(
    NotificationType.GAME,
    "游戏已启动",
    "我的整合包"
);

// 错误提示
NotificationManager.show(
    NotificationType.ERROR,
    "启动失败",
    "缺少 Java 运行时"
);
```