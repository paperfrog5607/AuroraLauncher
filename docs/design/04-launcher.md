# 模块四：launcher - 启动器核心模块

## 1. 模块概述

负责游戏版本管理、安装、启动参数构建和游戏进程管理。不依赖packwiz，独立实现完整启动流程。

## 2. 依赖关系

```
launcher
├── core (配置、日志、平台检测、网络)
├── download (下载服务)
└── account (账号系统)
```

## 3. 子包结构

```
com.aurora.launcher/
├── version/
│   ├── VersionManifest.java         # 版本清单模型
│   ├── VersionInfo.java             # 版本详情
│   ├── VersionType.java             # 版本类型枚举
│   ├── VersionManifestService.java  # 版本清单服务
│   └── VersionManager.java          # 版本管理器
├── install/
│   ├── GameInstaller.java           # 游戏安装器
│   ├── LibraryInstaller.java        # 库文件安装器
│   ├── AssetInstaller.java          # 资源文件安装器
│   ├── NativeExtractor.java         # Native文件提取
│   └── InstallTask.java             # 安装任务
├── library/
│   ├── Library.java                 # 库文件模型
│   ├── LibraryDownloader.java       # 库文件下载器
│   ├── LibraryValidator.java        # 库文件校验
│   └── LibraryPathResolver.java     # 路径解析器
├── asset/
│   ├── AssetIndex.java              # 资源索引
│   ├── AssetObject.java             # 资源对象
│   ├── AssetDownloader.java         # 资源下载器
│   └── AssetManager.java            # 资源管理器
├── java/
│   ├── JavaVersion.java             # Java版本模型
│   ├── JavaDetector.java            # Java检测器
│   ├── JavaManager.java             # Java管理器
│   └── JavaDownloader.java          # Java下载器
├── loader/
│   ├── ModLoader.java               # 加载器接口
│   ├── FabricLoader.java            # Fabric支持
│   ├── ForgeLoader.java             # Forge支持
│   ├── QuiltLoader.java             # Quilt支持
│   └── LoaderInstaller.java         # 加载器安装器
├── launch/
│   ├── LaunchArgumentBuilder.java   # 启动参数构建器
│   ├── JvmArgumentBuilder.java      # JVM参数构建器
│   ├── ClasspathBuilder.java        # Classpath构建器
│   ├── LaunchProfile.java           # 启动配置
│   └── GameLauncher.java            # 游戏启动器
├── memory/
│   ├── MemoryCalculator.java        # 内存计算器
│   ├── MemoryPreset.java            # 内存预设
│   ├── JvmOptimizer.java            # JVM优化器(G1GC)
│   ├── MemoryMonitor.java           # 内存监控
│   └── MemoryManager.java           # 内存管理器
└── profile/
    ├── GameProfile.java             # 游戏配置
    ├── ProfileManager.java           # 配置管理器
    └── ProfileBuilder.java          # 配置构建器
```

## 4. 核心类设计

### 4.1 VersionManifestService

```java
public class VersionManifestService {
    private static final String MANIFEST_URL = 
        "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    
    private final HttpClient httpClient;
    private final CacheManager cache;
    
    public CompletableFuture<VersionManifest> getManifest();
    public CompletableFuture<VersionInfo> getVersionInfo(String versionId);
    public CompletableFuture<List<VersionInfo>> getVersions(VersionType type);
    public CompletableFuture<VersionInfo> getLatestRelease();
    public CompletableFuture<VersionInfo> getLatestSnapshot();
}

// 模型类
public class VersionManifest {
    private List<VersionInfo> versions;
    private VersionInfo latestRelease;
    private VersionInfo latestSnapshot;
}

public class VersionInfo {
    private String id;
    private VersionType type;
    private String url;
    private String assetIndexUrl;
    private String clientDownloadUrl;
    private String clientVersionHash;
    private JavaVersion javaVersion;
    private String mainClass;
    private List<Library> libraries;
    private AssetIndex assetIndex;
}
```

### 4.2 GameInstaller

