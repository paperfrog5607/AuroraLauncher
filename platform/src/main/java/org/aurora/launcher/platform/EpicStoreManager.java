package org.aurora.launcher.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class EpicStoreManager {

    private static final Logger logger = LoggerFactory.getLogger(EpicStoreManager.class);
    private static EpicStoreManager instance;

    private final Map<String, EpicGame> installedGames;
    private final ExecutorService executor;

    private static final String EPIC_LAUNCHER_PATH = "C:\\Program Files (x86)\\Epic Games\\Launcher\\Portal\\Binaries\\Win64\\EpicGamesLauncher.exe";
    private String getManifestDir() {
        String localAppData = System.getenv("LOCALAPPDATA");
        return localAppData + "\\EpicGamesLauncher\\Data\\Manifests";
    }

    private EpicStoreManager() {
        this.installedGames = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    public static synchronized EpicStoreManager getInstance() {
        if (instance == null) {
            instance = new EpicStoreManager();
        }
        return instance;
    }

    public void scanInstalledGames(ScanCallback callback) {
        executor.submit(() -> {
            installedGames.clear();
            
            File manifestDir = new File(getManifestDir());
            if (!manifestDir.exists()) {
                callback.onError("Epic Games manifest directory not found");
                return;
            }

            File[] manifests = manifestDir.listFiles((dir, name) -> name.endsWith(".item"));
            if (manifests == null || manifests.length == 0) {
                callback.onSuccess(new ArrayList<>(installedGames.values()));
                return;
            }

            for (File manifest : manifests) {
                try {
                    EpicGame game = parseManifest(manifest);
                    if (game != null) {
                        installedGames.put(game.id, game);
                    }
                } catch (Exception e) {
                    logger.error("Failed to parse manifest: " + manifest.getName(), e);
                }
            }

            logger.info("Found {} Epic games", installedGames.size());
            callback.onSuccess(new ArrayList<>(installedGames.values()));
        });
    }

    private EpicGame parseManifest(File manifest) throws IOException {
        String content = Files.readString(manifest.toPath());
        
        EpicGame game = new EpicGame();
        game.id = extractString(content, "AppId");
        game.displayName = extractString(content, "DisplayName");
        game.installPath = extractString(content, "InstallLocation");
        game.version = extractString(content, "Version");
        game.launchExecutable = extractString(content, "LaunchExecutable");
        
        if (game.installPath.isEmpty() || game.launchExecutable.isEmpty()) {
            return null;
        }
        
        game.exePath = game.installPath + "\\" + game.launchExecutable;
        return game;
    }

    public boolean isInstalled() {
        return new File(EPIC_LAUNCHER_PATH).exists();
    }

    public void launchGame(String gameId, LaunchCallback callback) {
        EpicGame game = installedGames.get(gameId);
        if (game == null) {
            callback.onError("Game not found: " + gameId);
            return;
        }

        executor.submit(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    EPIC_LAUNCHER_PATH,
                    "-opengames",
                    "com.epicgames.launcher://apps/" + game.id + "?action=launch&silent=true"
                );
                pb.start();
                callback.onSuccess();
                logger.info("Launched Epic game: {}", game.displayName);
            } catch (IOException e) {
                logger.error("Failed to launch Epic game", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void launchGameDirect(String gameId, LaunchCallback callback) {
        EpicGame game = installedGames.get(gameId);
        if (game == null) {
            callback.onError("Game not found: " + gameId);
            return;
        }

        executor.submit(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(game.exePath);
                pb.start();
                callback.onSuccess();
                logger.info("Launched Epic game directly: {}", game.displayName);
            } catch (IOException e) {
                logger.error("Failed to launch Epic game directly", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public List<EpicGame> getInstalledGames() {
        return new ArrayList<>(installedGames.values());
    }

    public EpicGame getGame(String gameId) {
        return installedGames.get(gameId);
    }

    private String extractString(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    public static class EpicGame {
        public String id;
        public String displayName;
        public String installPath;
        public String exePath;
        public String version;
        public String launchExecutable;
    }

    public interface ScanCallback {
        void onSuccess(List<EpicGame> games);
        void onError(String error);
    }

    public interface LaunchCallback {
        void onSuccess();
        void onError(String error);
    }
}