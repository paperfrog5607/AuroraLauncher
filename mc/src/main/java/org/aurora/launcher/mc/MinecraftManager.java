package org.aurora.launcher.mc;

import org.aurora.launcher.mod.manager.ModManager;
import org.aurora.launcher.modpack.instance.InstanceManager;
import org.aurora.launcher.modpack.import_.ImportManager;
import org.aurora.launcher.modpack.export.ExportManager;
import org.aurora.launcher.modpack.backup.BackupManager;
import org.aurora.launcher.modpack.share.ShareManager;
import org.aurora.launcher.modpack.verify.VerifyManager;
import org.aurora.launcher.mod.version.ModVersionManager;
import org.aurora.launcher.mod.dependency.DependencyManager;
import org.aurora.launcher.mod.scanner.ModScanner;
import org.aurora.launcher.mod.search.ModSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Minecraft管理器
 * 整合modpack和mod模块，提供统一接口
 */
public class MinecraftManager {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftManager.class);
    private static MinecraftManager instance;

    private final InstanceManager instanceManager;
    private final ModManager modManager;
    private final ImportManager importManager;
    private final ExportManager exportManager;
    private final BackupManager backupManager;
    private final ShareManager shareManager;
    private final VerifyManager verifyManager;
    private final ModVersionManager versionManager;
    private final DependencyManager dependencyManager;
    private final ModScanner modScanner;
    private final ModSearcher modSearcher;

    private final List<MinecraftInstance> instances;
    private boolean isInitialized;

    private MinecraftManager() {
        this.instanceManager = new InstanceManager();
        this.modManager = new ModManager();
        this.importManager = new ImportManager();
        this.exportManager = new ExportManager();
        this.backupManager = new BackupManager();
        this.shareManager = new ShareManager();
        this.verifyManager = new VerifyManager();
        this.versionManager = new ModVersionManager();
        this.dependencyManager = new DependencyManager();
        this.modScanner = new ModScanner();
        this.modSearcher = new ModSearcher();
        this.instances = new CopyOnWriteArrayList<>();
        this.isInitialized = false;
    }

    public static synchronized MinecraftManager getInstance() {
        if (instance == null) {
            instance = new MinecraftManager();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void initialize() {
        if (isInitialized) {
            return;
        }

        logger.info("Initializing Minecraft Manager...");
        
        instanceManager.loadInstances();
        instances.clear();
        instances.addAll(instanceManager.getInstances());
        
        isInitialized = true;
        logger.info("Minecraft Manager initialized with {} instances", instances.size());
    }

    /**
     * 获取所有实例
     */
    public List<MinecraftInstance> getInstances() {
        return new ArrayList<>(instances);
    }

    /**
     * 根据ID获取实例
     */
    public MinecraftInstance getInstance(String id) {
        for (MinecraftInstance instance : instances) {
            if (instance.getId().equals(id)) {
                return instance;
            }
        }
        return null;
    }

    /**
     * 创建新实例
     */
    public MinecraftInstance createInstance(String name, String version) {
        MinecraftInstance instance = instanceManager.createInstance(name, version);
        if (instance != null) {
            instances.add(instance);
        }
        return instance;
    }

    /**
     * 删除实例
     */
    public boolean deleteInstance(String id) {
        MinecraftInstance instance = getInstance(id);
        if (instance == null) {
            return false;
        }
        
        if (instanceManager.deleteInstance(id)) {
            instances.remove(instance);
            return true;
        }
        return false;
    }

    /**
     * 导入整合包
     */
    public boolean importModpack(File file) {
        return importManager.importModpack(file) != null;
    }

    /**
     * 导出整合包
     */
    public boolean exportModpack(String instanceId, File target) {
        return exportManager.exportModpack(instanceId, target);
    }

    /**
     * 创建备份
     */
    public boolean createBackup(String instanceId) {
        return backupManager.createBackup(instanceId);
    }

    /**
     * 恢复备份
     */
    public boolean restoreBackup(String instanceId, String backupId) {
        return backupManager.restoreBackup(instanceId, backupId);
    }

    /**
     * 获取备份列表
     */
    public List<BackupInfo> getBackups(String instanceId) {
        return backupManager.getBackups(instanceId);
    }

    /**
     * 验证整合包
     */
    public VerifyResult verifyInstance(String instanceId) {
        return verifyManager.verify(instanceId);
    }

    /**
     * 修复损坏的整合包
     */
    public boolean repairInstance(String instanceId) {
        return verifyManager.repair(instanceId);
    }

    /**
     * 生成分享码
     */
    public String generateShareCode(String instanceId) {
        return shareManager.createShare(instanceId);
    }

    /**
     * 通过分享码导入
     */
    public MinecraftInstance importFromShareCode(String code) {
        MinecraftInstance instance = shareManager.importFromShare(code);
        if (instance != null) {
            instances.add(instance);
        }
        return instance;
    }

    /**
     * 扫描目录获取mod列表
     */
    public List<ModInfo> scanMods(File directory) {
        return modScanner.scan(directory);
    }

    /**
     * 搜索mod
     */
    public List<ModSearchResult> searchMods(String query, String source) {
        return modSearcher.search(query, source);
    }

    /**
     * 获取mod信息
     */
    public ModInfo getModInfo(String modId, String source) {
        return modSearcher.getModInfo(modId, source);
    }

    /**
     * 下载mod
     */
    public File downloadMod(String modId, String source, String version) {
        return modSearcher.downloadMod(modId, source, version);
    }

    /**
     * 检查mod依赖
     */
    public List<String> checkDependencies(ModInfo mod) {
        return dependencyManager.checkDependencies(mod);
    }

    /**
     * 解决依赖冲突
     */
    public List<ModInfo> resolveConflicts(List<ModInfo> mods) {
        return dependencyManager.resolveConflicts(mods);
    }

    /**
     * 获取实例管理器
     */
    public InstanceManager getInstanceManager() {
        return instanceManager;
    }

    /**
     * 获取mod管理器
     */
    public ModManager getModManager() {
        return modManager;
    }

    /**
     * 强制刷新
     */
    public void forceRefresh() {
        isInitialized = false;
        initialize();
    }

    /**
     * Minecraft实例简单模型
     */
    public static class MinecraftInstance {
        private String id;
        private String name;
        private String version;
        private String minecraftVersion;
        private String modloader;
        private File path;
        private long lastPlayed;
        private int modCount;
        private boolean isValid;

        public MinecraftInstance() {
        }

        public MinecraftInstance(String id, String name, String version) {
            this.id = id;
            this.name = name;
            this.version = version;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getMinecraftVersion() { return minecraftVersion; }
        public void setMinecraftVersion(String mcVersion) { this.minecraftVersion = mcVersion; }
        public String getModloader() { return modloader; }
        public void setModloader(String modloader) { this.modloader = modloader; }
        public File getPath() { return path; }
        public void setPath(File path) { this.path = path; }
        public long getLastPlayed() { return lastPlayed; }
        public void setLastPlayed(long lastPlayed) { this.lastPlayed = lastPlayed; }
        public int getModCount() { return modCount; }
        public void setModCount(int modCount) { this.modCount = modCount; }
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }

        public String getDisplayName() {
            return name + " " + version;
        }
    }

    /**
     * Mod信息
     */
    public static class ModInfo {
        private String id;
        private String name;
        private String version;
        private String source;
        private File file;
        private List<String> dependencies;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public File getFile() { return file; }
        public void setFile(File file) { this.file = file; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    }

    /**
     * Mod搜索结果
     */
    public static class ModSearchResult {
        private String id;
        private String name;
        private String description;
        private String author;
        private String version;
        private String source;
        private String downloadUrl;
        private String thumbnailUrl;
        private long downloads;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public long getDownloads() { return downloads; }
        public void setDownloads(long downloads) { this.downloads = downloads; }
    }

    /**
     * 备份信息
     */
    public static class BackupInfo {
        private String id;
        private String instanceId;
        private String name;
        private long timestamp;
        private long size;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getInstanceId() { return instanceId; }
        public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
    }

    /**
     * 验证结果
     */
    public static class VerifyResult {
        private boolean isValid;
        private List<String> missingFiles;
        private List<String> corruptedFiles;
        private List<String> suggestions;

        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }
        public List<String> getMissingFiles() { return missingFiles; }
        public void setMissingFiles(List<String> missingFiles) { this.missingFiles = missingFiles; }
        public List<String> getCorruptedFiles() { return corruptedFiles; }
        public void setCorruptedFiles(List<String> corruptedFiles) { this.corruptedFiles = corruptedFiles; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    }
}
