# 模块设计：core（核心基础模块）

## 概述

核心基础模块提供启动器所有模块共用的基础设施，包括配置管理、事件总线、国际化、文件操作、日志系统等。

## 子包结构

```
org.aurora.launcher.core/
├── config/           # 配置管理
├── event/            # 事件总线
├── i18n/             # 国际化
├── io/               # 文件操作
├── logging/          # 日志系统
├── platform/         # 平台检测
├── net/              # 网络工具
├── cache/            # 缓存管理
├── task/             # 任务系统
├── process/          # 进程管理
├── path/             # 路径管理
├── security/         # 安全工具
├── crypto/           # 加密工具
└── util/             # 通用工具
```

## 详细设计

### 1. config（配置管理）

**ConfigManager**
```java
public class ConfigManager {
    private Path configPath;
    private JsonObject config;
    
    public void load();
    public void save();
    public <T> T get(String key, Class<T> type);
    public void set(String key, Object value);
    public boolean has(String key);
    public void remove(String key);
}
```

**配置文件结构**
```json
{
  "version": "1.0.0",
  "language": "zh_CN",
  "theme": "dark",
  "java": {
    "autoDetect": true,
    "customPath": null,
    "defaultMemory": 4096
  },
  "launcher": {
    "checkUpdates": true,
    "autoStart": false,
    "closeAfterLaunch": false
  },
  "download": {
    "concurrent": 4,
    "timeout": 30000,
    "retryCount": 3
  },
  "ui": {
    "windowWidth": 1200,
    "windowHeight": 800,
    "rememberPosition": true
  }
}
```

### 2. event（事件总线）

**EventBus**
```java
public class EventBus {
    private Map<Class<?>, List<EventHandler<?>>> handlers;
    
    public <T> void register(Class<T> eventType, EventHandler<T> handler);
    public <T> void unregister(Class<T> eventType, EventHandler<T> handler);
    public <T> void post(T event);
    public <T> void postAsync(T event);
}

@FunctionalInterface
public interface EventHandler<T> {
    void handle(T event);
}
```

**核心事件**
```java
// 启动器事件
public class LauncherStartedEvent {}
public class LauncherClosingEvent {}

// 下载事件
public class DownloadStartedEvent { String url; String fileName; }
public class DownloadProgressEvent { String fileName; long current; long total; }
public class DownloadCompletedEvent { String fileName; Path path; }
public class DownloadFailedEvent { String fileName; Exception error; }

// 游戏事件
public class GameStartedEvent { String instanceName; }
public class GameClosedEvent { String instanceName; int exitCode; }

// 实例事件
public class InstanceCreatedEvent { String instanceName; }
public class InstanceDeletedEvent { String instanceName; }
public class InstanceModifiedEvent { String instanceName; }
```

### 3. i18n（国际化）

**I18n**
```java
public class I18n {
    private static Locale currentLocale;
    private static ResourceBundle bundle;
    
    public static void setLocale(Locale locale);
    public static String get(String key);
    public static String get(String key, Object... args);
    public static Locale getCurrentLocale();
    public static List<Locale> getSupportedLocales();
}
```

**语言文件**
```
resources/i18n/
├── messages_zh_CN.properties    # 简体中文
└── messages_en_US.properties    # 英文
```

### 4. io（文件操作）

**FileUtils**
```java
public class FileUtils {
    public static void copyDirectory(Path source, Path target);
    public static void deleteDirectory(Path path);
    public static long getDirectorySize(Path path);
    public static List<Path> listFiles(Path dir, String... extensions);
    public static void createDirectoryIfNotExists(Path path);
    public static String readAllText(Path path);
    public static void writeAllText(Path path, String content);
    public static byte[] readAllBytes(Path path);
    public static void writeAllBytes(Path path, byte[] bytes);
}
```

**JsonUtils**
```java
public class JsonUtils {
    public static <T> T fromJson(String json, Class<T> type);
    public static <T> T fromJson(String json, Type type);
    public static String toJson(Object obj);
    public static String toJsonPretty(Object obj);
    public static JsonObject parseObject(String json);
    public static JsonArray parseArray(String json);
}
```

**ZipUtils**
```java
public class ZipUtils {
    public static void extract(Path zipPath, Path targetDir);
    public static void compress(Path sourceDir, Path zipPath);
    public static void compress(Path sourceDir, Path zipPath, Predicate<Path> filter);
    public static List<String> listEntries(Path zipPath);
}
```

### 5. logging（日志系统）

**Logger**
```java
public class Logger {
    private String name;
    
    public void debug(String message);
    public void info(String message);
    public void warn(String message);
    public void error(String message);
    public void error(String message, Throwable t);
    
    public static Logger getLogger(String name);
    public static Logger getLogger(Class<?> clazz);
}
```

**LogManager**
```java
public class LogManager {
    public static void initialize(Path logDir);
    public static void setLevel(LogLevel level);
    public static void addAppender(LogAppender appender);
    public static void shutdown();
}
```

### 6. platform（平台检测）

**Platform**
```java
public class Platform {
    public enum OS { WINDOWS, MACOS, LINUX, UNKNOWN }
    public enum Arch { X86, X64, ARM64, UNKNOWN }
    
    public static OS getOS();
    public static Arch getArch();
    public static String getOSVersion();
    public static String getJavaVersion();
    public static String getJavaHome();
    public static long getTotalMemory();
    public static long getAvailableMemory();
    public static String getWorkingDirectory();
    public static Path getAppDataDirectory();
}
```

### 7. net（网络工具）

