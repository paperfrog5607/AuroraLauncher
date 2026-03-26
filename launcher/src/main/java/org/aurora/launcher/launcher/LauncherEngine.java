package org.aurora.launcher.launcher;

import org.aurora.launcher.launcher.account.AccountManager;
import org.aurora.launcher.launcher.config.LauncherConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LauncherEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(LauncherEngine.class);
    private static LauncherEngine instance;
    
    private GameDirectory gameDirectory;
    private LauncherConfig config;
    private AccountManager accountManager;
    
    private boolean initialized = false;
    
    private LauncherEngine() {
    }
    
    public static synchronized LauncherEngine getInstance() {
        if (instance == null) {
            instance = new LauncherEngine();
        }
        return instance;
    }
    
    public void initialize() {
        if (initialized) {
            logger.warn("LauncherEngine already initialized");
            return;
        }
        
        logger.info("Initializing Aurora Launcher Engine...");
        
        gameDirectory = GameDirectory.getInstance();
        gameDirectory.ensureDirectories();
        logger.info("Game directory: {}", gameDirectory.getLauncherDir());
        
        config = LauncherConfig.getInstance();
        logger.info("Launcher config loaded");
        
        accountManager = AccountManager.getInstance();
        logger.info("Account manager initialized, {} accounts", accountManager.getAccounts().size());
        
        initialized = true;
        logger.info("Aurora Launcher Engine initialized successfully");
    }
    
    public GameDirectory getGameDirectory() {
        return gameDirectory;
    }
    
    public LauncherConfig getConfig() {
        return config;
    }
    
    public AccountManager getAccountManager() {
        return accountManager;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
}