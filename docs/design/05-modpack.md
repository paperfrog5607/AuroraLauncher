# 模块五：modpack - 整合包管理模块

## 1. 模块概述

管理整合包实例、备份、模板、导入导出功能。支持CurseForge和Modrinth格式。

## 2. 依赖关系

```
modpack
├── core
├── launcher
├── mod
├── download
└── api
```

## 3. 子包结构

```
com.aurora.modpack/
├── instance/
│   ├── Instance.java                 # 实例模型
│   ├── InstanceManager.java          # 实例管理器
│   ├── InstanceBuilder.java          # 实例构建器
│   ├── InstanceConfig.java           # 实例配置
│   └── InstanceValidator.java        # 实例验证器
├── backup/
│   ├── Backup.java                   # 备份模型
│   ├── BackupManager.java            # 备份管理器
│   ├── BackupTask.java               # 备份任务
│   └── BackupScheduler.java          # 备份调度器
├── template/
│   ├── Template.java                 # 模板模型
│   ├── TemplateManager.java          # 模板管理器
│   └── TemplateBuilder.java          # 模板构建器
├── export/
│   ├── Exporter.java                 # 导出器接口
│   ├── CurseForgeExporter.java       # CF格式导出
│   ├── ModrinthExporter.java         # MR格式导出
│   ├── DualExporter.java             # 双平台导出
│   └── ExportOptions.java            # 导出选项
├── import_/
│   ├── Importer.java                 # 导入器接口
│   ├── CurseForgeImporter.java       # CF格式导入
│   ├── ModrinthImporter.java         # MR格式导入
│   └── ImportTask.java               # 导入任务
├── share/
│   ├── ShareCode.java                # 分享码模型
│   ├── ShareCodeGenerator.java       # 分享码生成器
│   ├── ShareCodeParser.java          # 分享码解析器
│   └── ShareCodeDownloader.java      # 分享码下载器
├── stats/
│   ├── InstanceStats.java            # 实例统计
│   ├── StatsCollector.java            # 统计收集器
│   └── StatsExporter.java            # 统计导出器
└── verify/
    ├── IntegrityChecker.java         # 完整性检查
    ├── DependencyChecker.java        # 依赖检查
    └── VerifyReport.java             # 验证报告
```

## 4. 核心类设计

### 4.1 Instance

```java
public class Instance {
    private String id;
    private String name;
    private String version;
    private Path instanceDir;
    private InstanceConfig config;
    private ModLoaderInfo loader;
    private Instant createdTime;
    private Instant lastPlayed;
    private long playTime;
    private String iconPath;
    private List<String> tags;
    private InstanceState state;
    
    public enum InstanceState {
        READY, RUNNING, UPDATING, ERROR
    }
}

public class InstanceConfig {
    private String minecraftVersion;
    private String loaderType;
    private String loaderVersion;
    private MemoryConfig memory;
    private JavaConfig java;
    private WindowConfig window;
    private List<String> customArgs;
}

public class InstanceManager {
    private final Path instancesDir;
    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    
    public CompletableFuture<Instance> create(InstanceBuilder builder);
    public CompletableFuture<Void> delete(String instanceId);
    public CompletableFuture<Instance> clone(String instanceId, String newName);
    public CompletableFuture<Void> rename(String instanceId, String newName);
    
    public Optional<Instance> getInstance(String instanceId);
    public List<Instance> getAllInstances();
    public List<Instance> search(String query);
    
    public CompletableFuture<Void> update(Instance instance);
    public CompletableFuture<Void> repair(Instance instance);
    
    public void addInstanceListener(InstanceListener listener);
}

public class InstanceBuilder {
    private String name;
    private String minecraftVersion;
    private String loaderType;
    private String loaderVersion;
    private Path iconPath;
    private List<String> tags;
    
    public InstanceBuilder fromCurseForge(Path cfZip);
    public InstanceBuilder fromModrinth(Path mrZip);
    public InstanceBuilder fromTemplate(Template template);
    public InstanceBuilder fromShareCode(String code);
    
    public CompletableFuture<Instance> build();
}
```

