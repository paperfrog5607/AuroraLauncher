# 模块十：diagnostic - 诊断工具模块

## 1. 模块概述

崩溃分析、日志分析、性能分析、FPS分析、冲突检测。

## 2. 依赖关系

```
diagnostic
├── core
├── mod (依赖分析)
└── ai (可选，用于智能分析)
```

## 3. 子包结构

```
com.aurora.diagnostic/
├── crash/
│   ├── CrashAnalyzer.java            # 崩溃分析器
│   ├── CrashReport.java              # 崩溃报告模型
│   ├── CrashPattern.java            # 崩溃模式匹配
│   └── CrashSolution.java            # 解决方案
├── log/
│   ├── LogAnalyzer.java              # 日志分析器
│   ├── LogParser.java                # 日志解析器
│   ├── LogLevel.java                # 日志级别
│   └── LogEntry.java                # 日志条目
├── performance/
│   ├── PerformanceMonitor.java       # 性能监控器
│   ├── MemoryAnalyzer.java           # 内存分析器
│   ├── CpuAnalyzer.java              # CPU分析器
│   └── PerformanceReport.java        # 性能报告
├── fps/
│   ├── FpsMonitor.java               # FPS监控器
│   ├── FpsAnalyzer.java              # FPS分析器
│   ├── FpsLog.java                  # FPS日志
│   └── FpsChart.java                # FPS图表
├── conflict/
│   ├── ConflictAnalyzer.java         # 冲突分析器
│   ├── ConflictReport.java           # 冲突报告
│   └── ModCompatibilityChecker.java  # 模组兼容性检查
└── report/
    ├── DiagnosticReport.java         # 诊断报告
    ├── ReportBuilder.java            # 报告构建器
    └── ReportExporter.java           # 报告导出器
```

## 4. 核心类设计

### 4.1 CrashAnalyzer

```java
public class CrashAnalyzer {
    private final List<CrashPattern> patterns;
    
    public CrashReport analyze(String crashLog) {
        // 1. 解析崩溃日志
        // 2. 匹配已知崩溃模式
        // 3. 识别崩溃原因
        // 4. 提供解决方案
    }
    
    private List<CrashPattern> loadPatterns() {
        // 加载内置崩溃模式
        // 可扩展：从文件加载自定义模式
    }
}

public class CrashReport {
    private String crashId;
    private CrashType type;
    private String summary;
    private String detailedDescription;
    private List<String> suspectedMods;
    private String exceptionType;
    private String stackTrace;
    private List<CrashSolution> solutions;
    private Instant analysisTime;
    private Confidence confidence;
    
    public enum CrashType {
        JAVA_VERSION, OUT_OF_MEMORY, MOD_CONFLICT, 
        CORRUPTED_FILE, DRIVER_ISSUE, UNKNOWN
    }
    
    public enum Confidence {
        HIGH, MEDIUM, LOW
    }
}

public class CrashPattern {
    private String id;
    private String name;
    private Pattern regex;
    private CrashType type;
    private String description;
    private List<String> keywords;
    private List<CrashSolution> solutions;
    private int priority;
    
    public boolean matches(String crashLog) {
        if (regex != null && regex.matcher(crashLog).find()) {
            return true;
        }
        return keywords.stream().allMatch(crashLog::contains);
    }
}

public class CrashSolution {
    private String title;
    private String description;
    private SolutionType type;
    private String action;  // 具体操作
    private String link;    // 相关链接
    
    public enum SolutionType {
        UPDATE_MOD, REMOVE_MOD, CHANGE_CONFIG, 
        UPDATE_JAVA, ALLOCATE_MEMORY, REINSTALL,
        UPDATE_DRIVER, MANUAL_FIX
    }
}
```

### 4.2 LogAnalyzer

```java
public class LogAnalyzer {
    public LogAnalysisResult analyze(Path logFile) {
        // 1. 解析日志文件
        // 2. 提取关键信息
        // 3. 识别错误和警告
        // 4. 分析问题
    }
}

public class LogParser {
    public List<LogEntry> parse(Path logFile) {
        // 解析各种日志格式:
        // - latest.log
        // - debug.log
        // - crash-*.txt
    }
    
    private LogEntry parseLine(String line) {
        // [timestamp] [level] [thread] message
        // 或其他格式
    }
}

public class LogEntry {
    private Instant timestamp;
    private LogLevel level;
    private String thread;
    private String logger;
    private String message;
    private Throwable throwable;
    
    public enum LogLevel {
        FATAL, ERROR, WARN, INFO, DEBUG, TRACE
    }
}

public class LogAnalysisResult {
    private List<LogEntry> errors;
    private List<LogEntry> warnings;
    private List<String> loadedMods;
    private List<String> failedMods;
    private MemoryInfo memoryInfo;
    private JavaInfo javaInfo;
    private List<String> recommendations;
}
```

