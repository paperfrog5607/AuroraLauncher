package org.aurora.launcher.modpack.instance;

import org.aurora.launcher.modpack.template.Template;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InstanceBuilder {
    
    private String id;
    private String name;
    private String version;
    private String minecraftVersion;
    private String loaderType;
    private String loaderVersion;
    private Path instanceDir;
    private Path iconPath;
    private List<String> tags = new ArrayList<>();
    private InstanceConfig.MemoryConfig memoryConfig;
    private InstanceConfig.JavaConfig javaConfig;
    private InstanceConfig.WindowConfig windowConfig;
    
    public InstanceBuilder() {
        this.id = UUID.randomUUID().toString();
        this.version = "1.0.0";
    }
    
    public InstanceBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    public InstanceBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public InstanceBuilder version(String version) {
        this.version = version;
        return this;
    }
    
    public InstanceBuilder minecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
        return this;
    }
    
    public InstanceBuilder loaderType(String loaderType) {
        this.loaderType = loaderType;
        return this;
    }
    
    public InstanceBuilder loaderVersion(String loaderVersion) {
        this.loaderVersion = loaderVersion;
        return this;
    }
    
    public InstanceBuilder instanceDir(Path instanceDir) {
        this.instanceDir = instanceDir;
        return this;
    }
    
    public InstanceBuilder iconPath(Path iconPath) {
        this.iconPath = iconPath;
        return this;
    }
    
    public InstanceBuilder addTag(String tag) {
        this.tags.add(tag);
        return this;
    }
    
    public InstanceBuilder tags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        return this;
    }
    
    public InstanceBuilder memoryConfig(InstanceConfig.MemoryConfig memoryConfig) {
        this.memoryConfig = memoryConfig;
        return this;
    }
    
    public InstanceBuilder javaConfig(InstanceConfig.JavaConfig javaConfig) {
        this.javaConfig = javaConfig;
        return this;
    }
    
    public InstanceBuilder windowConfig(InstanceConfig.WindowConfig windowConfig) {
        this.windowConfig = windowConfig;
        return this;
    }
    
    public InstanceBuilder fromCurseForge(Path cfZip) {
        return this;
    }
    
    public InstanceBuilder fromModrinth(Path mrZip) {
        return this;
    }
    
    public InstanceBuilder fromTemplate(Template template) {
        if (template != null) {
            this.name = template.getName();
            this.minecraftVersion = template.getMinecraftVersion();
            this.loaderType = template.getLoaderType();
            this.loaderVersion = template.getLoaderVersion();
            this.tags = new ArrayList<>(template.getTags());
        }
        return this;
    }
    
    public InstanceBuilder fromShareCode(String code) {
        return this;
    }
    
    public Instance build() {
        Instance instance = new Instance();
        instance.setId(id);
        instance.setName(name);
        instance.setVersion(version);
        instance.setInstanceDir(instanceDir);
        instance.setCreatedTime(Instant.now());
        instance.setTags(tags);
        
        if (iconPath != null) {
            instance.setIconPath(iconPath.toString());
        }
        
        InstanceConfig config = new InstanceConfig();
        config.setMinecraftVersion(minecraftVersion);
        config.setLoaderType(loaderType != null ? loaderType : "vanilla");
        config.setLoaderVersion(loaderVersion);
        
        if (memoryConfig != null) {
            config.setMemory(memoryConfig);
        }
        if (javaConfig != null) {
            config.setJava(javaConfig);
        }
        if (windowConfig != null) {
            config.setWindow(windowConfig);
        }
        
        instance.setConfig(config);
        
        ModLoaderInfo loaderInfo = new ModLoaderInfo();
        if (loaderType != null) {
            try {
                loaderInfo.setType(ModLoaderInfo.LoaderType.valueOf(loaderType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                loaderInfo.setType(ModLoaderInfo.LoaderType.VANILLA);
            }
        }
        loaderInfo.setVersion(loaderVersion);
        instance.setLoader(loaderInfo);
        
        return instance;
    }
}