### 4.2 BackupManager

```java
public class Backup {
    private String id;
    private String instanceId;
    private String name;
    private String description;
    private Instant createdTime;
    private long size;
    private BackupType type;
    private Path backupFile;
    
    public enum BackupType {
        FULL,           // 完整备份
        INCREMENTAL,    // 增量备份
        CONFIG_ONLY,    // 仅配置
        WORLD_ONLY      // 仅存档
    }
}

public class BackupManager {
    private final Path backupDir;
    
    public CompletableFuture<Backup> createBackup(String instanceId, BackupOptions options);
    public CompletableFuture<Void> restore(String instanceId, String backupId);
    public CompletableFuture<Void> deleteBackup(String backupId);
    
    public List<Backup> getBackups(String instanceId);
    public Optional<Backup> getLatestBackup(String instanceId);
    
    public CompletableFuture<Void> scheduleBackup(String instanceId, ScheduleConfig config);
    public void cancelSchedule(String instanceId);
}

public class BackupOptions {
    private BackupType type = BackupType.FULL;
    private boolean includeWorlds = true;
    private boolean includeConfigs = true;
    private boolean includeMods = true;
    private boolean includeLogs = false;
    private int maxBackups = 10;
}
```

### 4.3 Exporter

```java
public interface Exporter {
    String getFormat();
    CompletableFuture<Path> export(Instance instance, ExportOptions options);
}

public class CurseForgeExporter implements Exporter {
    @Override
    public CompletableFuture<Path> export(Instance instance, ExportOptions options) {
        // 1. 收集模组信息
        // 2. 匹配CF项目ID
        // 3. 生成manifest.json
        // 4. 打包为zip
    }
}

public class ModrinthExporter implements Exporter {
    @Override
    public CompletableFuture<Path> export(Instance instance, ExportOptions options) {
        // 1. 收集模组信息
        // 2. 匹配MR项目ID
        // 3. 生成modrinth.index.json
        // 4. 打包为mrpack
    }
}

public class DualExporter {
    public CompletableFuture<Map<String, Path>> exportBoth(Instance instance) {
        // 同时导出两种格式
    }
}

public class ExportOptions {
    private boolean includeWorlds = false;
    private boolean includeServerPack = false;
    private boolean overrideExisting = false;
    private boolean skipUnknownMods = false;
    private Path outputPath;
    private String name;
    private String version;
    private String author;
}
```

### 4.4 ShareCode

```java
public class ShareCode {
    private String code;
    private String instanceId;
    private Instant createdTime;
    private Instant expiresTime;
    private String format;  // "cf" or "mr"
    private String downloadUrl;
}

public class ShareCodeGenerator {
    public ShareCode generate(Instance instance, ShareOptions options) {
        // 1. 导出整合包
        // 2. 上传到云存储
        // 3. 生成分享码
        // 4. 返回分享信息
    }
}

public class ShareCodeParser {
    public ShareCode parse(String code) {
        // 解析分享码格式: AURORA-XXXX-XXXX
    }
}

public class ShareCodeDownloader {
    public CompletableFuture<Instance> download(String shareCode) {
        // 1. 解析分享码
        // 2. 获取下载链接
        // 3. 下载整合包
        // 4. 导入创建实例
    }
}
```

### 4.5 StatsCollector

```java
public class InstanceStats {
    private String instanceId;
    private int modCount;
    private int resourcePackCount;
    private int shaderPackCount;
    private int worldCount;
    private long totalSize;
    private long modSize;
    private long configSize;
    private long worldSize;
    private long playTimeSeconds;
    private int launchCount;
    private Map<String, Long> modSizes;
}

public class StatsCollector {
    public CompletableFuture<InstanceStats> collect(Instance instance) {
        // 扫描实例目录
        // 统计各类文件
        // 计算大小
        // 返回统计结果
    }
    
    public CompletableFuture<Map<String, InstanceStats>> collectAll() {
        // 收集所有实例统计
    }
}
```

### 4.6 IntegrityChecker

