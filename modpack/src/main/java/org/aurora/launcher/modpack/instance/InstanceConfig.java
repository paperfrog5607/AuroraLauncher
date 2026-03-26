package org.aurora.launcher.modpack.instance;

import java.util.ArrayList;
import java.util.List;

public class InstanceConfig {
    
    private String minecraftVersion;
    private String loaderType;
    private String loaderVersion;
    private MemoryConfig memory;
    private JavaConfig java;
    private WindowConfig window;
    private List<String> customArgs;
    
    public InstanceConfig() {
        this.customArgs = new ArrayList<>();
        this.memory = new MemoryConfig();
        this.java = new JavaConfig();
        this.window = new WindowConfig();
    }
    
    public String getMinecraftVersion() {
        return minecraftVersion;
    }
    
    public void setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }
    
    public String getLoaderType() {
        return loaderType;
    }
    
    public void setLoaderType(String loaderType) {
        this.loaderType = loaderType;
    }
    
    public String getLoaderVersion() {
        return loaderVersion;
    }
    
    public void setLoaderVersion(String loaderVersion) {
        this.loaderVersion = loaderVersion;
    }
    
    public MemoryConfig getMemory() {
        return memory;
    }
    
    public void setMemory(MemoryConfig memory) {
        this.memory = memory;
    }
    
    public JavaConfig getJava() {
        return java;
    }
    
    public void setJava(JavaConfig java) {
        this.java = java;
    }
    
    public WindowConfig getWindow() {
        return window;
    }
    
    public void setWindow(WindowConfig window) {
        this.window = window;
    }
    
    public List<String> getCustomArgs() {
        return customArgs;
    }
    
    public void setCustomArgs(List<String> customArgs) {
        this.customArgs = customArgs != null ? customArgs : new ArrayList<>();
    }
    
    public void addCustomArg(String arg) {
        customArgs.add(arg);
    }
    
    public static class MemoryConfig {
        private String preset = "standard";
        private int minMB = 2048;
        private int maxMB = 4096;
        
        public String getPreset() {
            return preset;
        }
        
        public void setPreset(String preset) {
            this.preset = preset;
        }
        
        public int getMinMB() {
            return minMB;
        }
        
        public void setMinMB(int minMB) {
            this.minMB = minMB;
        }
        
        public int getMaxMB() {
            return maxMB;
        }
        
        public void setMaxMB(int maxMB) {
            this.maxMB = maxMB;
        }
    }
    
    public static class JavaConfig {
        private String path;
        private List<String> args = new ArrayList<>();
        
        public JavaConfig() {
            args.add("-XX:+UseG1GC");
        }
        
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public List<String> getArgs() {
            return args;
        }
        
        public void setArgs(List<String> args) {
            this.args = args != null ? args : new ArrayList<>();
        }
    }
    
    public static class WindowConfig {
        private int width = 854;
        private int height = 480;
        private boolean fullscreen = false;
        
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public boolean isFullscreen() {
            return fullscreen;
        }
        
        public void setFullscreen(boolean fullscreen) {
            this.fullscreen = fullscreen;
        }
    }
}