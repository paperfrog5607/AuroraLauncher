# 模块九：api - API客户端模块

## 1. 模块概述

Modrinth、CurseForge、Mojang API客户端，提供统一搜索接口。

## 2. 依赖关系

```
api
├── core (网络、缓存、日志)
└── download (下载服务)
```

## 3. 子包结构

```
com.aurora.api/
├── common/
│   ├── ApiClient.java               # API客户端基类
│   ├── ApiResponse.java             # API响应模型
│   ├── ApiError.java                # API错误模型
│   ├── RateLimiter.java            # 速率限制器
│   └── RequestBuilder.java          # 请求构建器
├── modrinth/
│   ├── ModrinthClient.java          # Modrinth客户端
│   ├── ModrinthProject.java         # 项目模型
│   ├── ModrinthVersion.java         # 版本模型
│   ├── ModrinthUser.java            # 用户模型
│   ├── ModrinthSearch.java          # 搜索模型
│   └── ModrinthCategory.java        # 分类模型
├── curseforge/
│   ├── CurseForgeClient.java        # CurseForge客户端
│   ├── CurseForgeMod.java           # Mod模型
│   ├── CurseForgeFile.java          # 文件模型
│   ├── CurseForgeCategory.java      # 分类模型
│   └── CurseForgeFingerprint.java   # 指纹模型
├── mojang/
│   ├── MojangClient.java            # Mojang客户端
│   ├── MojangUser.java              # 用户模型
│   ├── MojangProfile.java           # 皮肤模型
│   ├── MojangStatus.java            # 服务状态
│   └── MojangAuth.java              # 认证模型
├── unified/
│   ├── UnifiedSearch.java           # 统一搜索接口
│   ├── UnifiedMod.java              # 统一Mod模型
│   ├── UnifiedVersion.java          # 统一版本模型
│   └── SearchResult.java            # 搜索结果
└── cache/
    ├── ApiCache.java                # API缓存
    └── CacheKey.java               # 缓存键
```

## 4. 核心类设计

### 4.1 ApiClient (基类)

```java
public abstract class ApiClient {
    protected final HttpClient httpClient;
    protected final ApiCache cache;
    protected final RateLimiter rateLimiter;
    protected final String baseUrl;
    
    protected <T> CompletableFuture<T> get(String endpoint, 
                                            Type type,
                                            Duration cacheTtl) {
        // 1. 检查缓存
        // 2. 速率限制
        // 3. 发送请求
        // 4. 解析响应
        // 5. 缓存结果
    }
    
    protected <T> CompletableFuture<T> post(String endpoint,
                                             Object body,
                                             Type type) {
        // POST请求
    }
    
    protected void checkRateLimit() {
        rateLimiter.acquire();
    }
}

public class RateLimiter {
    private final Semaphore semaphore;
    private final int maxRequests;
    private final Duration period;
    
    public void acquire() {
        semaphore.acquire();
    }
    
    public void release() {
        semaphore.release();
    }
}

public class ApiError {
    private int code;
    private String message;
    private String details;
}
```

### 4.2 ModrinthClient

