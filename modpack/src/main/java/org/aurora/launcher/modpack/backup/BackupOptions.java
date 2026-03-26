package org.aurora.launcher.modpack.backup;

public class BackupOptions {
    
    private Backup.BackupType type = Backup.BackupType.FULL;
    private boolean includeWorlds = true;
    private boolean includeConfigs = true;
    private boolean includeMods = true;
    private boolean includeLogs = false;
    private int maxBackups = 10;
    private String name;
    private String description;
    
    public BackupOptions() {
    }
    
    public Backup.BackupType getType() {
        return type;
    }
    
    public void setType(Backup.BackupType type) {
        this.type = type;
    }
    
    public boolean isIncludeWorlds() {
        return includeWorlds;
    }
    
    public void setIncludeWorlds(boolean includeWorlds) {
        this.includeWorlds = includeWorlds;
    }
    
    public boolean isIncludeConfigs() {
        return includeConfigs;
    }
    
    public void setIncludeConfigs(boolean includeConfigs) {
        this.includeConfigs = includeConfigs;
    }
    
    public boolean isIncludeMods() {
        return includeMods;
    }
    
    public void setIncludeMods(boolean includeMods) {
        this.includeMods = includeMods;
    }
    
    public boolean isIncludeLogs() {
        return includeLogs;
    }
    
    public void setIncludeLogs(boolean includeLogs) {
        this.includeLogs = includeLogs;
    }
    
    public int getMaxBackups() {
        return maxBackups;
    }
    
    public void setMaxBackups(int maxBackups) {
        this.maxBackups = maxBackups;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public static BackupOptions full() {
        BackupOptions options = new BackupOptions();
        options.setType(Backup.BackupType.FULL);
        options.setIncludeWorlds(true);
        options.setIncludeConfigs(true);
        options.setIncludeMods(true);
        return options;
    }
    
    public static BackupOptions configOnly() {
        BackupOptions options = new BackupOptions();
        options.setType(Backup.BackupType.CONFIG_ONLY);
        options.setIncludeWorlds(false);
        options.setIncludeConfigs(true);
        options.setIncludeMods(false);
        return options;
    }
    
    public static BackupOptions worldOnly() {
        BackupOptions options = new BackupOptions();
        options.setType(Backup.BackupType.WORLD_ONLY);
        options.setIncludeWorlds(true);
        options.setIncludeConfigs(false);
        options.setIncludeMods(false);
        return options;
    }
}