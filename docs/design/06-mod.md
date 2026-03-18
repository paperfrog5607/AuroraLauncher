# 模块六：mod - 模组管理模块

## 1. 模块概述

模组扫描、解析、启用禁用、依赖分析、版本管理、安全检测、在线搜索下载。

## 2. 依赖关系

```
mod
├── core
├── download
└── api
```

## 3. 子包结构

```
com.aurora.mod/
├── scanner/
│   ├── ModScanner.java              # 模组扫描器
│   ├── ModFile.java                 # 模组文件模型
│   ├── ModInfo.java                 # 模组信息
│   └── ScanResult.java              # 扫描结果
├── parser/
│   ├── ModParser.java               # 解析器接口
│   ├── FabricModParser.java         # Fabric模组解析
│   ├── ForgeModParser.java          # Forge模组解析
│   ├── QuiltModParser.java          # Quilt模组解析
│   └── ModMetadata.java             # 模组元数据
├── manager/
│   ├── ModManager.java              # 模组管理器
│   ├── ModEnabler.java              # 启用/禁用器
│   ├── ModMover.java                # 模组移动器
│   └── ModRemover.java              # 模组删除器
├── dependency/
│   ├── DependencyAnalyzer.java      # 依赖分析器
│   ├── DependencyTree.java          # 依赖树
│   ├── DependencyResolver.java      # 依赖解析器
│   └── ConflictDetector.java        # 冲突检测器
├── version/
│   ├── VersionChecker.java          # 版本检查器
│   ├── UpdateInfo.java              # 更新信息
│   ├── VersionRange.java            # 版本范围
│   └── VersionComparator.java       # 版本比较器
├── security/
│   ├── ModSecurityScanner.java      # 安全扫描器
│   ├── SecurityReport.java          # 安全报告
│   ├── MalwareDetector.java         # 恶意代码检测
│   └── PermissionAnalyzer.java      # 权限分析器
├── compatibility/
│   ├── CompatibilityChecker.java    # 兼容性检查
│   ├── CompatibilityReport.java      # 兼容性报告
│   └── ServerModDetector.java       # 服务端模组检测
├── filename/
│   ├── FilenameFixer.java            # 文件名修复器
│   └── FilenameFormatter.java        # 文件名格式化器
└── search/
    ├── ModSearcher.java             # 模组搜索器
    ├── ModDownloader.java           # 模组下载器
    ├── BatchDownloader.java         # 批量下载器
    └── SearchResult.java            # 搜索结果
```

## 4. 核心类设计

### 4.1 ModScanner

```java
public class ModScanner {
    private final List<ModParser> parsers;
    
    public CompletableFuture<ScanResult> scan(Path modsDir) {
        // 1. 扫描mods目录下所有jar文件
        // 2. 排除.disabled文件
        // 3. 并行解析模组信息
        // 4. 返回扫描结果
    }
    
    private ModParser getParser(Path modFile) {
        String name = modFile.getFileName().toString();
        // 根据文件类型选择解析器
    }
}

public class ModFile {
    private Path file;
    private String fileName;
    private boolean enabled;
    private long fileSize;
    private String sha1;
    private Instant lastModified;
}

public class ModInfo {
    private String id;
    private String name;
    private String version;
    private String description;
    private List<String> authors;
    private String homepage;
    private String source;
    private String license;
    private List<Dependency> dependencies;
    private List<String> depends;
    private List<String> breaksWith;
    private Path filePath;
    private ModLoader loader;
    private String mcVersion;
    
    public enum ModLoader {
        FABRIC, FORGE, QUILT, NEOFORGE
    }
}

public class ScanResult {
    private List<ModInfo> mods;
    private List<Path> disabledMods;
    private List<Path> invalidMods;
    private List<ScanError> errors;
    private Instant scanTime;
}
```

### 4.2 ModParser

```java
public interface ModParser {
    boolean canParse(Path modFile);
    CompletableFuture<ModInfo> parse(Path modFile);
}

public class FabricModParser implements ModParser {
    @Override
    public boolean canParse(Path modFile) {
        return true; // 检查fabric.mod.json存在
    }
    
    @Override
    public CompletableFuture<ModInfo> parse(Path modFile) {
        // 1. 读取fabric.mod.json
        // 2. 解析元数据
        // 3. 构建ModInfo
    }
}

public class ForgeModParser implements ModParser {
    @Override
    public boolean canParse(Path modFile) {
        return true; // 检查META-INF/mods.toml
    }
    
    @Override
    public CompletableFuture<ModInfo> parse(Path modFile) {
        // 1. 读取mods.toml
        // 2. 解析元数据
        // 3. 构建ModInfo
    }
}
```