```java
public class ModrinthClient extends ApiClient {
    private static final String BASE_URL = "https://api.modrinth.com/v2";
    
    // 搜索
    public CompletableFuture<ModrinthSearchResult> search(
            String query, 
            ModrinthSearchOptions options) {
        // GET /search
        // 参数: query, facets, index, offset, limit
    }
    
    // 项目
    public CompletableFuture<ModrinthProject> getProject(String slugOrId) {
        // GET /project/{slug|id}
    }
    
    public CompletableFuture<List<ModrinthProject>> getProjects(List<String> ids) {
        // GET /projects?ids=[...]
    }
    
    // 版本
    public CompletableFuture<List<ModrinthVersion>> getVersions(String projectId) {
        // GET /project/{id|slug}/version
    }
    
    public CompletableFuture<ModrinthVersion> getVersion(String versionId) {
        // GET /version/{id}
    }
    
    public CompletableFuture<List<ModrinthVersion>> getVersions(List<String> versionIds) {
        // GET /versions?ids=[...]
    }
    
    // 文件
    public CompletableFuture<ModrinthVersion> getVersionByHash(String hash, String algorithm) {
        // GET /version_file/{hash}?algorithm={algorithm}
    }
    
    public CompletableFuture<List<ModrinthVersion>> getVersionsByHashes(
            List<String> hashes, String algorithm) {
        // POST /version_files
    }
    
    // 分类
    public CompletableFuture<List<ModrinthCategory>> getCategories() {
        // GET /tag/category
    }
    
    // 用户
    public CompletableFuture<ModrinthUser> getUser(String usernameOrId) {
        // GET /user/{id|username}
    }
}

// 模型类
public class ModrinthProject {
    private String id;
    private String slug;
    private String name;
    private String description;
    private String iconUrl;
    private List<String> categories;
    private ProjectType projectType;
    private String status;
    private long downloads;
    private long followers;
    private List<String> gameVersions;
    private List<String> loaders;
    private Instant dateCreated;
    private Instant dateModified;
}

public class ModrinthVersion {
    private String id;
    private String projectId;
    private String name;
    private String versionNumber;
    private String changelog;
    private VersionType versionType;
    private List<String> gameVersions;
    private List<String> loaders;
    private boolean featured;
    private Instant datePublished;
    private List<ModrinthFile> files;
    private List<Dependency> dependencies;
}

public class ModrinthFile {
    private String url;
    private String filename;
    private boolean primary;
    private long size;
    private String sha1;
    private String sha512;
}

public class ModrinthSearchOptions {
    private String query;
    private List<String> facets;
    private String index = "relevance";
    private int offset = 0;
    private int limit = 20;
    
    // Facets builder
    public ModrinthSearchOptions gameVersion(String version) {
        facets.add("versions:" + version);
        return this;
    }
    
    public ModrinthSearchOptions loader(String loader) {
        facets.add("categories:" + loader);
        return this;
    }
    
    public ModrinthSearchOptions category(String category) {
        facets.add("categories:" + category);
        return this;
    }
}

public class ModrinthSearchResult {
    private List<ModrinthProject> hits;
    private int offset;
    private int limit;
    private int totalHits;
}
```

### 4.3 CurseForgeClient

```java
public class CurseForgeClient extends ApiClient {
    private static final String BASE_URL = "https://api.curseforge.com/v1";
    private final String apiKey;  // CurseForge需要API Key
    
    // 搜索
    public CompletableFuture<CurseForgeSearchResult> searchMods(
            CurseForgeSearchOptions options) {
        // GET /mods/search
        // 参数: gameId, classId, searchFilter, sortField, sortOrder, etc.
    }
    
    // Mod
    public CompletableFuture<CurseForgeMod> getMod(int modId) {
        // GET /mods/{modId}
    }
    
    public CompletableFuture<List<CurseForgeMod>> getMods(List<Integer> modIds) {
        // POST /mods
    }
    
    // 文件
    public CompletableFuture<List<CurseForgeFile>> getFiles(int modId) {
        // GET /mods/{modId}/files
    }
    
    public CompletableFuture<CurseForgeFile> getFile(int modId, int fileId) {
        // GET /mods/{modId}/files/{fileId}
    }
    
    // 指纹搜索
    public CompletableFuture<List<CurseForgeFile>> getFilesByFingerprints(
            List<Long> fingerprints) {
        // POST /fingerprints
    }
    
    // 分类
    public CompletableFuture<List<CurseForgeCategory>> getCategories(int gameId) {
        // GET /categories?gameId={gameId}
    }
}

public class CurseForgeMod {
    private int id;
    private String name;
    private String slug;
    private String summary;
    private String websiteUrl;
    private int gameId;
    private int classId;
    private String logoUrl;
    private List<CurseForgeCategory> categories;
    private long downloadCount;
    private int popularityScore;
    private List<CurseForgeFile> latestFiles;
    private CurseForgeFile latestFile;
}

public class CurseForgeFile {
    private int id;
    private int modId;
    private String displayName;
    private String fileName;
    private String downloadUrl;
    private long fileLength;
    private String fileDate;
    private List<String> gameVersions;
    private List<CurseForgeDependency> dependencies;
    private boolean isServerPack;
    private long fileFingerprint;
}

public class CurseForgeSearchOptions {
    private int gameId = 432;  // Minecraft gameId
    private Integer classId = 6;  // Mods classId
    private String searchFilter;
    private SortField sortField = SortField.POPULARITY;
    private SortOrder sortOrder = SortOrder.DESC;
    private int index = 0;
    private int pageSize = 20;
    private String gameVersion;
    private int modLoaderType;  // 0=any, 1=forge, 4=fabric
    
    public enum SortField {
        POPULARITY, NAME, DATE_CREATED, DATE_RELEASED, DOWNLOADS
    }
    
    public enum SortOrder {
        ASC, DESC
    }
}
```