```java
public class GameInstaller {
    private final DownloadService downloadService;
    private final LibraryInstaller libraryInstaller;
    private final AssetInstaller assetInstaller;
    
    public CompletableFuture<Void> install(VersionInfo version, 
                                           InstallOptions options,
                                           ProgressCallback callback);
    
    public CompletableFuture<Void> installClient(VersionInfo version, Path target);
    public CompletableFuture<Void> installLibraries(VersionInfo version, Path librariesDir);
    public CompletableFuture<Void> installAssets(VersionInfo version, Path assetsDir);
    public CompletableFuture<Void> installNatives(VersionInfo version, Path nativesDir);
    
    public boolean isInstalled(String versionId);
    public CompletableFuture<Void> repair(String versionId);
    public CompletableFuture<Void> uninstall(String versionId);
}

public class InstallOptions {
    private boolean includeAssets = true;
    private boolean includeNatives = true;
    private boolean verifyDownloads = true;
    private int maxConcurrentDownloads = 4;
    private Path customTargetPath;
}
```

### 4.3 LaunchArgumentBuilder

```java
public class LaunchArgumentBuilder {
    private final VersionInfo version;
    private final GameProfile profile;
    private final Account account;
    
    public List<String> build() {
        List<String> args = new ArrayList<>();
        args.addAll(buildJvmArguments());
        args.add(mainClass);
        args.addAll(buildGameArguments());
        return args;
    }
    
    private List<String> buildJvmArguments() {
        // -Djava.library.path
        // -Dminecraft.launcher.brand
        // -Dminecraft.launcher.version
        // -cp classpath
        // JVM优化参数(G1GC)
    }
    
    private List<String> buildGameArguments() {
        // --username
        // --version
        // --gameDir
        // --assetsDir
        // --assetIndex
        // --uuid
        // --accessToken
        // --userType
        // --versionType
    }
    
    // 模板参数替换
    private String replaceVariables(String template, Map<String, String> variables);
}
```

### 4.4 MemoryManager (G1GC方案)

```java
public class MemoryManager {
    private static final long MIN_MEMORY = 512 * 1024 * 1024;  // 512MB
    private static final long MAX_MEMORY = Runtime.getRuntime().maxMemory();
    
    private MemoryCalculator calculator;
    private MemoryPreset currentPreset;
    
    public MemoryConfig calculateOptimal(InstanceConfig config) {
        long systemMemory = getSystemMemory();
        long recommendedMemory = calculator.calculate(config);
        
        // 应用预设限制
        long finalMemory = currentPreset.apply(recommendedMemory, systemMemory);
        
        return new MemoryConfig(
            Math.max(MIN_MEMORY, finalMemory * 0.6),  // 初始堆
            Math.max(MIN_MEMORY, finalMemory),         // 最大堆
            buildG1GCArguments(finalMemory)            // G1GC参数
        );
    }
    
    private List<String> buildG1GCArguments(long heapSize) {
        List<String> args = new ArrayList<>();
        
        // G1GC基础参数
        args.add("-XX:+UseG1GC");
        args.add("-XX:+ParallelRefProcEnabled");
        args.add("-XX:MaxGCPauseMillis=200");
        
        // 内存相关
        args.add("-XX:InitiatingHeapOccupancyPercent=10");
        args.add("-XX:G1NewSizePercent=5");
        args.add("-XX:G1MaxNewSizePercent=30");
        
        // 性能优化
        args.add("-XX:+UnlockExperimentalVMOptions");
        args.add("-XX:G1NewSizePercent=20");
        args.add("-XX:G1ReservePercent=20");
        
        return args;
    }
}

public class MemoryPreset {
    public static final MemoryPreset LOW_END = new MemoryPreset(
        "low", "低配模式", 
        2L * 1024 * 1024 * 1024,   // 最大2GB
        0.3                          // 使用系统内存30%
    );
    
    public static final MemoryPreset STANDARD = new MemoryPreset(
        "standard", "标准模式",
        4L * 1024 * 1024 * 1024,   // 最大4GB
        0.4
    );
    
    public static final MemoryPreset HIGH_END = new MemoryPreset(
        "high", "高配模式",
        8L * 1024 * 1024 * 1024,   // 最大8GB
        0.5
    );
    
    public static final MemoryPreset EXTREME = new MemoryPreset(
        "extreme", "极致模式",
        16L * 1024 * 1024 * 1024,  // 最大16GB
        0.6
    );
    
    private String id;
    private String name;
    private long maxHeapSize;
    private double systemMemoryRatio;
}

public class MemoryConfig {
    private long initialHeap;
    private long maxHeap;
    private List<String> jvmArguments;
}
```

### 4.5 JavaManager