### 4.3 PerformanceMonitor

```java
public class PerformanceMonitor {
    private final ScheduledExecutorService scheduler;
    private final List<PerformanceSample> samples;
    private volatile boolean monitoring;
    
    public void start(Duration interval) {
        monitoring = true;
        scheduler.scheduleAtFixedRate(this::sample, 0, 
            interval.toMillis(), TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        monitoring = false;
    }
    
    private void sample() {
        PerformanceSample sample = new PerformanceSample(
            Instant.now(),
            getMemoryUsage(),
            getCpuUsage(),
            getFps(),
            getThreadCount()
        );
        samples.add(sample);
    }
    
    public PerformanceReport generateReport(Duration period) {
        // 生成性能报告
    }
}

public class PerformanceSample {
    private Instant timestamp;
    private MemoryUsage memory;
    private double cpuUsage;
    private int fps;
    private int threadCount;
    
    public static class MemoryUsage {
        private long heapUsed;
        private long heapMax;
        private long nonHeapUsed;
    }
}

public class PerformanceReport {
    private Duration duration;
    private int sampleCount;
    private double averageFps;
    private int minFps;
    private int maxFps;
    private double averageMemoryUsage;
    private long peakMemoryUsage;
    private double averageCpuUsage;
    private double peakCpuUsage;
    private List<PerformanceIssue> issues;
}

public class PerformanceIssue {
    private IssueType type;
    private String description;
    private Instant timestamp;
    private Severity severity;
    
    public enum IssueType {
        LOW_FPS, HIGH_MEMORY, MEMORY_LEAK, HIGH_CPU, GC_PAUSE
    }
    
    public enum Severity {
        INFO, WARNING, CRITICAL
    }
}

public class MemoryAnalyzer {
    public MemoryInfo analyze() {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryInfo(
            runtime.totalMemory(),
            runtime.freeMemory(),
            runtime.maxMemory(),
            calculateGcOverhead()
        );
    }
    
    public List<MemoryLeak> detectLeaks(List<PerformanceSample> samples) {
        // 检测内存泄漏模式
    }
}
```

### 4.4 FpsAnalyzer

```java
public class FpsMonitor {
    private final Path logDir;
    private final List<FpsLog> fpsLogs;
    
    public void startMonitoring(String instanceId) {
        // 开始监控FPS
        // 解析游戏日志中的FPS数据
    }
    
    public List<FpsLog> getLogs(String instanceId) {
        // 获取FPS日志
    }
    
    public FpsChart generateChart(FpsLog log) {
        // 生成FPS图表数据
    }
}

public class FpsLog {
    private String instanceId;
    private Instant startTime;
    private Instant endTime;
    private List<FpsSample> samples;
    private FpsStatistics statistics;
}

public class FpsSample {
    private Instant timestamp;
    private int fps;
    private int frameTime;  // ms
    private String location;  // 游戏内位置
}

public class FpsStatistics {
    private double average;
    private int min;
    private int max;
    private double percentile1;  // 1% low
    private double percentile5;  // 5% low
    private double standardDeviation;
}

public class FpsChart {
    private List<Point> dataPoints;
    private double[] fpsBuckets;
    private Map<String, Double> averageByLocation;
    
    public static class Point {
        private long timestamp;
        private int fps;
    }
}
```

### 4.5 ConflictAnalyzer

