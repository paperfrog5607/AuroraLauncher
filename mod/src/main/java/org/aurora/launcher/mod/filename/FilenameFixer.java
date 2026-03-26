package org.aurora.launcher.mod.filename;

import org.aurora.launcher.mod.scanner.ModInfo;

public class FilenameFixer {
    
    private String defaultFormat = "{name}-{version}.jar";
    
    public String fix(String originalFilename, ModInfo mod) {
        return format(mod, defaultFormat);
    }
    
    public String format(ModInfo mod, String format) {
        if (format == null || format.isEmpty()) {
            format = defaultFormat;
        }
        
        String name = sanitize(mod.getName() != null ? mod.getName() : mod.getId());
        String id = sanitize(mod.getId());
        String version = sanitize(mod.getVersion() != null ? mod.getVersion() : "unknown");
        String author = mod.getAuthors().isEmpty() ? "unknown" : sanitize(mod.getAuthors().get(0));
        
        String result = format
                .replace("{name}", name)
                .replace("{id}", id)
                .replace("{version}", version)
                .replace("{author}", author);
        
        if (!result.endsWith(".jar")) {
            result += ".jar";
        }
        
        return result;
    }
    
    private String sanitize(String input) {
        if (input == null) return "unknown";
        
        return input
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .replaceAll("_{2,}", "_")
                .trim();
    }
    
    public String getDefaultFormat() {
        return defaultFormat;
    }
    
    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }
}