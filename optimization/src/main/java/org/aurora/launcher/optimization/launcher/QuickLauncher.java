package org.aurora.launcher.optimization.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class QuickLauncher {

    private static final Logger logger = LoggerFactory.getLogger(QuickLauncher.class);
    private static QuickLauncher instance;

    private final Map<String, CachedGameData> gameCache;
    private final ExecutorService preloadExecutor;
    private final Path cacheDir;

    private QuickLauncher() {
        this.gameCache = new ConcurrentHashMap<>();
        this.preloadExecutor = Executors.newCachedThreadPool();
        this.cacheDir = Paths.get(System.getProperty("user.home"), ".aurora", "preload");
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            logger.error("Failed to create cache directory", e);
        }
    }

    public static synchronized QuickLauncher getInstance() {
        if (instance == null) {
            instance = new QuickLauncher();
        }
        return instance;
    }

    public void preloadGame(String gameId, GameMetadata metadata) {
        preloadExecutor.submit(() -> {
            CachedGameData data = new CachedGameData();
            data.setGameId(gameId);
            data.setMetadata(metadata);
            data.setPreloadTime(System.currentTimeMillis());
            gameCache.put(gameId, data);
            logger.info("Game preloaded: {}", gameId);
        });
    }

    public LaunchResult quickLaunch(String gameId, String executablePath) {
        CachedGameData cached = gameCache.get(gameId);
        
        if (cached == null) {
            logger.warn("Game not preloaded, doing normal launch: {}", gameId);
            return normalLaunch(executablePath);
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            ProcessBuilder pb = new ProcessBuilder(executablePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            LaunchResult result = new LaunchResult();
            result.setSuccess(true);
            result.setProcess(process);
            result.setLaunchTimeMs(System.currentTimeMillis() - startTime);
            result.setMessage("Quick launch successful");
            
            logger.info("Quick launched {} in {}ms", gameId, result.getLaunchTimeMs());
            return result;
            
        } catch (IOException e) {
            logger.error("Failed to quick launch {}", gameId, e);
            LaunchResult result = new LaunchResult();
            result.setSuccess(false);
            result.setMessage("Launch failed: " + e.getMessage());
            return result;
        }
    }

    private LaunchResult normalLaunch(String executablePath) {
        try {
            long startTime = System.currentTimeMillis();
            ProcessBuilder pb = new ProcessBuilder(executablePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            LaunchResult result = new LaunchResult();
            result.setSuccess(true);
            result.setProcess(process);
            result.setLaunchTimeMs(System.currentTimeMillis() - startTime);
            result.setMessage("Normal launch successful");
            return result;
        } catch (IOException e) {
            LaunchResult result = new LaunchResult();
            result.setSuccess(false);
            result.setMessage("Launch failed: " + e.getMessage());
            return result;
        }
    }

    public void cacheGameAssets(String gameId, List<Path> assets) {
        preloadExecutor.submit(() -> {
            CachedGameData cached = gameCache.get(gameId);
            if (cached != null) {
                cached.setAssetsCached(true);
                logger.info("Assets cached for game: {}", gameId);
            }
        });
    }

    public void clearCache(String gameId) {
        gameCache.remove(gameId);
        logger.info("Cache cleared for game: {}", gameId);
    }

    public void clearAllCache() {
        gameCache.clear();
        logger.info("All game cache cleared");
    }

    public Set<String> getPreloadedGames() {
        return new HashSet<>(gameCache.keySet());
    }

    public static class GameMetadata implements Serializable {
        private String name;
        private String version;
        private long size;
        private String iconPath;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public String getIconPath() { return iconPath; }
        public void setIconPath(String iconPath) { this.iconPath = iconPath; }
    }

    public static class CachedGameData {
        private String gameId;
        private GameMetadata metadata;
        private long preloadTime;
        private boolean assetsCached;
        
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        public GameMetadata getMetadata() { return metadata; }
        public void setMetadata(GameMetadata metadata) { this.metadata = metadata; }
        public long getPreloadTime() { return preloadTime; }
        public void setPreloadTime(long preloadTime) { this.preloadTime = preloadTime; }
        public boolean isAssetsCached() { return assetsCached; }
        public void setAssetsCached(boolean assetsCached) { this.assetsCached = assetsCached; }
    }

    public static class LaunchResult {
        private boolean success;
        private Process process;
        private long launchTimeMs;
        private String message;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Process getProcess() { return process; }
        public void setProcess(Process process) { this.process = process; }
        public long getLaunchTimeMs() { return launchTimeMs; }
        public void setLaunchTimeMs(long launchTimeMs) { this.launchTimeMs = launchTimeMs; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}