# Aurora Launcher 开发进度

## 更新时间: 2026-03-26 19:40

## 模式变更: 本地优先 (Local-Only Mode)

云端后端已移除，切换为纯本地启动器模式。

**原因**: 服务器成本过高，改为本地功能优先。

**当前状态**:
- JavaFX UI: ✅ 完成
- Gradle 构建: ✅ 通过
- 后端: ❌ 已移除

## 一、设计文档 ✅ 完成

所有设计文档已保存在 `docs/design/` 目录：

| 模块 | 文档 | 状态 |
|------|------|------|
| 01 | core - 核心基础模块 | ✅ |
| 02 | config - 配置管理模块 | ✅ |
| 03 | account - 账号系统模块 | ✅ |
| 04 | launcher - 启动器核心模块 | ✅ |
| 05 | modpack - 整合包管理模块 | ✅ |
| 06 | mod - 模组管理模块 | ✅ |
| 07 | resource - 资源管理模块 | ✅ |
| 08 | download - 下载服务模块 | ✅ |
| 09 | api - API客户端模块 | ✅ |
| 10 | diagnostic - 诊断工具模块 | ✅ |
| 11 | ai - AI服务模块 | ✅ |
| 12 | dev - 开发辅助模块 | ✅ |
| 13 | ui - 界面模块 (5个子文档) | ✅ |

## 二、项目结构 ✅ 完成

```
Aurora Launcher/
├── core/          # 核心基础
├── config/        # 配置管理
├── account/       # 账号系统
├── launcher/      # 启动器核心
├── modpack/       # 整合包管理
├── mod/           # 模组管理
├── resource/      # 资源管理
├── download/      # 下载服务
├── api/           # API客户端
├── diagnostic/    # 诊断工具
├── ai/            # AI服务
├── dev/           # 开发辅助
├── ui/            # 界面层
└── javafx-jmods/  # JavaFX jmods
```

## 三、模块实现进度

| 模块 | 设计 | 代码 | 测试 | 状态 |
|------|------|------|------|------|
| core | ✅ | 🔄 | ❌ | 进行中 |
| config | ✅ | ✅ | ❌ | 基本完成 |
| account | ✅ | ❌ | ❌ | 未开始 |
| launcher | ✅ | ❌ | ❌ | 未开始 |
| modpack | ✅ | 🔄 | ❌ | 进行中 |
| mod | ✅ | 🔄 | ❌ | 进行中 |
| resource | ✅ | ✅ | ❌ | 基本完成 |
| download | ✅ | ✅ | ❌ | 完成 |
| api | ✅ | ✅ | ⚠️ | 本地API模块(无远程) |
| diagnostic | ✅ | ❌ | ❌ | 未开始 |
| ai | ✅ | ✅ | ✅ | 完成 |
| dev | ✅ | 🔄 | ❌ | 进行中 |
| ui | ✅ | ✅ | ⚠️ | 完成(可打包运行) |

## 四、UI模块详情

### 已完成
- ✅ AuroraApplication.java - 应用入口
- ✅ TabRouter.java - 标签路由系统
- ✅ BaseController.java - 基础控制器
- ✅ MainViewController.java - 主控制器
- ✅ DownloadController.java - 下载页控制器
- ✅ VersionController.java - 版本列表控制器
- ✅ MainView.fxml - 主视图
- ✅ LaunchView.fxml - 启动页
- ✅ DownloadView.fxml - 下载页
- ✅ SettingsView.fxml - 设置页
- ✅ VersionView.fxml - 版本列表视图
- ✅ QuickLaunchCard.fxml - 快速启动卡片
- ✅ InstanceListCard.fxml - 实例列表卡片
- ✅ 国际化资源文件 (中/英)
- ✅ CSS主题样式
- ✅ 按钮事件绑定
- ✅ 标签切换功能

### 待完成
- ⚠️ 版本列表显示问题（当前空白）
- ⚠️ 控制器业务逻辑实现
- ⚠️ 组件完善
- ⚠️ 更多页面视图

## 五、打包配置 ✅ 完成

### 打包命令
```bash
# 编译
./gradlew build -x test

# 便携版
./gradlew :ui:packagePortable

# 安装包
./gradlew :ui:packageExe
```

### 输出位置
- 便携版: `ui/build/distribute/AuroraLauncher/`
- 安装包: `ui/build/distribute/AuroraLauncher-1.0.0.exe`

### 已配置
- ✅ JavaFX jmods 集成
- ✅ WiX Toolset 安装
- ✅ Shadow JAR 打包
- ✅ ResourceBundle 国际化
- ✅ java.logging 模块添加

## 六、当前问题 (BUG)

### ~~版本列表空白问题~~ ✅ 已修复

**原因**: 
1. 按钮事件正常工作
2. SSL模块缺失导致HTTPS请求失败

**解决方案**:
- 添加 `java.security.jgss` 和 `jdk.crypto.ec` 模块到打包配置

**修复文件**: `ui/build.gradle`

## 七、当前问题 (待解决)

### CSS解析警告
**现象**: CSS Error parsing `-fx-shadow-sm/md/lg`
**原因**: JavaFX CSS不支持CSS变量作为box-shadow值
**影响**: 仅警告，不影响功能
**待修复**: 将CSS变量替换为具体值

## 七、已实现的服务

### VersionService (版本服务)
- 位置: `ui/src/main/java/org/aurora/launcher/ui/service/VersionService.java`
- 功能:
  - 从Mojang API获取版本列表
  - 过滤正式版和快照版
  - 下载版本JSON文件
  - 检查版本是否已下载

### ServiceLocator (服务定位器)
- 位置: `ui/src/main/java/org/aurora/launcher/ui/service/ServiceLocator.java`
- 已注册服务:
  - InstanceManager
  - VersionService

## 八、下一步计划

1. **修复版本列表空白问题**
   - 检查按钮点击事件是否正确触发
   - 检查CSS样式
   - 添加更多调试日志

2. **完善版本下载功能**
   - 显示版本列表
   - 实现下载进度显示
   - 实现下载完成后刷新UI

3. **实现其他功能**
   - 实例管理
   - 账号系统
   - 游戏启动

## 九、已完成修复 (2026-03-17)

- ✅ ServiceLocator import问题已修复
- ✅ LaunchSettingsController.java import已更新
- ✅ ServiceLocatorTest.java 已移动到新包
- ✅ 便携版打包成功
- ✅ 编译通过
- ✅ FXML中 `<Region>` 替换为 `<HBox>`
- ✅ CSS变量替换为具体值
- ✅ java.logging 模块添加到打包配置
- ✅ MainViewController 控制器正确加载

## 十、技术栈

- Java 17
- JavaFX 17
- Gradle 8.5
- SLF4J + Logback
- Gson
- Guava
- OkHttp

## 十一、设计风格

PCL风格界面:
- 顶部标签导航: [启动] [下载] [更多]
- 卡片式布局
- 紫色主色调 (#8B5CF6)
- 深色主题为主

## 十二、调试日志级别

当前日志配置 (logback.xml):
- INFO: 主要流程
- DEBUG: 详细信息
- WARN: 警告
- ERROR: 错误

关键日志输出点:
- `AuroraApplication`: 应用启动
- `TabRouter`: 标签切换
- `MainViewController`: 按钮点击
- `DownloadController`: 下载页初始化
- `VersionController`: 版本列表加载
- `VersionService`: API调用