### 4.4 MojangClient

```java
public class MojangClient extends ApiClient {
    private static final String API_URL = "https://api.mojang.com";
    private static final String SESSION_URL = "https://sessionserver.mojang.com";
    private static final String LAUNCHER_URL = "https://piston-meta.mojang.com";
    
    // 版本清单
    public CompletableFuture<VersionManifest> getVersionManifest() {
        // GET /mc/game/version_manifest_v2.json
    }
    
    public CompletableFuture<VersionInfo> getVersionInfo(String versionId) {
        // 从清单中获取URL，然后下载JSON
    }
    
    // 用户
    public CompletableFuture<MojangProfile> getProfile(String uuid) {
        // GET /user/profile/{uuid}
    }
    
    public CompletableFuture<MojangProfile> getProfileByUsername(String username) {
        // GET /users/profiles/minecraft/{username}
    }
    
    public CompletableFuture<List<MojangProfile>> getProfiles(List<String> usernames) {
        // POST /profiles/minecraft
    }
    
    // 皮肤
    public CompletableFuture<byte[]> getSkin(String uuid) {
        // GET /sessionserver/session/minecraft/profile/{uuid}
    }
    
    // 服务状态
    public CompletableFuture<List<MojangStatus>> getStatus() {
        // GET /status
    }
    
    // 黑名单
    public CompletableFuture<List<String>> getBlockedServers() {
        // GET /blockedservers
    }
}

public class VersionManifest {
    private LatestVersion latest;
    private List<VersionInfo> versions;
    
    public static class LatestVersion {
        private String release;
        private String snapshot;
    }
}

public class MojangProfile {
    private String id;  // UUID
    private String name;
    private List<Property> properties;
    
    public static class Property {
        private String name;
        private String value;
        private String signature;
    }
}

public class MojangStatus {
    private String service;
    private Status status;
    
    public enum Status {
        GREEN, YELLOW, RED
    }
}
```

### 4.5 UnifiedSearch (统一搜索)

```java
public class UnifiedSearch {
    private final ModrinthClient modrinth;
    private final CurseForgeClient curseforge;
    
    public CompletableFuture<List<SearchResult>> search(String query, 
                                                        SearchOptions options) {
        // 并行搜索两个平台，合并结果
        CompletableFuture<List<SearchResult>> mr = searchModrinth(query, options);
        CompletableFuture<List<SearchResult>> cf = searchCurseForge(query, options);
        
        return CompletableFuture.allOf(mr, cf)
            .thenApply(v -> mergeResults(mr.join(), cf.join()));
    }
    
    public CompletableFuture<UnifiedMod> getMod(String id, String source) {
        return switch (source.toLowerCase()) {
            case "modrinth" -> modrinth.getProject(id)
                .thenApply(this::toUnifiedMod);
            case "curseforge" -> curseforge.getMod(Integer.parseInt(id))
                .thenApply(this::toUnifiedMod);
            default -> throw new IllegalArgumentException("Unknown source: " + source);
        };
    }
    
    public CompletableFuture<List<UnifiedVersion>> getVersions(String id, String source) {
        // 获取版本列表
    }
    
    public CompletableFuture<UnifiedVersion> findByHash(String sha1) {
        // 通过SHA1查找模组
        // 先查Modrinth，再查CurseForge指纹
    }
    
    private List<SearchResult> mergeResults(List<SearchResult> mr, List<SearchResult> cf) {
        // 合并去重
        // 按相关度排序
    }
}

public class UnifiedMod {
    private String id;
    private String source;  // "modrinth" or "curseforge"
    private String name;
    private String slug;
    private String description;
    private String iconUrl;
    private String author;
    private long downloads;
    private List<String> categories;
    private List<String> gameVersions;
    private List<String> loaders;
    private String pageUrl;
}

public class UnifiedVersion {
    private String id;
    private String source;
    private String versionNumber;
    private String name;
    private String changelog;
    private VersionType type;
    private List<String> gameVersions;
    private List<String> loaders;
    private Instant datePublished;
    private List<DownloadFile> files;
    
    public static class DownloadFile {
        private String url;
        private String filename;
        private long size;
        private String sha1;
        private boolean primary;
    }
}

public class SearchOptions {
    private String query;
    private String gameVersion;
    private String loader;
    private String category;
    private String sortBy = "relevance";
    private int limit = 20;
    private int offset = 0;
}
```