```java
public class IntegrityChecker {
    public CompletableFuture<VerifyReport> check(Instance instance) {
        // 1. 检查核心文件
        // 2. 检查模组完整性
        // 3. 检查依赖关系
        // 4. 检查配置文件
        // 5. 生成报告
    }
    
    public CompletableFuture<Void> repair(Instance instance, VerifyReport report) {
        // 根据报告修复问题
    }
}

public class VerifyReport {
    private String instanceId;
    private Instant checkTime;
    private List<FileIssue> missingFiles;
    private List<FileIssue> corruptedFiles;
    private List<DependencyIssue> missingDependencies;
    private List<DependencyIssue> conflictDependencies;
    private List<ConfigIssue> configIssues;
    private boolean passed;
    
    public static class FileIssue {
        private Path path;
        private IssueType type;
        private String expectedHash;
        private String actualHash;
    }
    
    public static class DependencyIssue {
        private String modId;
        private String requiredMod;
        private String requiredVersion;
        private IssueType type;
    }
    
    public enum IssueType {
        MISSING, CORRUPTED, VERSION_MISMATCH, CONFLICT
    }
}
```

## 5. 关键流程

### 5.1 创建实例流程

```
用户创建实例
    ↓
选择创建方式:
├── 从零创建 → 选择MC版本 → 选择加载器 → 完成
├── 导入整合包 → 解析manifest → 下载依赖 → 完成
├── 使用模板 → 应用模板 → 完成
└── 分享码导入 → 下载整合包 → 导入 → 完成
    ↓
创建实例目录结构
    ↓
生成实例配置
    ↓
实例就绪
```

### 5.2 导出流程

```
用户选择导出
    ↓
选择导出格式:
├── CurseForge
├── Modrinth
└── 双平台
    ↓
扫描模组目录
    ↓
匹配平台项目ID:
├── 本地SHA1匹配
├── Modrinth API查询
└── CurseForge API查询
    ↓
生成manifest文件
    ↓
打包压缩文件
    ↓
导出完成
```

### 5.3 备份流程

```
用户选择备份
    ↓
选择备份类型:
├── 完整备份
├── 增量备份
├── 仅配置
└── 仅存档
    ↓
扫描需要备份的文件
    ↓
压缩打包
    ↓
存储到备份目录
    ↓
清理旧备份(如超过上限)
    ↓
备份完成
```

## 6. 配置文件

### 6.1 实例配置

```json
{
  "id": "instance-uuid",
  "name": "My Modpack",
  "version": "1.0.0",
  "minecraft": {
    "version": "1.20.4",
    "loader": {
      "type": "fabric",
      "version": "0.15.7"
    }
  },
  "memory": {
    "preset": "standard",
    "minMB": 2048,
    "maxMB": 4096
  },
  "java": {
    "path": "/path/to/java",
    "args": ["-XX:+UseG1GC"]
  },
  "tags": ["tech", "survival"],
  "icon": "icon.png",
  "created": "2024-01-01T00:00:00Z",
  "lastPlayed": "2024-01-02T12:00:00Z",
  "playTime": 3600
}
```

### 6.2 备份配置

```json
{
  "autoBackup": true,
  "backupInterval": "daily",
  "maxBackups": 10,
  "backupBeforeUpdate": true,
  "includeWorlds": true,
  "includeConfigs": true,
  "includeMods": false
}
```

## 7. 目录结构

```
instances/
├── instance-1/
│   ├── instance.json          # 实例配置
│   ├── .minecraft/
│   │   ├── mods/
│   │   ├── config/
│   │   ├── saves/
│   │   ├── resourcepacks/
│   │   ├── shaderpacks/
│   │   └── options.txt
│   └── icon.png
└── instance-2/
    └── ...

backups/
├── instance-1/
│   ├── backup-2024-01-01.zip
│   └── backup-2024-01-02.zip
└── instance-2/
    └── ...

templates/
├── tech-template.json
└── magic-template.json
```

## 8. 测试要点

- 实例创建、克隆、删除
- 备份和恢复功能
- 导出导入正确性
- 分享码生成和解析
- 完整性检查准确性
- 统计数据收集