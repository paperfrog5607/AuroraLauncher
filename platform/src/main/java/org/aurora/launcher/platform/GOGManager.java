package org.aurora.launcher.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class GOGManager {

    private static final Logger logger = LoggerFactory.getLogger(GOGManager.class);
    private static GOGManager instance;

    private final Map<String, GOGGame> installedGames;
    private final ExecutorService executor;

    private static final String GOG_LAUNCHER_PATH = "C:\\Program Files (x86)\\GOG Galaxy\\GalaxyClient.exe";
    private static final String GAMES_REGISTRY_KEY = "HKCU\\Software\\GOG.com\\Games";

    private GOGManager() {
        this.installedGames = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    public static synchronized GOGManager getInstance() {
        if (instance == null) {
            instance = new GOGManager();
        }
        return instance;
    }

    public void scanInstalledGames(ScanCallback callback) {
        executor.submit(() -> {
            installedGames.clear();
            
            try {
                ProcessResult result = runRegQuery(GAMES_REGISTRY_KEY);
                if (result.exitCode != 0) {
                    logger.warn("GOG registry key not found, trying alternative scan");
                    scanGOGGalaxyFolder();
                } else {
                    parseRegistryOutput(result.output);
                }
            } catch (Exception e) {
                logger.error("Failed to scan GOG games via registry", e);
                scanGOGGalaxyFolder();
            }

            logger.info("Found {} GOG games", installedGames.size());
            callback.onSuccess(new ArrayList<>(installedGames.values()));
        });
    }

    private void scanGOGGalaxyFolder() {
        String programData = System.getenv("ProgramData");
        if (programData == null) return;
        
        Path gogFolder = Paths.get(programData, "GOG.com", "Games");
        if (!Files.exists(gogFolder)) return;

        try {
            Files.walk(gogFolder).filter(p -> p.toString().endsWith(".json")).forEach(p -> {
                try {
                    String content = Files.readString(p);
                    GOGGame game = parseGameJson(content);
                    if (game != null) {
                        installedGames.put(game.gameId, game);
                    }
                } catch (Exception e) {
                    logger.error("Failed to parse GOG game json: " + p, e);
                }
            });
        } catch (IOException e) {
            logger.error("Failed to walk GOG games folder", e);
        }
    }

    private GOGGame parseGameJson(String json) {
        GOGGame game = new GOGGame();
        game.gameId = extractString(json, "gameId");
        game.name = extractString(json, "name");
        game.installPath = extractString(json, "installPath");
        game.exe = extractString(json, "exe");
        game.version = extractString(json, "version");
        
        if (game.installPath.isEmpty() || game.exe.isEmpty()) {
            return null;
        }
        
        game.exePath = game.installPath + "\\" + game.exe;
        return game;
    }

    private void parseRegistryOutput(String output) {
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            String gameId = line.trim();
            if (gameId.matches("\\d+")) {
                try {
                    String keyPath = GAMES_REGISTRY_KEY + "\\" + gameId;
                    ProcessResult result = runRegQuery(keyPath);
                    if (result.exitCode == 0) {
                        GOGGame game = parseRegistryGame(result.output, gameId);
                        if (game != null) {
                            installedGames.put(game.gameId, game);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Failed to parse game: " + gameId, e);
                }
            }
        }
    }

    private GOGGame parseRegistryGame(String output, String gameId) {
        GOGGame game = new GOGGame();
        game.gameId = gameId;
        game.name = extractRegistryValue(output, "gameName");
        game.installPath = extractRegistryValue(output, "installPath");
        game.exe = extractRegistryValue(output, "executable");
        game.exePath = game.installPath + "\\" + game.exe;
        game.version = extractRegistryValue(output, "version");
        
        if (game.name.isEmpty() || game.installPath.isEmpty()) {
            return null;
        }
        return game;
    }

    private String extractRegistryValue(String output, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            key + "\\s+REG_SZ\\s+(.+)");
        java.util.regex.Matcher m = p.matcher(output);
        return m.find() ? m.group(1).trim() : "";
    }

    private String extractString(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    public boolean isInstalled() {
        return new File(GOG_LAUNCHER_PATH).exists();
    }

    public void launchGame(String gameId, LaunchCallback callback) {
        GOGGame game = installedGames.get(gameId);
        if (game == null) {
            callback.onError("Game not found: " + gameId);
            return;
        }

        executor.submit(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    GOG_LAUNCHER_PATH,
                    "/command=runGame",
                    "/gameId=" + game.gameId
                );
                pb.start();
                callback.onSuccess();
                logger.info("Launched GOG game: {}", game.name);
            } catch (IOException e) {
                logger.error("Failed to launch GOG game", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void launchGameDirect(String gameId, LaunchCallback callback) {
        GOGGame game = installedGames.get(gameId);
        if (game == null) {
            callback.onError("Game not found: " + gameId);
            return;
        }

        executor.submit(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(game.exePath);
                pb.start();
                callback.onSuccess();
                logger.info("Launched GOG game directly: {}", game.name);
            } catch (IOException e) {
                logger.error("Failed to launch GOG game directly", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public List<GOGGame> getInstalledGames() {
        return new ArrayList<>(installedGames.values());
    }

    public GOGGame getGame(String gameId) {
        return installedGames.get(gameId);
    }

    private ProcessResult runRegQuery(String key) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("reg", "query", key);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes());
        int exitCode = p.waitFor();
        return new ProcessResult(exitCode, output);
    }

    private static class ProcessResult {
        int exitCode;
        String output;
        ProcessResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
    }

    public static class GOGGame {
        public String gameId;
        public String name;
        public String installPath;
        public String exe;
        public String exePath;
        public String version;
    }

    public interface ScanCallback {
        void onSuccess(List<GOGGame> games);
        void onError(String error);
    }

    public interface LaunchCallback {
        void onSuccess();
        void onError(String error);
    }
}