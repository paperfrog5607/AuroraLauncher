# Aurora 联机功能设计规范

## 更新时间: 2026-03-26

## 1. 概述

### 1.1 功能定位

为 Aurora 游戏平台添加 P2P 联机功能，支持多游戏跨平台联机，不依赖云服务器。

### 1.2 核心原理

基于 RadminVPN/Steam 的虚拟局域网原理，通过 NAT 穿透技术实现 P2P 直连。

### 1.3 技术选型

| 组件 | 技术 | 说明 |
|------|------|------|
| NAT 穿透 | ICE 协议 | 自动选择最佳连接方式 |
| STUN | Google STUN | 查询公网 IP:端口 |
| TURN | Metered.ca | 直连失败时中继转发 |
| 加密 | TLS/DTLS | 流量加密保护隐私 |

## 2. Token 系统

### 2.1 Token 格式

| 类型 | 格式 | 示例 |
|------|------|------|
| 房间号 | 6位字母数字 | `ABC123` |
| 邀请链接 | UUID 格式 | `https://aurora.gg/join/abc123-def456` |

### 2.2 Token 用途

- 房间号：简短易记，输入方便
- 邀请链接：点击即加入，适合分享

### 2.3 安全机制

- Token 验证后才可进入房间
- 房主可踢出成员
- 连接加密，IP 隐藏

## 3. UI 设计

### 3.1 联机中心入口

```
┌─────────────────────────────────────┐
│  🎮 Aurora 联机中心                    │
├─────────────────────────────────────┤
│                                     │
│   [创建房间]      [加入房间]           │
│                                     │
└─────────────────────────────────────┘
```

### 3.2 创建房间视图

```
┌─────────────────────────────────────┐
│  ← 返回                    创建房间   │
├─────────────────────────────────────┤
│                                     │
│  房间号: ABC123        [复制]       │
│                                     │
│  邀请链接:                         │
│  https://aurora.gg/join/... [复制]   │
│                                     │
│  状态: 等待中         成员: 0/8     │
│                                     │
│  ┌─ 连接成员列表 ────────────────┐   │
│  │ ● 玩家1 (房主) - 已连接       │   │
│  │ ○ 等待加入...                │   │
│  └──────────────────────────────┘   │
│                                     │
│  [刷新] [开始游戏] [关闭房间]        │
│                                     │
├─────────────────────────────────────┤
│  NAT类型: 开放  |  延迟: --        │
└─────────────────────────────────────┘
```

### 3.3 加入房间视图

```
┌─────────────────────────────────────┐
│  ← 返回                    加入房间   │
├─────────────────────────────────────┤
│                                     │
│  输入房间号或粘贴邀请链接:           │
│  ┌─────────────────────────────┐    │
│  │ ABC123                      │    │
│  └─────────────────────────────┘    │
│                                     │
│  [加入房间]                          │
│                                     │
│  ─── 或 ───                          │
│                                     │
│  粘贴链接:                          │
│  ┌─────────────────────────────┐    │
│  │ https://aurora.gg/join/...  │    │
│  └─────────────────────────────┘    │
│                                     │
│  [从链接加入]                        │
│                                     │
└─────────────────────────────────────┘
```

### 3.4 连接状态视图

```
┌─────────────────────────────────────┐
│  联机中心              [断开连接]    │
├─────────────────────────────────────┤
│                                     │
│  房间号: ABC123     状态: 已连接     │
│  房主: 玩家1       成员: 3/8       │
│                                     │
│  ┌─ 连接成员 ──────────────────┐   │
│  │ ● 玩家1 (房主) - 12ms       │   │
│  │ ● 玩家2 - 15ms              │   │
│  │ ● 玩家3 - 8ms               │   │
│  └──────────────────────────────┘   │
│                                     │
│  [邀请好友] [设置]                   │
│                                     │
├─────────────────────────────────────┤
│  NAT类型: 开放  |  延迟: 12ms(平均) │
└─────────────────────────────────────┘
```

## 4. 功能模块

### 4.1 房间管理

| 功能 | 说明 |
|------|------|
| 创建房间 | 生成 Token，设置房间参数 |
| 加入房间 | 验证 Token 后加入 |
| 邀请好友 | 复制房间号或链接 |
| 踢出成员 | 房主权限 |
| 关闭房间 | 房主关闭，所有人断开 |

### 4.2 连接管理

| 功能 | 说明 |
|------|------|
| NAT 检测 | 自动检测 NAT 类型 |
| 连接建立 | ICE 协商建立 P2P 连接 |
| 失败重试 | 直连失败自动尝试中继 |
| 断线重连 | 网络波动时自动重连 |

### 4.3 游戏适配

| 功能 | 说明 |
|------|------|
| TCP 代理 | 游戏 TCP 流量走 P2P 通道 |
| UDP 代理 | 游戏 UDP 流量走 P2P 通道 |
| 端口映射 | 本地端口自动映射给其他成员 |

## 5. 数据流

### 5.1 创建房间流程

```
用户点击创建房间
    ↓
生成 6位房间号 + UUID
    ↓
初始化 ICE/STUN/TURN
    ↓
开始监听连接
    ↓
显示房间号和链接
```

### 5.2 加入房间流程

