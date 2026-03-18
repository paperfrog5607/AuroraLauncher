# 模块八：download - 下载服务模块

## 1. 模块概述

HTTP下载、断点续传、多线程下载、任务队列、并发控制、进度追踪、重试机制。

## 2. 依赖关系

```
download
└── core (网络、日志、配置)
```

## 3. 子包结构

```
com.aurora.download/
├── core/
│   ├── DownloadEngine.java           # 下载引擎
│   ├── DownloadTask.java            # 下载任务
│   ├── DownloadRequest.java          # 下载请求
│   └── DownloadResult.java           # 下载结果
├── queue/
│   ├── DownloadQueue.java            # 下载队列
│   ├── TaskScheduler.java           # 任务调度器
│   └── PriorityTask.java            # 优先级任务
├── chunk/
│   ├── ChunkDownloader.java          # 分块下载器
│   ├── ChunkManager.java            # 分块管理器
│   └── ChunkInfo.java               # 分块信息
├── resume/
│   ├── ResumeSupport.java            # 断点续传支持
│   ├── ResumeRecord.java            # 续传记录
│   └── ResumeManager.java            # 续传管理器
├── progress/
│   ├── ProgressTracker.java          # 进度追踪器
│   ├── ProgressEvent.java            # 进度事件
│   └── ProgressCallback.java        # 进度回调
├── retry/
│   ├── RetryPolicy.java             # 重试策略
│   ├── RetryHandler.java            # 重试处理器
│   └── BackoffStrategy.java         # 退避策略
├── validation/
│   ├── FileValidator.java           # 文件验证器
│   ├── ChecksumValidator.java        # 校验验证器
│   └── SizeValidator.java           # 大小验证器
└── config/
    ├── DownloadConfig.java           # 下载配置
    └── ProxyConfig.java             # 代理配置
```

## 4. 核心类设计

### 4.1 DownloadEngine

```java
public class DownloadEngine {
    private final HttpClient httpClient;
    private final DownloadQueue queue;
    private final TaskScheduler scheduler;
    private final DownloadConfig config;
    
    public CompletableFuture<DownloadResult> download(DownloadRequest request) {
        // 单文件下载
    }
    
    public CompletableFuture<List<DownloadResult>> downloadBatch(
            List<DownloadRequest> requests) {
        // 批量下载
    }
    
    public CompletableFuture<DownloadResult> downloadWithResume(
            DownloadRequest request) {
        // 支持续传的下载
    }
    
    public CompletableFuture<DownloadResult> downloadWithChunks(
            DownloadRequest request) {
        // 多线程分块下载
    }
    
    public void cancel(String taskId);
    public void cancelAll();
    
    public void pause(String taskId);
    public void resume(String taskId);
    
    public void setMaxConcurrent(int max);
    public int getActiveCount();
    public int getQueuedCount();
}

public class DownloadRequest {
    private String id;
    private String url;
    private Path targetPath;
    private String expectedSha1;
    private long expectedSize;
    private int maxRetries = 3;
    private int chunkCount = 4;
    private boolean supportResume = true;
    private ProxyConfig proxy;
    private Map<String, String> headers;
    private int timeout = 30000;
    private int priority = 0;
}

public class DownloadResult {
    private String id;
    private DownloadStatus status;
    private Path filePath;
    private long bytesDownloaded;
    private long totalBytes;
    private long duration;
    private long averageSpeed;
    private String error;
    
    public enum DownloadStatus {
        PENDING, DOWNLOADING, PAUSED, COMPLETED, FAILED, CANCELLED
    }
}

public class DownloadTask {
    private DownloadRequest request;
    private DownloadStatus status;
    private long downloadedBytes;
    private long totalBytes;
    private Instant startTime;
    private Instant endTime;
    private int retryCount;
    private List<ProgressCallback> callbacks;
    
    public void addCallback(ProgressCallback callback);
    public void pause();
    public void resume();
    public void cancel();
}
```

### 4.2 DownloadQueue

```java
public class DownloadQueue {
    private final PriorityBlockingQueue<PriorityTask> queue;
    private final int maxConcurrent;
    private final ExecutorService executor;
    private final Map<String, DownloadTask> activeTasks;
    
    public void submit(DownloadTask task) {
        queue.put(new PriorityTask(task, task.getPriority()));
    }
    
    public void submitAll(List<DownloadTask> tasks) {
        tasks.forEach(this::submit);
    }
    
    public void cancel(String taskId) {
        DownloadTask task = activeTasks.get(taskId);
        if (task != null) {
            task.cancel();
        } else {
            queue.removeIf(t -> t.getTask().getId().equals(taskId));
        }
    }
    
    public void pause(String taskId);
    public void resume(String taskId);
    
    public int getQueueSize();
    public int getActiveCount();
    public List<DownloadTask> getActiveTasks();
    public List<DownloadTask> getQueuedTasks();
}

public class TaskScheduler {
    private final ThreadPoolExecutor executor;
    
    public void schedule(DownloadTask task) {
        executor.submit(() -> executeTask(task));
    }
    
    private void executeTask(DownloadTask task) {
        // 执行下载任务
    }
}
```