**HttpClient**
```java
public class HttpClient {
    public String get(String url);
    public String get(String url, Map<String, String> headers);
    public byte[] getBytes(String url);
    public void download(String url, Path target, ProgressCallback callback);
    public void download(String url, Path target, DownloadOptions options);
}

public interface ProgressCallback {
    void onProgress(long current, long total);
}
```

**DownloadOptions**
```java
public class DownloadOptions {
    private int timeout = 30000;
    private int retryCount = 3;
    private boolean overwrite = true;
    private Map<String, String> headers;
}
```

### 8. cache（缓存管理）

**CacheManager**
```java
public class CacheManager {
    private Path cacheDir;
    private long maxSize;
    
    public Path getCachePath(String key);
    public InputStream getCache(String key);
    public void putCache(String key, InputStream data);
    public void putCache(String key, Path file);
    public boolean hasCache(String key);
    public void invalidate(String key);
    public void invalidateAll();
    public void cleanOldCache();
    public long getCacheSize();
}
```

### 9. task（任务系统）

**Task**
```java
public abstract class Task<T> {
    protected String name;
    protected float progress;
    protected TaskState state;
    
    public abstract T execute() throws TaskException;
    public void cancel();
    public void setProgress(float progress);
    public void updateProgress(long current, long total);
    
    public String getName();
    public float getProgress();
    public TaskState getState();
}

public enum TaskState {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
}
```

**TaskManager**
```java
public class TaskManager {
    private ExecutorService executor;
    private List<Task<?>> runningTasks;
    
    public <T> CompletableFuture<T> submit(Task<T> task);
    public List<Task<?>> getRunningTasks();
    public void cancelAll();
    public void shutdown();
}
```

### 10. process（进程管理）

**ProcessManager**
```java
public class ProcessManager {
    private Map<String, GameProcess> processes;
    
    public GameProcess start(String name, List<String> command, Path workDir);
    public void kill(String name);
    public void killAll();
    public List<GameProcess> getRunningProcesses();
    public boolean isRunning(String name);
}

public class GameProcess {
    private String name;
    private Process process;
    private Instant startTime;
    
    public void waitFor();
    public void kill();
    public int getExitCode();
    public InputStream getInputStream();
    public InputStream getErrorStream();
    public Duration getUptime();
}
```

### 11. path（路径管理）

**PathManager**
```java
public class PathManager {
    private Path baseDir;
    private Path instancesDir;
    private Path cacheDir;
    private Path logsDir;
    private Path configDir;
    private Path tempDir;
    
    public static void initialize(Path basePath);
    public static PathManager getInstance();
    
    public Path getBaseDirectory();
    public Path getInstancesDirectory();
    public Path getInstanceDirectory(String name);
    public Path getCacheDirectory();
    public Path getLogsDirectory();
    public Path getConfigDirectory();
    public Path getTempDirectory();
    public Path getJavaDirectory();
    public Path getVersionsDirectory();
}
```

### 12. security（安全工具）

**SecurityUtils**
```java
public class SecurityUtils {
    public static String hashSHA256(byte[] data);
    public static String hashSHA256(Path file);
    public static String hashMD5(byte[] data);
    public static String hashMD5(Path file);
    public static boolean verifyChecksum(Path file, String expectedHash, String algorithm);
}
```

**UrlValidator**
```java
public class UrlValidator {
    public static boolean isValidUrl(String url);
    public static boolean isAllowedDomain(String url, List<String> allowedDomains);
    public static String sanitizeFilename(String filename);
}
```

### 13. crypto（加密工具）

**CryptoUtils**
```java
public class CryptoUtils {
    public static String encryptAES(String data, String key);
    public static String decryptAES(String encryptedData, String key);
    public static String hashPassword(String password);
    public static boolean verifyPassword(String password, String hash);
}
```

### 14. util（通用工具）

**StringUtils**
```java
public class StringUtils {
    public static boolean isNullOrEmpty(String str);
    public static boolean isNullOrBlank(String str);
    public static String truncate(String str, int maxLength);
    public static String formatSize(long bytes);
    public static String formatDuration(Duration duration);
}
```

**CollectionUtils**
```java
public class CollectionUtils {
    public static <T> List<T> emptyIfNull(List<T> list);
    public static <T> boolean isNullOrEmpty(Collection<T> collection);
}
```

## 依赖关系

本模块为最底层模块，不依赖其他业务模块，仅依赖第三方库：
- Gson（JSON处理）
- OkHttp（网络请求）
- SLF4J（日志门面）

## 使用示例

```java
// 配置管理
ConfigManager config = new ConfigManager(PathManager.getConfigDirectory().resolve("config.json"));
config.load();
int memory = config.get("java.defaultMemory", Integer.class);

// 事件总线
EventBus.register(GameStartedEvent.class, event -> {
    Logger.info("Game started: " + event.getInstanceName());
});
EventBus.post(new GameStartedEvent("my-instance"));

// 国际化
I18n.setLocale(Locale.SIMPLIFIED_CHINESE);
String title = I18n.get("app.title");

// 日志
Logger log = Logger.getLogger(Launcher.class);
log.info("Launcher started");

// 平台检测
if (Platform.getOS() == Platform.OS.WINDOWS) {
    // Windows特定逻辑
}

// 文件操作
FileUtils.copyDirectory(sourceDir, targetDir);

// 路径管理
Path instanceDir = PathManager.getInstanceDirectory("my-instance");

// 任务执行
TaskManager.submit(new DownloadTask(url, targetPath));

// 进程管理
GameProcess process = ProcessManager.start("minecraft", command, workDir);
```