```java
public class ConflictAnalyzer {
    private final ModManager modManager;
    private final ConflictDatabase database;
    
    public ConflictReport analyze() {
        // 1. 获取所有模组信息
        // 2. 检查已知冲突
        // 3. 分析依赖冲突
        // 4. 生成报告
    }
    
    private List<Conflict> checkKnownConflicts(List<ModInfo> mods) {
        // 检查已知冲突数据库
    }
    
    private List<Conflict> checkDependencyConflicts(List<ModInfo> mods) {
        // 检查依赖版本冲突
    }
}

public class ConflictReport {
    private List<Conflict> conflicts;
    private List<Warning> warnings;
    private Instant analysisTime;
    
    public boolean hasCriticalConflicts() {
        return conflicts.stream()
            .anyMatch(c -> c.getSeverity() == Severity.CRITICAL);
    }
}

public class Conflict {
    private String mod1;
    private String mod2;
    private ConflictType type;
    private String reason;
    private String solution;
    private Severity severity;
    private String reference;
    
    public enum ConflictType {
        INCOMPATIBLE, VERSION_MISMATCH, DEPENDENCY_MISSING,
        DUPLICATE, PERFORMANCE_IMPACT
    }
    
    public enum Severity {
        INFO, WARNING, CRITICAL
    }
}

public class ConflictDatabase {
    private final Map<String, List<ConflictEntry>> entries;
    
    public List<ConflictEntry> lookup(String modId) {
        return entries.getOrDefault(modId, Collections.emptyList());
    }
    
    public void addEntry(ConflictEntry entry) {
        // 添加冲突条目
    }
    
    public void load(Path databaseFile) {
        // 从文件加载
    }
}

public class ModCompatibilityChecker {
    public CompatibilityResult check(ModInfo mod, List<ModInfo> installedMods) {
        // 检查模组与已安装模组的兼容性
    }
    
    public boolean isCompatible(String modId, String mcVersion, String loader) {
        // 检查模组是否兼容指定版本
    }
}
```

### 4.6 DiagnosticReport

```java
public class DiagnosticReport {
    private String instanceId;
    private Instant generatedTime;
    private SystemInfo systemInfo;
    private JavaInfo javaInfo;
    private MinecraftInfo minecraftInfo;
    private List<ModInfo> mods;
    private Optional<CrashReport> crashReport;
    private Optional<LogAnalysisResult> logAnalysis;
    private Optional<PerformanceReport> performance;
    private Optional<ConflictReport> conflicts;
    private List<Recommendation> recommendations;
    
    public String exportAsText();
    public String exportAsMarkdown();
    public byte[] exportAsJson();
}

public class SystemInfo {
    private String osName;
    private String osVersion;
    private String osArch;
    private int cpuCores;
    private long totalMemory;
    private String gpuName;
    private String gpuDriver;
}

public class Recommendation {
    private String title;
    private String description;
    private Priority priority;
    private String action;
    
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}

public class ReportBuilder {
    private DiagnosticReport report;
    
    public ReportBuilder systemInfo(SystemInfo info) {
        report.setSystemInfo(info);
        return this;
    }
    
    public ReportBuilder crashReport(CrashReport crash) {
        report.setCrashReport(Optional.ofNullable(crash));
        return this;
    }
    
    // ... 其他构建方法
    
    public DiagnosticReport build() {
        generateRecommendations();
        return report;
    }
}
```

## 5. 关键流程

### 5.1 崩溃分析流程

```
接收崩溃日志
    ↓
解析日志结构
    ↓
匹配崩溃模式:
├── Java版本问题
├── 内存不足
├── 模组冲突
├── 文件损坏
├── 驱动问题
└── 其他已知模式
    ↓
提取相关信息:
├── 异常类型
├── 堆栈追踪
├── 可疑模组
└── 环境信息
    ↓
生成解决方案
    ↓
返回崩溃报告
```

### 5.2 性能监控流程

```
启动监控
    ↓
定时采样:
├── 内存使用
├── CPU使用
├── FPS
└── 线程数
    ↓
存储样本数据
    ↓
分析趋势:
├── FPS波动
├── 内存增长
├── CPU峰值
└── GC频率
    ↓
检测问题
    ↓
生成报告
```

### 5.3 冲突检测流程

```
获取模组列表
    ↓
并行检测:
├── 已知冲突数据库
├── 依赖关系冲突
├── 版本兼容性
└── 重复模组
    ↓
收集检测结果
    ↓
按严重程度排序
    ↓
生成冲突报告
```

## 6. 配置文件

```json
{
  "diagnostic": {
    "crashAnalyzer": {
      "patternFiles": ["patterns/default.json", "patterns/custom.json"]
    },
    "performanceMonitor": {
      "sampleInterval": 1000,
      "maxSamples": 3600,
      "alertThresholds": {
        "fps": 30,
        "memoryPercent": 90,
        "cpuPercent": 80
      }
    },
    "fpsMonitor": {
      "enabled": true,
      "logInterval": 1000
    },
    "conflictDatabase": {
      "autoUpdate": true,
      "updateUrl": "https://example.com/conflicts.json"
    }
  }
}
```

## 7. 测试要点

- 崩溃日志解析正确性
- 崩溃模式匹配准确性
- 解决方案有效性
- 性能采样精度
- FPS监控可靠性
- 冲突检测完整性