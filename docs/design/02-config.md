# 模块设计：config（配置管理模块）

## 概述

配置管理模块负责启动器及游戏实例的配置文件解析、编辑、对比和管理。支持多种配置格式，提供配置模板和批量编辑功能。

## 子包结构

```
org.aurora.launcher.config/
├── parser/           # 配置解析器
├── editor/           # 配置编辑器
├── permission/       # 权限管理
├── compare/          # 配置对比
├── template/         # 配置模板
└── batch/            # 批量编辑
```

## 详细设计

### 1. parser（配置解析器）

**ConfigParser接口**
```java
public interface ConfigParser {
    Map<String, Object> parse(InputStream input) throws ConfigParseException;
    void write(OutputStream output, Map<String, Object> config) throws ConfigParseException;
    String[] getSupportedExtensions();
}
```

**PropertiesParser**
```java
public class PropertiesParser implements ConfigParser {
    @Override
    public Map<String, Object> parse(InputStream input);
    
    @Override
    public void write(OutputStream output, Map<String, Object> config);
    
    public Properties parseProperties(InputStream input);
    public void writeProperties(OutputStream output, Properties props);
}
```

**JsonParser**
```java
public class JsonParser implements ConfigParser {
    @Override
    public Map<String, Object> parse(InputStream input);
    
    @Override
    public void write(OutputStream output, Map<String, Object> config);
    
    public JsonObject parseObject(InputStream input);
    public JsonArray parseArray(InputStream input);
}
```

**TomlParser**
```java
public class TomlParser implements ConfigParser {
    @Override
    public Map<String, Object> parse(InputStream input);
    
    @Override
    public void write(OutputStream output, Map<String, Object> config);
}
```

**YamlParser**
```java
public class YamlParser implements ConfigParser {
    @Override
    public Map<String, Object> parse(InputStream input);
    
    @Override
    public void write(OutputStream output, Map<String, Object> config);
}
```

**ConfigParserFactory**
```java
public class ConfigParserFactory {
    private static Map<String, ConfigParser> parsers;
    
    public static void registerParser(ConfigParser parser);
    public static ConfigParser getParser(String extension);
    public static ConfigParser getParserByFile(Path file);
    public static boolean isSupported(String extension);
}
```

### 2. editor（配置编辑器）

**ConfigEditor**
```java
public class ConfigEditor {
    private Path configPath;
    private ConfigParser parser;
    private Map<String, ConfigEntry> entries;
    private boolean modified;
    
    public static ConfigEditor load(Path configPath);
    
    public void set(String key, Object value);
    public void set(String key, Object value, String comment);
    public Object get(String key);
    public <T> T get(String key, Class<T> type);
    public <T> T get(String key, Class<T> type, T defaultValue);
    public void remove(String key);
    public boolean has(String key);
    public void addComment(String key, String comment);
    public void addSection(String section);
    
    public void save();
    public void saveAs(Path target);
    public void reload();
    public boolean isModified();
    
    public Map<String, ConfigEntry> getEntries();
    public List<String> getKeys();
}

public class ConfigEntry {
    private String key;
    private Object value;
    private String comment;
    private String section;
    
    // getters and setters
}
```

**ServerConfigEditor（服务端配置编辑器）**
```java
public class ServerConfigEditor extends ConfigEditor {
    
    // server.properties 常用配置
    public void setServerPort(int port);
    public int getServerPort();
    public void setMaxPlayers(int maxPlayers);
    public int getMaxPlayers();
    public void setDifficulty(String difficulty);
    public String getDifficulty();
    public void setMotd(String motd);
    public String getMotd();
    public void setOnlineMode(boolean onlineMode);
    public boolean isOnlineMode();
    public void setViewDistance(int distance);
    public int getViewDistance();
    public void setSpawnProtection(int range);
    public int getSpawnProtection();
    public void setAllowNether(boolean allow);
    public boolean isAllowNether();
    public void setEnableCommandBlock(boolean enable);
    public boolean isEnableCommandBlock();
    
    // bukkit.yml / spigot.yml
    public void setBukkitSetting(String path, Object value);
    public Object getBukkitSetting(String path);
    
    // paper.yml
    public void setPaperSetting(String path, Object value);
    public Object getPaperSetting(String path);
    
    // forge config
    public void setForgeConfig(String modId, String path, Object value);
    public Object getForgeConfig(String modId, String path);
    
    // fabric config
    public void setFabricConfig(String modId, String path, Object value);
    public Object getFabricConfig(String modId, String path);
}
```

