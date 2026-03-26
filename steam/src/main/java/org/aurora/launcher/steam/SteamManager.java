package org.aurora.launcher.steam;

import org.aurora.launcher.steam.api.SteamApiClient;
import org.aurora.launcher.steam.launcher.SteamGameLauncher;
import org.aurora.launcher.steam.model.SteamGame;
import org.aurora.launcher.steam.scanner.SteamGameScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Steam管理器
 * 整合Steam游戏扫描、API、启动功能
 */
public class SteamManager {

    private static final Logger logger = LoggerFactory.getLogger(SteamManager.class);

    private static SteamManager instance;

    private final SteamScanner scanner;
    private final SteamApiClient apiClient;
    private final SteamGameLauncher launcher;
    
    private final List<SteamGame> cachedGames;
    private boolean isInitialized;

    private SteamManager() {
        this.scanner = new SteamGameScanner();
        this.apiClient = new SteamApiClient();
        this.launcher = new SteamGameLauncher(scanner);
        this.cachedGames = new CopyOnWriteArrayList<>();
        this.isInitialized = false;
    }

    public static synchronized SteamManager getInstance() {
        if (instance == null) {
            instance = new SteamManager();
        }
        return instance;
    }

    /**
     * 初始化 - 扫描本地游戏
     */
    public void initialize() {
        if (isInitialized) {
            return;
        }

        logger.info("Initializing Steam Manager...");
        
        if (!scanner.isSteamInstalled()) {
            logger.warn("Steam is not installed on this system");
            isInitialized = true;
            return;
        }

        refreshGames();
        isInitialized = true;
        logger.info("Steam Manager initialized with {} games", cachedGames.size());
    }

    /**
     * 刷新游戏列表
     */
    public void refreshGames() {
        cachedGames.clear();
        
        if (!scanner.isSteamInstalled()) {
            logger.warn("Cannot refresh games: Steam not installed");
            return;
        }

        List<SteamGame> games = scanner.scanInstalledGames();
        cachedGames.addAll(games);
        
        logger.info("Refreshed Steam games: {} found", games.size());
    }

    /**
     * 获取所有已安装的游戏
     */
    public List<SteamGame> getInstalledGames() {
        return new ArrayList<>(cachedGames);
    }

    /**
     * 根据AppID获取游戏
     */
    public SteamGame getGameByAppId(String appId) {
        for (SteamGame game : cachedGames) {
            if (game.getAppId().equals(appId)) {
                return game;
            }
        }
        return null;
    }

    /**
     * 获取游戏封面URL
     */
    public String getGameHeaderUrl(SteamGame game) {
        if (game.getHeaderImage() != null) {
            return game.getHeaderImage();
        }
        return apiClient.getGameHeaderUrl(game.getAppIdAsInt());
    }

    /**
     * 获取游戏图标URL
     */
    public String getGameIconUrl(SteamGame game) {
        return apiClient.getGameIconUrl(game.getAppIdAsInt());
    }

    /**
     * 获取游戏背景URL
     */
    public String getGameBackgroundUrl(SteamGame game) {
        return apiClient.getGameBackgroundUrl(game.getAppIdAsInt());
    }

    /**
     * 启动游戏
     */
    public boolean launchGame(SteamGame game) {
        if (game == null) {
            logger.error("Cannot launch null game");
            return false;
        }

        if (!launcher.isSteamRunning()) {
            if (!launcher.startSteamIfNeeded()) {
                logger.error("Failed to start Steam");
                return false;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return launcher.launchGame(game);
    }

    /**
     * 直接启动（不通过Steam）
     */
    public boolean launchGameDirect(SteamGame game, String... args) {
        return launcher.launchGameDirect(game, args);
    }

    /**
     * 打开商店页面
     */
    public boolean openStorePage(SteamGame game) {
        return launcher.openStorePage(game);
    }

    /**
     * 检查Steam是否安装
     */
    public boolean isSteamInstalled() {
        return scanner.isSteamInstalled();
    }

    /**
     * 检查Steam是否在运行
     */
    public boolean isSteamRunning() {
        return launcher.isSteamRunning();
    }

    /**
     * 获取Steam安装路径
     */
    public String getSteamPath() {
        return scanner.getSteamPath();
    }

    /**
     * 搜索游戏
     */
    public List<SteamGame> searchGames(String query) {
        if (query == null || query.isEmpty()) {
            return getInstalledGames();
        }

        List<SteamGame> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (SteamGame game : cachedGames) {
            if (game.getName().toLowerCase().contains(lowerQuery)) {
                results.add(game);
            }
        }
        
        return results;
    }

    /**
     * 获取运行中的游戏
     */
    public List<SteamGame> getRunningGames() {
        List<SteamGame> running = new ArrayList<>();
        for (SteamGame game : cachedGames) {
            if (isGameRunning(game)) {
                running.add(game);
            }
        }
        return running;
    }

    /**
     * 检查游戏是否在运行
     */
    public boolean isGameRunning(SteamGame game) {
        if (game == null || game.getName() == null) {
            return false;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist");
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes());
            String exeName = game.getName() + ".exe";
            return output.toLowerCase().contains(exeName.toLowerCase());
        } catch (Exception e) {
            logger.debug("Failed to check if game is running: {}", game.getName());
            return false;
        }
    }

    /**
     * 获取Steam API客户端
     */
    public SteamApiClient getApiClient() {
        return apiClient;
    }

    /**
     * 获取游戏扫描器
     */
    public SteamScanner getScanner() {
        return scanner;
    }

    /**
     * 强制刷新
     */
    public void forceRefresh() {
        isInitialized = false;
        initialize();
    }
}
