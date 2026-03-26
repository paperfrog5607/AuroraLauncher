package org.aurora.launcher.steam.launcher;

import org.aurora.launcher.steam.model.SteamGame;
import org.aurora.launcher.steam.scanner.SteamGameScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Steam游戏启动器
 */
public class SteamGameLauncher {

    private static final Logger logger = LoggerFactory.getLogger(SteamGameLauncher.class);

    private final SteamGameScanner scanner;

    public SteamGameLauncher() {
        this.scanner = new SteamGameScanner();
    }

    public SteamGameLauncher(SteamGameScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 通过Steam协议启动游戏
     * 使用 steam://rungameid/ 的方式
     */
    public boolean launchGame(SteamGame game) {
        if (game == null || game.getAppId() == null) {
            logger.error("Cannot launch game: invalid game object");
            return false;
        }

        String appId = game.getAppId();
        
        try {
            String steamUrl = "steam://rungameid/" + appId;
            logger.info("Launching game via Steam: {} ({})", game.getName(), appId);
            
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "", steamUrl);
            pb.start();
            
            return true;
        } catch (IOException e) {
            logger.error("Failed to launch game: {}", game.getName(), e);
            return false;
        }
    }

    /**
     * 通过Steam Big Picture模式启动
     */
    public boolean launchInBigPicture(SteamGame game) {
        if (game == null || game.getAppId() == null) {
            return false;
        }

        try {
            String steamUrl = "steam://rungameid/" + game.getAppId() + "?bigpicture";
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "", steamUrl);
            pb.start();
            return true;
        } catch (IOException e) {
            logger.error("Failed to launch game in Big Picture mode", e);
            return false;
        }
    }

    /**
     * 直接通过可执行文件启动游戏（不通过Steam）
     */
    public boolean launchGameDirect(SteamGame game, String... extraArgs) {
        File exe = scanner.findGameExecutable(game);
        if (exe == null || !exe.exists()) {
            logger.error("Game executable not found: {}", game.getName());
            return false;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder();
            
            java.util.List<String> commands = new java.util.ArrayList<>();
            commands.add(exe.getAbsolutePath());
            for (String arg : extraArgs) {
                commands.add(arg);
            }
            
            pb.command(commands);
            pb.directory(exe.getParentFile());
            
            logger.info("Launching game directly: {}", exe.getAbsolutePath());
            pb.start();
            
            return true;
        } catch (IOException e) {
            logger.error("Failed to launch game directly: {}", game.getName(), e);
            return false;
        }
    }

    /**
     * 打开游戏商店页面
     */
    public boolean openStorePage(SteamGame game) {
        if (game == null || game.getAppId() == null) {
            return false;
        }

        try {
            String url = "https://store.steampowered.com/app/" + game.getAppId();
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "", url);
            pb.start();
            return true;
        } catch (IOException e) {
            logger.error("Failed to open store page", e);
            return false;
        }
    }

    /**
     * 打开Steam社区页面
     */
    public boolean openCommunityPage(SteamGame game) {
        if (game == null || game.getAppId() == null) {
            return false;
        }

        try {
            String url = "https://steamcommunity.com/app/" + game.getAppId();
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "", url);
            pb.start();
            return true;
        } catch (IOException e) {
            logger.error("Failed to open community page", e);
            return false;
        }
    }

    /**
     * 检查Steam是否在运行
     */
    public boolean isSteamRunning() {
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist");
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes());
            return output.contains("steam.exe") || output.contains("Steam.exe");
        } catch (IOException e) {
            logger.error("Failed to check if Steam is running", e);
            return false;
        }
    }

    /**
     * 启动Steam（如果未运行）
     */
    public boolean startSteamIfNeeded() {
        if (isSteamRunning()) {
            logger.info("Steam is already running");
            return true;
        }

        String steamPath = scanner.getSteamPath();
        if (steamPath == null) {
            logger.error("Steam installation not found");
            return false;
        }

        try {
            File steamExe = new File(steamPath, "steam.exe");
            if (!steamExe.exists()) {
                logger.error("Steam executable not found at: {}", steamExe.getAbsolutePath());
                return false;
            }

            logger.info("Starting Steam from: {}", steamExe.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder(steamExe.getAbsolutePath(), "-silent");
            pb.start();
            return true;
        } catch (IOException e) {
            logger.error("Failed to start Steam", e);
            return false;
        }
    }
}