**ConfigEditorUI数据模型**
```java
public class ConfigNode {
    private String key;
    private Object value;
    private String type;
    private String comment;
    private List<ConfigNode> children;
    private boolean expanded;
    
    public boolean isSection();
    public boolean isLeaf();
}

public class ConfigDiffNode {
    private String key;
    private Object oldValue;
    private Object newValue;
    private DiffType diffType;
    
    public enum DiffType {
        ADDED, REMOVED, MODIFIED, UNCHANGED
    }
}
```

### 3. permission（权限管理）

**PermissionManager**
```java
public class PermissionManager {
    private Map<String, PermissionGroup> groups;
    private Map<String, PermissionUser> users;
    
    public void loadFromDirectory(Path dir);
    public void saveToDirectory(Path dir);
    
    public PermissionGroup createGroup(String name);
    public void deleteGroup(String name);
    public PermissionGroup getGroup(String name);
    public List<PermissionGroup> getGroups();
    
    public PermissionUser createUser(String name, String uuid);
    public void deleteUser(String name);
    public PermissionUser getUser(String name);
    public PermissionUser getUserByUuid(String uuid);
    public List<PermissionUser> getUsers();
    
    public void addUserToGroup(String userName, String groupName);
    public void removeUserFromGroup(String userName, String groupName);
    public boolean hasPermission(String userName, String permission);
    public boolean hasPermission(String userName, String permission, String world);
}

public class PermissionGroup {
    private String name;
    private String prefix;
    private String suffix;
    private List<String> permissions;
    private List<String> inheritance;
    private int priority;
    
    public boolean hasPermission(String permission);
    public void addPermission(String permission);
    public void removePermission(String permission);
}

public class PermissionUser {
    private String name;
    private String uuid;
    private List<String> groups;
    private List<String> permissions;
    private Map<String, List<String>> worldPermissions;
    
    public boolean hasPermission(String permission);
    public boolean hasPermission(String permission, String world);
}
```

**权限配置格式支持**

LuckPerms格式：
```json
{
  "username": "Player1",
  "uuid": "...",
  "primaryGroup": "admin",
  "permissions": [
    "minecraft.command.gamemode",
    "essentials.fly"
  ],
  "groups": ["admin"]
}
```

PermissionsEx格式：
```yaml
users:
  Player1:
    group:
    - admin
    permissions: []
groups:
  admin:
    permissions:
    - '*'
    options:
      prefix: '&c[Admin]&f '
```

### 4. compare（配置对比）

**ConfigComparator**
```java
public class ConfigComparator {
    
    public ConfigComparison compare(Path config1, Path config2);
    public ConfigComparison compare(Map<String, Object> config1, Map<String, Object> config2);
    public ConfigComparison compare(Path config, Map<String, Object> expected);
    
    public List<ConfigDiff> getDifferences(ConfigComparison comparison);
    public List<ConfigDiff> getAddedEntries(ConfigComparison comparison);
    public List<ConfigDiff> getRemovedEntries(ConfigComparison comparison);
    public List<ConfigDiff> getModifiedEntries(ConfigComparison comparison);
    
    public void exportDiff(ConfigComparison comparison, Path output);
    public void applyPatch(Path config, Path patch);
}

public class ConfigComparison {
    private Map<String, Object> config1;
    private Map<String, Object> config2;
    private List<ConfigDiff> diffs;
    private int addedCount;
    private int removedCount;
    private int modifiedCount;
    
    public boolean hasDifferences();
    public int getTotalDifferences();
}

public class ConfigDiff {
    private String key;
    private Object oldValue;
    private Object newValue;
    private DiffType type;
    private String oldComment;
    private String newComment;
    
    public enum DiffType {
        ADDED, REMOVED, MODIFIED
    }
}
```

**对比报告生成**
```java
public class ConfigDiffReporter {
    
    public String generateTextReport(ConfigComparison comparison);
    public String generateHtmlReport(ConfigComparison comparison);
    public String generateMarkdownReport(ConfigComparison comparison);
    
    public void setIncludeComments(boolean include);
    public void setIncludeSections(boolean include);
    public void setFilter(DiffType... types);
}
```

### 5. template（配置模板）