```
用户输入房间号或粘贴链接
    ↓
解析 Token 获取房间信息
    ↓
ICE 协商建立连接
    ↓
连接成功进入房间
```

### 5.3 P2P 连接流程

```
1. 查询本地公网地址 (STUN)
2. 交换地址信息 (通过 Token 关联)
3. 尝试 UDP 打洞
4. 成功则直连，失败走 TURN 中继
```

## 6. 状态定义

### 6.1 连接状态

| 状态 | 说明 |
|------|------|
| `IDLE` | 未连接 |
| `CONNECTING` | 连接中 |
| `CONNECTED` | 已连接 |
| `RECONNECTING` | 断线重连 |
| `DISCONNECTED` | 已断开 |

### 6.2 NAT 类型

| 类型 | 说明 | P2P 成功率 |
|------|------|-----------|
| `OPEN` | 开放网络 | 100% |
| `MODERATE` | 中等 NAT | 90% |
| `STRICT` | 严格 NAT | 50% |
| `SYMMETRIC` | 对称 NAT | 10% |

## 7. 界面交互

### 7.1 输入验证

- 房间号：仅允许 6 位字母数字，自动大写
- 链接：自动识别并提取房间 ID
- 错误提示：无效 Token、房间不存在、已满等

### 7.2 反馈机制

- 连接中：显示动画和状态
- 成功：短暂提示后进入房间
- 失败：显示原因和重试选项

## 8. 技术实现

### 8.1 依赖库

- Java ICE 实现：Ice4J 或类似库
- 或使用 JNI 绑定 libnice

### 8.2 文件结构

```
ui/src/main/java/org/aurora/launcher/
├── network/
│   ├── p2p/
│   │   ├── P2PManager.java         # P2P 连接管理
│   │   ├── IceService.java         # ICE 协议实现
│   │   ├── RoomService.java        # 房间 Token 管理
│   │   └── ProxyService.java       # TCP/UDP 代理
│   └── ui/
│       ├── NetworkController.java   # 联机控制器
│       └── NetworkView.fxml         # 联机界面
```

## 9. FXML 布局

```xml
<?xml version="1.0" encoding="UTF-8"?>

<StackPane xmlns="http://javafx.com/javafx/17"
           xmlns:fx="http://javafx.com/fxml/1"
           fx:controller="org.aurora.launcher.network.ui.NetworkController">

    <VBox spacing="20" styleClass="network-container">
        <!-- 标题 -->
        <HBox styleClass="network-header">
            <Label text="Aurora 联机中心" styleClass="title"/>
        </HBox>

        <!-- 主按钮区 -->
        <HBox spacing="40" alignment="CENTER">
            <Button text="创建房间" styleClass="primary-btn" onAction="#createRoom"/>
            <Button text="加入房间" styleClass="secondary-btn" onAction="#joinRoom"/>
        </HBox>

        <!-- 房间信息区 (创建后显示) -->
        <VBox fx:id="roomInfoPanel" visible="false">
            <HBox>
                <Label text="房间号: "/>
                <Label fx:id="roomCode" styleClass="code"/>
                <Button text="复制" onAction="#copyRoomCode"/>
            </HBox>
            <HBox>
                <Label text="邀请链接: "/>
                <TextField fx:id="inviteLink" editable="false"/>
                <Button text="复制" onAction="#copyInviteLink"/>
            </HBox>
        </VBox>

        <!-- 成员列表 -->
        <VBox fx:id="memberListPanel" visible="false">
            <Label text="连接成员"/>
            <ListView fx:id="memberList"/>
        </VBox>

        <!-- 加入房间输入区 -->
        <VBox fx:id="joinPanel" visible="false">
            <TextField fx:id="roomCodeInput" promptText="输入房间号"/>
            <Button text="加入" onAction="#joinByCode"/>
            <Label text="或"/>
            <TextField fx:id="inviteLinkInput" promptText="粘贴邀请链接"/>
            <Button text="从链接加入" onAction="#joinByLink"/>
        </VBox>

        <!-- 状态栏 -->
        <HBox styleClass="status-bar">
            <Label text="NAT类型: "/>
            <Label fx:id="natType" text="--"/>
            <Label text=" | 延迟: "/>
            <Label fx:id="latency" text="--"/>
        </HBox>
    </VBox>
</StackPane>
```

## 10. CSS 样式

```css
.network-container {
    -fx-background-color: #1a1a2e;
    -fx-padding: 30;
}

.network-header .title {
    -fx-font-size: 24;
    -fx-text-fill: #8B5CF6;
}

.primary-btn {
    -fx-background-color: #8B5CF6;
    -fx-text-fill: white;
    -fx-min-width: 150;
    -fx-min-height: 50;
}

.secondary-btn {
    -fx-background-color: transparent;
    -fx-border-color: #8B5CF6;
    -fx-text-fill: #8B5CF6;
    -fx-min-width: 150;
    -fx-min-height: 50;
}

.code {
    -fx-font-family: monospace;
    -fx-font-size: 20;
    -fx-text-fill: #00ff88;
}

.status-bar {
    -fx-background-color: #16162a;
    -fx-padding: 10;
}
```

## 11. 下一步计划

1. 创建 `network` 包和基础类
2. 实现 Token 生成和管理
3. 实现 ICE/STUN/TURN 集成
4. 实现 UI 界面
5. 测试 P2P 连接