```java
public class JavaManager {
    private final List<JavaVersion> installedVersions = new ArrayList<>();
    
    public CompletableFuture<List<JavaVersion>> detectInstalled() {
        // 检测常见安装路径
        // Windows: Program Files/Java, Program Files/Eclipse Adoptium, etc.
        // macOS: /Library/Java/JavaVirtualMachines
        // Linux: /usr/lib/jvm
    }
    
    public JavaVersion findBest(JavaVersionRequirement requirement) {
        return installedVersions.stream()
            .filter(v -> v.meets(requirement))
            .max(Comparator.comparingInt(JavaVersion::getMajorVersion))
            .orElse(null);
    }
    
    public boolean isCompatible(JavaVersion java, VersionInfo mcVersion) {
        int minVersion = mcVersion.getJavaVersion().getMajorVersion();
        return java.getMajorVersion() >= minVersion;
    }
}
```

### 4.6 GameLauncher

```java
public class GameLauncher {
    private final ProcessManager processManager;
    
    public CompletableFuture<Process> launch(LaunchProfile profile, 
                                             LaunchOptions options) {
        // 1. 验证游戏完整性
        // 2. 构建启动参数
        // 3. 设置环境变量
        // 4. 启动进程
        // 5. 启动日志收集
    }
    
    public CompletableFuture<Void> kill(String instanceId);
    public CompletableFuture<Void> killAll();
    
    public List<GameProcess> getRunningInstances();
    public Optional<GameProcess> getInstance(String instanceId);
    
    public void setLogHandler(String instanceId, Consumer<String> handler);
    public void setExitHandler(String instanceId, Consumer<Integer> handler);
}

public class LaunchProfile {
    private String instanceId;
    private VersionInfo version;
    private GameProfile gameConfig;
    private Account account;
    private JavaVersion javaVersion;
    private MemoryConfig memoryConfig;
    private Path gameDir;
    private List<String> customJvmArgs;
    private List<String> customGameArgs;
}

public class GameProcess {
    private String instanceId;
    private Process process;
    private Instant startTime;
    private Path logFile;
    private ProcessState state;
}
```

## 5. 关键流程

### 5.1 游戏安装流程

```
用户选择版本
    ↓
获取版本清单
    ↓
下载版本JSON
    ↓
解析依赖关系
    ↓
并行下载:
├── 客户端jar
├── 库文件
├── 资源文件
└── Native文件
    ↓
校验文件完整性
    ↓
安装完成
```

### 5.2 游戏启动流程

```
用户点击启动
    ↓
验证账号状态
    ↓
刷新Token(如需要)
    ↓
计算内存配置
    ↓
检测/选择Java版本
    ↓
构建启动参数:
├── JVM参数
│   ├── 内存参数
│   ├── G1GC参数
│   └── 自定义参数
└── 游戏参数
    ├── 基础参数
    ├── 账号参数
    └── 自定义参数
    ↓
启动游戏进程
    ↓
监控日志输出
    ↓
游戏运行中
```

## 6. 配置文件

### 6.1 版本清单缓存

```json
{
  "lastUpdated": "2024-01-01T00:00:00Z",
  "versions": [
    {
      "id": "1.20.4",
      "type": "release",
      "url": "...",
      "javaVersion": 17
    }
  ]
}
```

### 6.2 启动配置

```json
{
  "instanceId": "my-instance",
  "version": "1.20.4",
  "javaPath": "/path/to/java",
  "memoryPreset": "standard",
  "customMemory": {
    "min": 2048,
    "max": 4096
  },
  "jvmArgs": ["-XX:+UseG1GC"],
  "gameArgs": [],
  "autoMemory": true
}
```

## 7. 接口定义

```java
public interface ModLoader {
    String getName();
    String getVersion();
    CompletableFuture<Void> install(VersionInfo mcVersion, Path target);
    boolean isInstalled(Path instanceDir);
    List<Library> getLibraries();
    String getMainClass();
    List<String> getArguments();
}

public interface ProgressCallback {
    void onProgress(String stage, double progress, long current, long total);
    void onMessage(String message);
    void onError(Exception error);
    void onComplete();
}
```

## 8. 错误处理

```java
public class LauncherException extends RuntimeException {
    private final LauncherErrorCode code;
    
    public enum LauncherErrorCode {
        VERSION_NOT_FOUND,
        DOWNLOAD_FAILED,
        INTEGRITY_CHECK_FAILED,
        JAVA_NOT_FOUND,
        JAVA_INCOMPATIBLE,
        LAUNCH_FAILED,
        PROCESS_CRASHED
    }
}
```

## 9. 测试要点

- 版本清单解析和缓存
- 依赖关系解析
- 并行下载和断点续传
- 启动参数构建正确性
- 内存计算准确性
- Java版本兼容性检测
- 进程管理和日志收集