**ConfigTemplate**
```java
public class ConfigTemplate {
    private String id;
    private String name;
    private String description;
    private String gameVersion;
    private String loaderType;
    private String loaderVersion;
    private Map<String, Object> defaults;
    private List<TemplateRule> rules;
    
    public void apply(Path configPath);
    public void apply(ConfigEditor editor);
    public void applyWithOverrides(Path configPath, Map<String, Object> overrides);
}

public class TemplateRule {
    private String key;
    private RuleCondition condition;
    private Object value;
    private String comment;
    
    public enum RuleCondition {
        ALWAYS, IF_MISSING, IF_EMPTY, CUSTOM
    }
}
```

**ConfigTemplateManager**
```java
public class ConfigTemplateManager {
    private Path templateDir;
    private Map<String, ConfigTemplate> templates;
    
    public static ConfigTemplateManager getInstance();
    
    public void loadTemplates();
    public ConfigTemplate getTemplate(String id);
    public List<ConfigTemplate> getTemplates();
    public List<ConfigTemplate> getTemplatesForVersion(String gameVersion);
    public List<ConfigTemplate> getTemplatesForLoader(String loaderType);
    
    public void saveTemplate(ConfigTemplate template);
    public void deleteTemplate(String id);
    public void importTemplate(Path file);
    public void exportTemplate(String id, Path target);
}
```

**内置模板**

```
templates/
├── vanilla-server.json         # 原版服务端配置
├── paper-server.json           # Paper服务端优化配置
├── fabric-server.json          # Fabric服务端配置
├── forge-server.json           # Forge服务端配置
├── permissions-basic.json      # 基础权限配置
├── permissions-donator.json    # 捐赠者权限配置
└── performance-optimized.json  # 性能优化配置
```

### 6. batch（批量编辑）

**BatchEditor**
```java
public class BatchEditor {
    private List<BatchOperation> operations;
    
    public void addOperation(BatchOperation operation);
    public void addSetOperation(String key, Object value);
    public void addRemoveOperation(String key);
    public void addRenameOperation(String oldKey, String newKey);
    public void addCommentOperation(String key, String comment);
    
    public BatchResult execute(Path configPath);
    public BatchResult execute(List<Path> configPaths);
    public BatchResult execute(ConfigEditor editor);
    
    public void preview(Path configPath);
    public void saveScript(Path output);
    public void loadScript(Path input);
}

public abstract class BatchOperation {
    protected String key;
    
    public abstract void apply(ConfigEditor editor);
    public abstract String getDescription();
}

public class SetOperation extends BatchOperation {
    private Object value;
    private String comment;
}

public class RemoveOperation extends BatchOperation {
}

public class RenameOperation extends BatchOperation {
    private String newKey;
}

public class BatchResult {
    private int successCount;
    private int failCount;
    private List<BatchError> errors;
    private List<Path> processedFiles;
    
    public boolean isAllSuccess();
    public List<BatchError> getErrors();
}

public class BatchError {
    private Path file;
    private String operation;
    private String message;
}
```

**批量编辑脚本格式**
```json
{
  "name": "Batch Script",
  "description": "Apply common server optimizations",
  "operations": [
    {
      "type": "set",
      "key": "server.port",
      "value": 25565
    },
    {
      "type": "set",
      "key": "max-players",
      "value": 20
    },
    {
      "type": "remove",
      "key": "deprecated-option"
    },
    {
      "type": "rename",
      "oldKey": "old-name",
      "newKey": "new-name"
    }
  ]
}
```

## 依赖关系

本模块依赖：
- core（配置管理基础、文件操作、日志）
- 第三方库：Gson、TOML解析库、YAML解析库

## 使用示例

```java
// 加载并编辑配置
ConfigEditor editor = ConfigEditor.load(Paths.get("server.properties"));
editor.set("server-port", 25566, "Server listen port");
editor.set("max-players", 50);
editor.save();

// 服务端配置编辑
ServerConfigEditor serverConfig = new ServerConfigEditor(instancePath);
serverConfig.setMotd("Welcome to my server!");
serverConfig.setMaxPlayers(100);
serverConfig.save();

// 配置对比
ConfigComparator comparator = new ConfigComparator();
ConfigComparison comparison = comparator.compare(config1, config2);
List<ConfigDiff> diffs = comparator.getDifferences(comparison);
String report = new ConfigDiffReporter().generateMarkdownReport(comparison);

// 应用模板
ConfigTemplate template = ConfigTemplateManager.getInstance().getTemplate("paper-server");
template.apply(serverConfigPath);

// 批量编辑
BatchEditor batch = new BatchEditor();
batch.addSetOperation("difficulty", "hard");
batch.addSetOperation("spawn-monsters", true);
BatchResult result = batch.execute(configFiles);
```