### 4.3 ChunkDownloader

```java
public class ChunkInfo {
    private int index;
    private long startByte;
    private long endByte;
    private long downloadedBytes;
    private ChunkStatus status;
    
    public enum ChunkStatus {
        PENDING, DOWNLOADING, COMPLETED, FAILED
    }
}

public class ChunkManager {
    public List<ChunkInfo> planChunks(long fileSize, int chunkCount) {
        // 计算分块范围
        long chunkSize = fileSize / chunkCount;
        List<ChunkInfo> chunks = new ArrayList<>();
        for (int i = 0; i < chunkCount; i++) {
            long start = i * chunkSize;
            long end = (i == chunkCount - 1) ? fileSize - 1 : start + chunkSize - 1;
            chunks.add(new ChunkInfo(i, start, end));
        }
        return chunks;
    }
    
    public boolean allCompleted(List<ChunkInfo> chunks);
    public void mergeChunks(List<ChunkInfo> chunks, Path output);
}

public class ChunkDownloader {
    private final HttpClient httpClient;
    
    public CompletableFuture<Void> downloadChunk(
            String url, 
            ChunkInfo chunk, 
            Path tempFile,
            ProgressCallback callback) {
        // 下载单个分块
        // 使用Range请求头
    }
}
```

### 4.4 ResumeSupport

```java
public class ResumeRecord {
    private String id;
    private String url;
    private String targetPath;
    private long totalSize;
    private long downloadedSize;
    private List<ChunkInfo> chunks;
    private Instant createdTime;
    private Instant lastModified;
    private String tempFilePath;
}

public class ResumeManager {
    private final Path recordsDir;
    
    public void saveRecord(DownloadTask task) {
        // 保存续传记录
    }
    
    public Optional<ResumeRecord> loadRecord(String taskId) {
        // 加载续传记录
    }
    
    public void deleteRecord(String taskId) {
        // 删除续传记录
    }
    
    public void cleanupOldRecords(Duration maxAge) {
        // 清理过期记录
    }
}

public class ResumeSupport {
    public boolean isSupported(String url) {
        // 发送HEAD请求检查Accept-Ranges头
    }
    
    public CompletableFuture<DownloadResult> downloadWithResume(
            DownloadRequest request) {
        // 1. 检查是否有续传记录
        // 2. 如果有，从断点继续
        // 3. 如果没有，新开始下载
        // 4. 定期保存进度
    }
}
```

### 4.5 ProgressTracker

```java
public class ProgressTracker {
    private long totalBytes;
    private long downloadedBytes;
    private Instant startTime;
    private final List<ProgressCallback> callbacks;
    private final ScheduledExecutorService scheduler;
    
    public void update(long bytes) {
        downloadedBytes += bytes;
        notifyCallbacks();
    }
    
    public double getProgress() {
        return totalBytes > 0 ? (double) downloadedBytes / totalBytes : 0;
    }
    
    public long getSpeed() {
        // 计算下载速度 bytes/s
    }
    
    public Duration getEstimatedTimeRemaining() {
        // 预估剩余时间
    }
    
    public void addCallback(ProgressCallback callback);
    
    private void notifyCallbacks() {
        ProgressEvent event = new ProgressEvent(
            downloadedBytes, totalBytes, getSpeed(), getEstimatedTimeRemaining()
        );
        callbacks.forEach(cb -> cb.onProgress(event));
    }
}

public class ProgressEvent {
    private long downloadedBytes;
    private long totalBytes;
    private double progress;
    private long speed;
    private Duration estimatedTimeRemaining;
}

public interface ProgressCallback {
    void onProgress(ProgressEvent event);
    void onComplete(DownloadResult result);
    void onError(Exception error);
}
```

### 4.6 RetryHandler