### 4.6 ApiCache

```java
public class ApiCache {
    private final Path cacheDir;
    private final Duration defaultTtl;
    private final Cache<String, CacheEntry> memoryCache;
    
    public <T> Optional<T> get(String key, Type type) {
        // 1. 先查内存缓存
        // 2. 再查磁盘缓存
        // 3. 检查是否过期
    }
    
    public <T> void put(String key, T value, Duration ttl) {
        // 保存到内存和磁盘
    }
    
    public void invalidate(String key) {
        // 使缓存失效
    }
    
    public void clear() {
        // 清空所有缓存
    }
    
    public void cleanup() {
        // 清理过期缓存
    }
    
    private String generateKey(String endpoint, Map<String, String> params) {
        return Hashing.sha256().hashString(endpoint + params, StandardCharsets.UTF_8).toString();
    }
}

public class CacheEntry {
    private String key;
    private String data;
    private Instant created;
    private Instant expires;
    private Type type;
}
```

## 5. 关键流程

### 5.1 统一搜索流程

```
用户输入搜索词
    ↓
构建搜索参数
    ↓
并行请求:
├── Modrinth API
└── CurseForge API
    ↓
解析响应
    ↓
转换为统一模型
    ↓
合并去重
    ↓
排序返回
```

### 5.2 哈希查找流程

```
获取文件SHA1
    ↓
查询Modrinth:
GET /version_file/{sha1}?algorithm=sha1
    ↓
找到?
├── 是 → 返回结果
└── 否 → 查询CurseForge:
         POST /fingerprints
         ↓
         返回结果
```

### 5.3 缓存流程

```
收到API请求
    ↓
生成缓存键
    ↓
检查缓存:
├── 内存缓存命中 → 返回
├── 磁盘缓存命中 → 检查过期
│   ├── 未过期 → 返回
│   └── 已过期 → 删除
└── 缓存未命中
    ↓
发送API请求
    ↓
解析响应
    ↓
存入缓存
    ↓
返回结果
```

## 6. 配置文件

```json
{
  "api": {
    "modrinth": {
      "baseUrl": "https://api.modrinth.com/v2",
      "cacheTtl": 3600000,
      "rateLimit": {
        "requests": 300,
        "period": 60000
      }
    },
    "curseforge": {
      "baseUrl": "https://api.curseforge.com/v1",
      "apiKey": "${CURSEFORGE_API_KEY}",
      "cacheTtl": 3600000,
      "rateLimit": {
        "requests": 100,
        "period": 60000
      }
    },
    "mojang": {
      "apiUrl": "https://api.mojang.com",
      "sessionUrl": "https://sessionserver.mojang.com",
      "cacheTtl": 86400000
    },
    "cacheDir": "./cache/api"
  }
}
```

## 7. 测试要点

- API请求正确性
- 速率限制
- 缓存命中/失效
- 错误处理
- 统一搜索合并
- 哈希查找
- 代理支持