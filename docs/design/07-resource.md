# 模块七：resource - 资源管理模块

## 1. 模块概述

管理资源包、光影包、数据包、语言文件、纹理预览。

## 2. 依赖关系

```
resource
├── core
└── download
```

## 3. 子包结构

```
com.aurora.resource/
├── resourcepack/
│   ├── ResourcePack.java             # 资源包模型
│   ├── ResourcePackManager.java      # 资源包管理器
│   ├── ResourcePackScanner.java      # 资源包扫描器
│   ├── ResourcePackParser.java       # 资源包解析器
│   └── PackMeta.java                 # pack.mcmeta模型
├── shaderpack/
│   ├── ShaderPack.java               # 光影包模型
│   ├── ShaderPackManager.java        # 光影包管理器
│   ├── ShaderPackScanner.java       # 光影包扫描器
│   └── ShaderConfig.java            # 光影配置
├── datapack/
│   ├── DataPack.java                 # 数据包模型
│   ├── DataPackManager.java          # 数据包管理器
│   ├── DataPackScanner.java          # 数据包扫描器
│   └── DataPackParser.java           # 数据包解析器
├── language/
│   ├── LanguageFile.java             # 语言文件模型
│   ├── LanguageFileManager.java      # 语言文件管理器
│   ├── LanguageEditor.java           # 语言文件编辑器
│   └── LanguageDiff.java            # 语言文件对比
└── texture/
    ├── TexturePreview.java           # 纹理预览器
    ├── TextureExtractor.java         # 纹理提取器
    └── TextureCache.java            # 纹理缓存
```

## 4. 核心类设计

### 4.1 ResourcePack

```java
public class ResourcePack {
    private String id;
    private String name;
    private String description;
    private int packFormat;
    private Path filePath;
    private ResourcePackType type;
    private long fileSize;
    private String iconHash;
    private Image icon;
    private List<String> supportedVersions;
    
    public enum ResourcePackType {
        ZIP, FOLDER, SERVER
    }
}

public class ResourcePackManager {
    private final Path resourcePacksDir;
    private final ResourcePackScanner scanner;
    
    public CompletableFuture<List<ResourcePack>> scan();
    public CompletableFuture<Void> enable(String packId);
    public CompletableFuture<Void> disable(String packId);
    public CompletableFuture<Void> delete(String packId);
    
    public CompletableFuture<Void> moveUp(String packId);
    public CompletableFuture<Void> moveDown(String packId);
    
    public CompletableFuture<Void> importPack(Path source);
    public CompletableFuture<Path> exportPack(String packId, Path target);
    
    public List<ResourcePack> getEnabledPacks();
    public List<ResourcePack> getAvailablePacks();
    
    public void savePackOrder(List<String> packIds);
}

public class ResourcePackScanner {
    public CompletableFuture<List<ResourcePack>> scan(Path dir) {
        // 1. 扫描目录下所有zip文件
        // 2. 扫描子目录(文件夹资源包)
        // 3. 解析pack.mcmeta
        // 4. 加载图标
    }
}

public class PackMeta {
    private int packFormat;
    private String description;
    private Map<String, Object> raw;
    
    public static PackMeta parse(Path packPath) {
        // 解析pack.mcmeta
    }
}
```

### 4.2 ShaderPack

```java
public class ShaderPack {
    private String id;
    private String name;
    private String description;
    private Path filePath;
    private ShaderPackType type;
    private long fileSize;
    private Image icon;
    private List<String> supportedRenderers;
    
    public enum ShaderPackType {
        OPTIFINE, IRIS, VANILLA
    }
}

public class ShaderPackManager {
    private final Path shaderPacksDir;
    
    public CompletableFuture<List<ShaderPack>> scan();
    public CompletableFuture<Void> enable(String packId);
    public CompletableFuture<Void> disable();
    public CompletableFuture<Void> delete(String packId);
    
    public CompletableFuture<Void> importPack(Path source);
    
    public Optional<ShaderPack> getCurrentPack();
    public boolean isShadersEnabled();
    
    public CompletableFuture<Void> openShaderSettings(String packId);
}

public class ShaderConfig {
    // 光影配置项解析和管理
    // 支持OptiFine和Iris的配置格式
}
```

### 4.3 DataPack