### 4.3 ModManager

```java
public class ModManager {
    private final Path modsDir;
    private final ModScanner scanner;
    private final ModEnabler enabler;
    
    public CompletableFuture<ScanResult> refresh() {
        return scanner.scan(modsDir);
    }
    
    public CompletableFuture<Void> enable(String modId) {
        // 将.mod.disabled改为.mod
    }
    
    public CompletableFuture<Void> disable(String modId) {
        // 将.mod改为.mod.disabled
    }
    
    public CompletableFuture<Void> delete(String modId);
    
    public CompletableFuture<Void> moveUp(String modId);
    public CompletableFuture<Void> moveDown(String modId);
    
    public CompletableFuture<Void> rename(String modId, String newName);
    
    public Optional<ModInfo> getMod(String modId);
    public List<ModInfo> getAllMods();
    public List<ModInfo> getEnabledMods();
    public List<ModInfo> getDisabledMods();
    
    public CompletableFuture<ModInfo> install(String modId, String version);
    public CompletableFuture<Void> update(String modId);
    public CompletableFuture<Void> updateAll();
}

public class ModEnabler {
    public void enable(Path modFile) {
        String name = modFile.getFileName().toString();
        if (name.endsWith(".disabled")) {
            Path newPath = Path.of(modFile.toString().replace(".disabled", ""));
            Files.move(modFile, newPath);
        }
    }
    
    public void disable(Path modFile) {
        String name = modFile.getFileName().toString();
        if (!name.endsWith(".disabled")) {
            Path newPath = Path.of(modFile.toString() + ".disabled");
            Files.move(modFile, newPath);
        }
    }
}
```

### 4.4 DependencyAnalyzer

```java
public class Dependency {
    private String modId;
    private VersionRange versionRange;
    private DependencyType type;
    private boolean optional;
    
    public enum DependencyType {
        DEPENDS,      // 必须依赖
        RECOMMENDS,   // 推荐依赖
        SUGGESTS,     // 建议依赖
        BREAKS,       // 冲突
        CONFLICTS     // 不兼容
    }
}

public class DependencyAnalyzer {
    public CompletableFuture<DependencyTree> analyze(List<ModInfo> mods) {
        // 1. 收集所有依赖声明
        // 2. 构建依赖图
        // 3. 检测缺失依赖
        // 4. 检测冲突
        // 5. 返回依赖树
    }
    
    public List<Dependency> getMissingDependencies(List<ModInfo> mods);
    public List<ConflictInfo> getConflicts(List<ModInfo> mods);
}

public class DependencyTree {
    private Map<String, List<Dependency>> dependencies;
    private Map<String, List<String>> dependents;  // 反向依赖
    
    public List<Dependency> getDependencies(String modId);
    public List<String> getDependents(String modId);
    public boolean hasMissingDependencies();
    public boolean hasConflicts();
}

public class ConflictDetector {
    public List<ConflictInfo> detect(List<ModInfo> mods) {
        // 检测:
        // 1. breaksWith声明
        // 2. conflicts声明
        // 3. 版本范围冲突
        // 4. 重复模组
    }
}

public class ConflictInfo {
    private String mod1;
    private String mod2;
    private ConflictType type;
    private String reason;
    
    public enum ConflictType {
        BREAKS, CONFLICT, VERSION_MISMATCH, DUPLICATE
    }
}
```

### 4.5 VersionChecker

```java
public class VersionChecker {
    private final ModrinthClient modrinth;
    private final CurseForgeClient curseforge;
    
    public CompletableFuture<UpdateInfo> checkUpdate(ModInfo mod) {
        // 1. 在Modrinth查找
        // 2. 在CurseForge查找
        // 3. 比较版本
        // 4. 返回更新信息
    }
    
    public CompletableFuture<Map<String, UpdateInfo>> checkAllUpdates(List<ModInfo> mods) {
        // 批量检查更新
    }
}

public class UpdateInfo {
    private String modId;
    private String currentVersion;
    private String latestVersion;
    private String changelog;
    private String downloadUrl;
    private String source;  // "modrinth" or "curseforge"
    private Instant releaseDate;
    private VersionType versionType;
    
    public enum VersionType {
        RELEASE, BETA, ALPHA
    }
}

public class VersionRange {
    private String minVersion;
    private String maxVersion;
    private boolean includeMin;
    private boolean includeMax;
    
    public boolean matches(String version) {
        // 检查版本是否在范围内
    }
}
```

