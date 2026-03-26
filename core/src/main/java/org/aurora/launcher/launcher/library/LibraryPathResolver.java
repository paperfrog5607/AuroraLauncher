package org.aurora.launcher.launcher.library;

public class LibraryPathResolver {

    public String resolve(String libraryName) {
        if (libraryName == null || libraryName.isEmpty()) {
            return null;
        }
        
        String[] parts = libraryName.split(":");
        if (parts.length < 3) {
            return null;
        }
        
        String group = parts[0].replace(".", "/");
        String artifact = parts[1];
        String version = parts[2];
        
        return group + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
    }

    public String resolveNative(String libraryName, String classifier) {
        String basePath = resolve(libraryName);
        if (basePath == null) return null;
        
        return basePath.replace(".jar", "-" + classifier + ".jar");
    }

    public String getFileName(String libraryName) {
        if (libraryName == null || libraryName.isEmpty()) {
            return null;
        }
        
        String[] parts = libraryName.split(":");
        if (parts.length < 3) {
            return null;
        }
        
        String artifact = parts[1];
        String version = parts[2];
        
        return artifact + "-" + version + ".jar";
    }
}