```java
public class DataPack {
    private String id;
    private String name;
    private String description;
    private int packFormat;
    private Path filePath;
    private DataPackType type;
    private List<String> namespaces;
    private long fileSize;
    
    public enum DataPackType {
        ZIP, FOLDER, WORLD
    }
}

public class DataPackManager {
    private final Path worldDataPacksDir;
    
    public CompletableFuture<List<DataPack>> scanWorld(String worldName);
    public CompletableFuture<List<DataPack>> scanGlobal();
    
    public CompletableFuture<Void> enable(String packId, String worldName);
    public CompletableFuture<Void> disable(String packId, String worldName);
    public CompletableFuture<Void> delete(String packId);
    
    public CompletableFuture<Void> importPack(Path source, String worldName);
    
    public List<DataPack> getEnabledPacks(String worldName);
}

public class DataPackParser {
    public DataPack parse(Path packPath) {
        // 1. 解析pack.mcmeta
        // 2. 扫描命名空间
        // 3. 提取描述
    }
}
```

### 4.4 LanguageFileManager

```java
public class LanguageFile {
    private String languageCode;  // zh_cn, en_us
    private Path filePath;
    private Map<String, String> entries;
    private String modId;
}

public class LanguageFileManager {
    public CompletableFuture<List<LanguageFile>> scanModLanguages(Path modFile);
    public CompletableFuture<List<LanguageFile>> scanResourcePackLanguages(Path packPath);
    
    public CompletableFuture<LanguageFile> load(Path filePath);
    public CompletableFuture<Void> save(LanguageFile file);
    
    public CompletableFuture<LanguageFile> create(String modId, String langCode);
    
    public Map<String, LanguageDiff> compare(LanguageFile base, LanguageFile other);
}

public class LanguageEditor {
    private LanguageFile file;
    private List<LanguageChangeListener> listeners;
    
    public void setEntry(String key, String value);
    public void removeEntry(String key);
    
    public void addEntries(Map<String, String> entries);
    
    public CompletableFuture<Void> save();
    public void undo();
    public void redo();
    
    public List<String> search(String query);
    public List<String> getMissingKeys(LanguageFile reference);
}

public class LanguageDiff {
    private String key;
    private String baseValue;
    private String otherValue;
    private DiffType type;
    
    public enum DiffType {
        ADDED, REMOVED, MODIFIED, UNCHANGED
    }
}
```

### 4.5 TexturePreview

```java
public class TexturePreview {
    private Image texture;
    private String texturePath;  // assets/modid/textures/...
    private int width;
    private int height;
    private boolean animated;
    private List<Image> animationFrames;
}

public class TextureExtractor {
    public CompletableFuture<List<TexturePreview>> extractFromMod(Path modFile) {
        // 1. 打开jar文件
        // 2. 遍历textures目录
        // 3. 提取png文件
        // 4. 加载为Image
    }
    
    public CompletableFuture<List<TexturePreview>> extractFromResourcePack(Path packPath) {
        // 同上
    }
    
    public CompletableFuture<Image> extractSingle(Path filePath, String texturePath) {
        // 提取单个纹理
    }
}

public class TextureCache {
    private final Cache<String, Image> cache;
    
    public Image get(Path filePath, String texturePath) {
        String key = filePath + ":" + texturePath;
        return cache.get(key, k -> loadTexture(filePath, texturePath));
    }
    
    public void clear() {
        cache.clear();
    }
}
```

## 5. 关键流程

### 5.1 资源包管理流程

```
扫描资源包目录
    ↓
解析每个资源包:
├── 读取pack.mcmeta
├── 提取图标
└── 检测格式版本
    ↓
列出资源包列表
    ↓
用户操作:
├── 启用/禁用
├── 上移/下移
├── 导入/导出
└── 删除
    ↓
保存资源包顺序到options.txt
```

### 5.2 语言文件编辑流程

```
选择模组/资源包
    ↓
扫描语言文件
    ↓
加载zh_cn和en_us
    ↓
对比差异
    ↓
用户编辑:
├── 修改翻译
├── 添加新条目
└── 删除条目
    ↓
保存修改
```

### 5.3 纹理预览流程

```
选择模组/资源包
    ↓
扫描textures目录
    ↓
提取纹理文件
    ↓
加载为图片
    ↓
显示预览:
├── 缩略图列表
├── 详细预览
└── 动画播放
```

## 6. 配置文件

```json
{
  "resourceManagement": {
    "autoScanPacks": true,
    "cacheTextures": true,
    "maxTextureCacheSize": 100,
    "languageEditor": {
      "autoSave": true,
      "saveInterval": 30000,
      "showLineNumbers": true
    }
  }
}
```

## 7. 测试要点

- 资源包解析和加载
- 光影包启用/禁用
- 数据包世界级管理
- 语言文件编辑和保存
- 纹理提取和预览
- 缓存机制有效性