package org.aurora.launcher.steam.scanner;

import org.aurora.launcher.steam.model.SteamGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Steam游戏目录扫描器
 * 扫描本地Steam安装目录，检测已安装的游戏
 */
public class SteamGameScanner {

    private static final Logger logger = LoggerFactory.getLogger(SteamGameScanner.class);

    private static final String STEAM_REGISTRY_KEY = "HKLM\\SOFTWARE\\Valve\\Steam";
    private static final String STEAM_REGISTRY_KEY_ALT = "HKCU\\SOFTWARE\\Valve\\Steam";
    private static final String STEAM_DEFAULT_PATH = "C:\\Program Files (x86)\\Steam";
    private static final String STEAM_DEFAULT_PATH_ALT = "C:\\Program Files\\Steam";
    private static final String STEAMAPPS_FOLDER = "steamapps";
    private static final String MANIFEST_FOLDER = "appmanifest_*.acf";

    private String steamPath;

    public SteamGameScanner() {
        this.steamPath = findSteamPath();
    }

    /**
     * 查找Steam安装路径
     */
    public String findSteamPath() {
        // 1. 从注册表读取
        String registryPath = readSteamPathFromRegistry(STEAM_REGISTRY_KEY);
        if (registryPath != null && isValidSteamPath(registryPath)) {
            return registryPath;
        }

        registryPath = readSteamPathFromRegistry(STEAM_REGISTRY_KEY_ALT);
        if (registryPath != null && isValidSteamPath(registryPath)) {
            return registryPath;
        }

        // 2. 检查默认路径
        if (isValidSteamPath(STEAM_DEFAULT_PATH)) {
            return STEAM_DEFAULT_PATH;
        }

        if (isValidSteamPath(STEAM_DEFAULT_PATH_ALT)) {
            return STEAM_DEFAULT_PATH_ALT;
        }

        logger.warn("Steam installation path not found");
        return null;
    }

    private String readSteamPathFromRegistry(String key) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "reg", "query", key, "/v", "InstallPath"
            );
            Process process = pb.start();
            String result = new String(process.getInputStream().readAllBytes());
            
            int index = result.indexOf("REG_SZ");
            if (index != -1) {
                String path = result.substring(index + 6).trim();
                return path.split("\\s+")[0];
            }
        } catch (Exception e) {
            logger.debug("Failed to read Steam path from registry: {}", e.getMessage());
        }
        return null;
    }

    private boolean isValidSteamPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        File steamExe = new File(path, "steam.exe");
        File steamApps = new File(path, STEAMAPPS_FOLDER);
        return steamExe.exists() || steamApps.exists();
    }

    /**
     * 扫描已安装的游戏
     */
    public List<SteamGame> scanInstalledGames() {
        List<SteamGame> games = new ArrayList<>();
        
        if (steamPath == null) {
            logger.warn("Steam path is null, cannot scan games");
            return games;
        }

        File steamApps = new File(steamPath, STEAMAPPS_FOLDER);
        if (!steamApps.exists()) {
            logger.warn("steamapps folder not found at: {}", steamApps.getAbsolutePath());
            return games;
        }

        // 扫描 manifest 文件
        File[] manifests = steamApps.listFiles((dir, name) -> 
            name.startsWith("appmanifest_") && name.endsWith(".acf")
        );

        if (manifests != null) {
            for (File manifest : manifests) {
                try {
                    SteamGame game = parseManifest(manifest);
                    if (game != null) {
                        games.add(game);
                        logger.debug("Found game: {} ({})", game.getName(), game.getAppId());
                    }
                } catch (Exception e) {
                    logger.error("Failed to parse manifest: {}", manifest.getName(), e);
                }
            }
        }

        logger.info("Found {} Steam games", games.size());
        return games;
    }

    /**
     * 解析app manifest文件
     */
    private SteamGame parseManifest(File manifest) {
        try {
            String content = Files.readString(manifest.toPath());
            Map<String, String> values = new HashMap<>();
            
            for (String line : content.split("\n")) {
                line = line.trim();
                if (line.startsWith("\"") && line.contains("\"")) {
                    int endQuote = line.indexOf("\"", 1);
                    String key = line.substring(1, endQuote);
                    String rest = line.substring(endQuote + 1).trim();
                    if (rest.startsWith("\"")) {
                        int endVal = rest.lastIndexOf("\"");
                        if (endVal > 1) {
                            String value = rest.substring(1, endVal);
                            values.put(key, value);
                        }
                    }
                }
            }

            String appId = values.get("appid");
            String name = values.get("name");
            String installDir = values.get("installdir");
            String sizeOnDisk = values.get("SizeOnDisk");

            if (appId != null && name != null) {
                SteamGame game = new SteamGame(appId, name);
                
                if (installDir != null) {
                    File gamePath = new File(steamApps, STEAMAPPS_FOLDER + "/" + installDir);
                    if (gamePath.exists()) {
                        game.setInstallPath(gamePath.getAbsolutePath());
                    } else {
                        gamePath = new File(steamApps.getParentFile(), installDir);
                        if (gamePath.exists()) {
                            game.setInstallPath(gamePath.getAbsolutePath());
                        }
                    }
                }
                
                return game;
            }
        } catch (IOException e) {
            logger.error("Failed to read manifest file: {}", manifest.getAbsolutePath(), e);
        }
        
        return null;
    }

    /**
     * 获取游戏安装目录
     */
    public File getGameInstallDir(SteamGame game) {
        if (game.getInstallPath() != null) {
            return new File(game.getInstallPath());
        }
        
        if (steamPath == null) {
            return null;
        }

        File commonFolder = new File(steamPath, STEAMAPPS_FOLDER + "/common");
        if (commonFolder.exists()) {
            File[] dirs = commonFolder.listFiles();
            if (dirs != null) {
                for (File dir : dirs) {
                    if (dir.isDirectory() && dir.getName().toLowerCase().contains(game.getName().toLowerCase())) {
                        return dir;
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * 获取游戏可执行文件
     */
    public File findGameExecutable(SteamGame game) {
        File installDir = getGameInstallDir(game);
        if (installDir == null || !installDir.exists()) {
            return null;
        }

        // 常见游戏可执行文件模式
        String[] patterns = {
            game.getName() + ".exe",
            game.getName().replace(":", "") + ".exe",
            "Game.exe",
            "PlayGame.exe",
            "Launch.exe",
            "Start.exe"
        };

        return findExeInDirectory(installDir, patterns);
    }

    private File findExeInDirectory(File dir, String[] patterns) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return null;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".exe")) {
                for (String pattern : patterns) {
                    if (file.getName().equalsIgnoreCase(pattern)) {
                        return file;
                    }
                }
            } else if (file.isDirectory()) {
                File found = findExeInDirectory(file, patterns);
                if (found != null) {
                    return found;
                }
            }
        }
        
        return null;
    }

    /**
     * 验证游戏是否仍安装在原位置
     */
    public boolean isGameStillInstalled(SteamGame game) {
        if (game.getInstallPath() == null) {
            return false;
        }
        
        File installDir = new File(game.getInstallPath());
        if (!installDir.exists()) {
            return false;
        }

        File exe = findGameExecutable(game);
        return exe != null && exe.exists();
    }

    public String getSteamPath() {
        return steamPath;
    }

    public boolean isSteamInstalled() {
        return steamPath != null && isValidSteamPath(steamPath);
    }
}
