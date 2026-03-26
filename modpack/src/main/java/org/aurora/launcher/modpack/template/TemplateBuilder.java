package org.aurora.launcher.modpack.template;

import org.aurora.launcher.modpack.instance.Instance;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TemplateBuilder {
    
    private String id;
    private String name;
    private String description;
    private String minecraftVersion;
    private String loaderType;
    private String loaderVersion;
    private List<String> tags = new ArrayList<>();
    private List<String> defaultMods = new ArrayList<>();
    private String iconPath;
    private String author;
    
    public TemplateBuilder() {
        this.id = UUID.randomUUID().toString();
    }
    
    public TemplateBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    public TemplateBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public TemplateBuilder description(String description) {
        this.description = description;
        return this;
    }
    
    public TemplateBuilder minecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
        return this;
    }
    
    public TemplateBuilder loaderType(String loaderType) {
        this.loaderType = loaderType;
        return this;
    }
    
    public TemplateBuilder loaderVersion(String loaderVersion) {
        this.loaderVersion = loaderVersion;
        return this;
    }
    
    public TemplateBuilder addTag(String tag) {
        this.tags.add(tag);
        return this;
    }
    
    public TemplateBuilder tags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        return this;
    }
    
    public TemplateBuilder addDefaultMod(String modId) {
        this.defaultMods.add(modId);
        return this;
    }
    
    public TemplateBuilder defaultMods(List<String> defaultMods) {
        this.defaultMods = defaultMods != null ? new ArrayList<>(defaultMods) : new ArrayList<>();
        return this;
    }
    
    public TemplateBuilder iconPath(String iconPath) {
        this.iconPath = iconPath;
        return this;
    }
    
    public TemplateBuilder author(String author) {
        this.author = author;
        return this;
    }
    
    public TemplateBuilder fromInstance(Instance instance) {
        if (instance != null) {
            this.name = instance.getName();
            this.minecraftVersion = instance.getConfig() != null ? 
                    instance.getConfig().getMinecraftVersion() : null;
            this.loaderType = instance.getConfig() != null ? 
                    instance.getConfig().getLoaderType() : "vanilla";
            this.loaderVersion = instance.getConfig() != null ? 
                    instance.getConfig().getLoaderVersion() : null;
            this.tags = instance.getTags() != null ? 
                    new ArrayList<>(instance.getTags()) : new ArrayList<>();
            this.iconPath = instance.getIconPath();
        }
        return this;
    }
    
    public Template build() {
        Template template = new Template();
        template.setId(id);
        template.setName(name);
        template.setDescription(description);
        template.setMinecraftVersion(minecraftVersion);
        template.setLoaderType(loaderType != null ? loaderType : "vanilla");
        template.setLoaderVersion(loaderVersion);
        template.setTags(tags);
        template.setDefaultMods(defaultMods);
        template.setIconPath(iconPath);
        template.setAuthor(author);
        template.setCreatedTime(Instant.now());
        return template;
    }
}