```java
public class RetryPolicy {
    private int maxRetries = 3;
    private Duration initialDelay = Duration.ofSeconds(1);
    private Duration maxDelay = Duration.ofSeconds(30);
    private double multiplier = 2.0;
    private BackoffStrategy strategy = BackoffStrategy.EXPONENTIAL;
    
    public enum BackoffStrategy {
        FIXED, LINEAR, EXPONENTIAL
    }
}

public class RetryHandler {
    private final RetryPolicy policy;
    
    public <T> CompletableFuture<T> executeWithRetry(
            Supplier<CompletableFuture<T>> action,
            Predicate<Exception> shouldRetry) {
        // 带重试的执行
    }
    
    public Duration calculateDelay(int attempt) {
        return switch (policy.getStrategy()) {
            case FIXED -> policy.getInitialDelay();
            case LINEAR -> policy.getInitialDelay().multipliedBy(attempt);
            case EXPONENTIAL -> {
                long delay = (long) (policy.getInitialDelay().toMillis() 
                    * Math.pow(policy.getMultiplier(), attempt - 1));
                yield Duration.ofMillis(Math.min(delay, policy.getMaxDelay().toMillis()));
            }
        };
    }
}

public class BackoffStrategy {
    public static Duration exponential(int attempt, Duration initial, double multiplier) {
        long delay = (long) (initial.toMillis() * Math.pow(multiplier, attempt - 1));
        return Duration.ofMillis(delay);
    }
    
    public static Duration linear(int attempt, Duration initial) {
        return initial.multipliedBy(attempt);
    }
    
    public static Duration fixed(Duration delay) {
        return delay;
    }
}
```

### 4.7 FileValidator

```java
public interface FileValidator {
    boolean validate(Path file, DownloadRequest request);
    String getValidationError();
}

public class ChecksumValidator implements FileValidator {
    @Override
    public boolean validate(Path file, DownloadRequest request) {
        if (request.getExpectedSha1() == null) return true;
        
        String actualSha1 = calculateSha1(file);
        return actualSha1.equalsIgnoreCase(request.getExpectedSha1());
    }
    
    private String calculateSha1(Path file) {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        return Hex.toHexString(md.digest());
    }
}

public class SizeValidator implements FileValidator {
    @Override
    public boolean validate(Path file, DownloadRequest request) {
        if (request.getExpectedSize() <= 0) return true;
        return Files.size(file) == request.getExpectedSize();
    }
}
```

### 4.8 DownloadConfig

```java
public class DownloadConfig {
    private int maxConcurrent = 4;
    private int maxRetries = 3;
    private int chunkCount = 4;
    private int connectTimeout = 10000;
    private int readTimeout = 30000;
    private boolean resumeEnabled = true;
    private Path tempDir;
    private ProxyConfig proxy;
}

public class ProxyConfig {
    private boolean enabled;
    private String host;
    private int port;
    private String username;
    private String password;
    private ProxyType type;
    
    public enum ProxyType {
        HTTP, SOCKS4, SOCKS5
    }
}
```

## 5. 关键流程

### 5.1 普通下载流程

```
创建下载请求
    ↓
加入下载队列
    ↓
调度器分配线程
    ↓
执行下载:
├── 发送HTTP请求
├── 创建目标文件
├── 流式写入
├── 更新进度
└── 重试(如失败)
    ↓
文件验证
    ↓
完成回调
```

### 5.2 分块下载流程

```
发送HEAD请求获取文件大小
    ↓
检查是否支持Range
    ↓
计算分块
    ↓
并行下载各分块:
├── 分块1: Range=0-999
├── 分块2: Range=1000-1999
├── 分块3: Range=2000-2999
└── 分块4: Range=3000-3999
    ↓
合并分块
    ↓
文件验证
    ↓
完成
```

### 5.3 断点续传流程

```
检查续传记录
    ↓
记录存在?
├── 是 → 读取进度
│       ↓
│       检查临时文件
│       ↓
│       继续下载
└── 否 → 新建下载
         ↓
         定期保存进度
         ↓
         下载完成?
         ├── 是 → 删除记录
         └── 否 → 保留记录
```

## 6. 配置文件

```json
{
  "download": {
    "maxConcurrent": 4,
    "maxRetries": 3,
    "chunkCount": 4,
    "connectTimeout": 10000,
    "readTimeout": 30000,
    "resumeEnabled": true,
    "tempDir": "./temp/downloads",
    "proxy": {
      "enabled": false,
      "type": "HTTP",
      "host": "127.0.0.1",
      "port": 7890,
      "username": "",
      "password": ""
    }
  }
}
```

## 7. 测试要点

- 单文件下载
- 批量下载并发控制
- 分块下载正确性
- 断点续传可靠性
- 重试机制
- 文件校验
- 代理支持
- 取消和暂停