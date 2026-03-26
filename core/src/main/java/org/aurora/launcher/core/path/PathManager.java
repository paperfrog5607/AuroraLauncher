package org.aurora.launcher.core.path;

import java.nio.file.Path;

public class PathManager {
    private static PathManager instance;

    private final Path baseDir;
    private final Path instancesDir;
    private final Path cacheDir;
    private final Path logsDir;
    private final Path configDir;
    private final Path tempDir;
    private final Path javaDir;
    private final Path versionsDir;

    private PathManager(Path basePath) {
        this.baseDir = basePath;
        this.instancesDir = basePath.resolve("instances");
        this.cacheDir = basePath.resolve("cache");
        this.logsDir = basePath.resolve("logs");
        this.configDir = basePath.resolve("config");
        this.tempDir = basePath.resolve("temp");
        this.javaDir = basePath.resolve("java");
        this.versionsDir = basePath.resolve("versions");
    }

    public static synchronized void initialize(Path basePath) {
        instance = new PathManager(basePath);
    }
    
    public static synchronized void reset() {
        instance = null;
    }

    public static PathManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PathManager not initialized. Call initialize() first.");
        }
        return instance;
    }

    public Path getBaseDirectory() {
        return baseDir;
    }

    public Path getInstancesDirectory() {
        return instancesDir;
    }

    public Path getInstanceDirectory(String name) {
        return instancesDir.resolve(name);
    }

    public Path getCacheDirectory() {
        return cacheDir;
    }

    public Path getLogsDirectory() {
        return logsDir;
    }

    public Path getConfigDirectory() {
        return configDir;
    }

    public Path getTempDirectory() {
        return tempDir;
    }

    public Path getJavaDirectory() {
        return javaDir;
    }

    public Path getVersionsDirectory() {
        return versionsDir;
    }
}