### 4.6 ModSecurityScanner

```java
public class ModSecurityScanner {
    public CompletableFuture<SecurityReport> scan(ModInfo mod) {
        // 1. 检查文件哈希
        // 2. 检查权限声明
        // 3. 检查网络访问
        // 4. 检查文件操作
        // 5. 检查可疑代码模式
        // 6. 返回报告
    }
}

public class SecurityReport {
    private String modId;
    private RiskLevel overallRisk;
    private List<SecurityIssue> issues;
    private List<PermissionRequest> permissions;
    
    public enum RiskLevel {
        SAFE, LOW, MEDIUM, HIGH, CRITICAL
    }
}

public class SecurityIssue {
    private IssueType type;
    private String description;
    private RiskLevel risk;
    private String location;  // 代码位置或文件位置
    
    public enum IssueType {
        NETWORK_ACCESS, FILE_ACCESS, CLASS_LOADER,
        NATIVE_CODE, REFLECTION, SUSPICIOUS_PATTERN
    }
}

public class PermissionAnalyzer {
    public List<PermissionRequest> analyze(ModInfo mod) {
        // 分析模组请求的权限:
        // - 网络访问
        // - 文件读写
        // - 反射
        // - 类加载
    }
}
```

### 4.7 ModSearcher

```java
public class ModSearcher {
    private final ModrinthClient modrinth;
    private final CurseForgeClient curseforge;
    
    public CompletableFuture<List<SearchResult>> search(String query, 
                                                        SearchOptions options) {
        // 并行搜索两个平台
        // 合并去重结果
    }
    
    public CompletableFuture<List<String>> getVersions(String modId);
    public CompletableFuture<ModInfo> getModInfo(String modId);
}

public class SearchOptions {
    private String query;
    private String mcVersion;
    private String loader;
    private String category;
    private String sortBy = "relevance";
    private int limit = 20;
    private int offset = 0;
}

public class SearchResult {
    private String id;
    private String slug;
    private String name;
    private String description;
    private String author;
    private String iconUrl;
    private int downloads;
    private String source;  // "modrinth" or "curseforge"
    private List<String> categories;
}

public class ModDownloader {
    private final DownloadService downloadService;
    
    public CompletableFuture<Path> download(String modId, String version, 
                                            Path target, ProgressCallback callback) {
        // 1. 获取下载链接
        // 2. 下载文件
        // 3. 校验SHA1
        // 4. 返回文件路径
    }
    
    public CompletableFuture<List<Path>> batchDownload(
            List<DownloadRequest> requests, ProgressCallback callback) {
        // 批量下载，并发控制
    }
}
```

### 4.8 FilenameFixer

```java
public class FilenameFixer {
    public String fix(String originalFilename, ModInfo mod) {
        // 将随机文件名改为规范格式
        // 格式: [modid]-[version].jar
    }
    
    public String format(ModInfo mod, String format) {
        // 支持自定义格式:
        // {name}, {id}, {version}, {author}
    }
}
```

## 5. 关键流程

### 5.1 模组扫描流程

```
扫描mods目录
    ↓
遍历所有.jar文件
    ↓
并行解析:
├── 检测加载器类型
├── 读取元数据文件
├── 提取模组信息
└── 计算SHA1
    ↓
收集结果
    ↓
返回扫描结果
```

### 5.2 依赖分析流程

```
获取所有模组信息
    ↓
收集依赖声明
    ↓
构建依赖图:
├── 解析depends
├── 解析recommends
├── 解析breaks
└── 解析conflicts
    ↓
检测问题:
├── 缺失依赖
├── 版本冲突
├── 不兼容模组
└── 循环依赖
    ↓
生成依赖树和报告
```

### 5.3 更新检查流程

```
获取模组列表
    ↓
并行查询每个模组:
├── 计算SHA1
├── 查询Modrinth
├── 查询CurseForge
└── 匹配项目ID
    ↓
比较版本:
├── 获取最新版本
├── 比较版本号
└── 检查MC版本兼容
    ↓
返回更新列表
```

## 6. 配置文件

```json
{
  "modManagement": {
    "autoUpdateCheck": true,
    "updateCheckInterval": "daily",
    "autoFixFilenames": true,
    "filenameFormat": "{name}-{version}.jar",
    "securityScan": true,
    "checkDependencies": true
  }
}
```

## 7. 测试要点

- 模组解析正确性（Fabric/Forge/Quilt）
- 依赖分析准确性
- 冲突检测完整性
- 版本比较逻辑
- 安全扫描可靠性
- 批量下载稳定性