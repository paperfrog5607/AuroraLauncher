package org.aurora.launcher.launcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameDirectory {
    
    private static GameDirectory instance;
    
    private final Path launcherDir;
    private final Path minecraftDir;
    
    private GameDirectory() {
        this.launcherDir = detectLauncherDirectory();
        this.minecraftDir = launcherDir.resolve(".minecraft");
    }
    
    public static synchronized GameDirectory getInstance() {
        if (instance == null) {
            instance = new GameDirectory();
        }
        return instance;
    }
    
    private Path detectLauncherDirectory() {
        File exeFile = new File(System.getProperty("java.started.launcher.path", 
            GameDirectory.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
        
        if (exeFile.exists()) {
            return exeFile.getParentFile().toPath().toAbsolutePath().normalize();
        }
        
        String userDir = System.getProperty("user.dir");
        return Paths.get(userDir).toAbsolutePath().normalize();
    }
    
    public Path getLauncherDir() {
        return launcherDir;
    }
    
    public Path getMinecraftDir() {
        return minecraftDir;
    }
    
    public Path getVersionsDir() {
        return minecraftDir.resolve("versions");
    }
    
    public Path getModsDir() {
        return minecraftDir.resolve("mods");
    }
    
    public Path getResourcePacksDir() {
        return minecraftDir.resolve("resourcepacks");
    }
    
    public Path getShadersDir() {
        return minecraftDir.resolve("shaderpacks");
    }
    
    public Path getSavesDir() {
        return minecraftDir.resolve("saves");
    }
    
    public Path getScreenshotsDir() {
        return minecraftDir.resolve("screenshots");
    }
    
    public Path getLibrariesDir() {
        return minecraftDir.resolve("libraries");
    }
    
    public Path getAssetsDir() {
        return minecraftDir.resolve("assets");
    }
    
    public Path getAccountsDir() {
        return launcherDir.resolve("accounts");
    }
    
    public Path getConfigsDir() {
        return launcherDir.resolve("configs");
    }
    
    public Path getCacheDir() {
        return launcherDir.resolve("cache");
    }
    
    public Path getLogsDir() {
        return launcherDir.resolve("logs");
    }
    
    public Path getVersionDir(String versionName) {
        return getVersionsDir().resolve(versionName);
    }
    
    public Path getVersionJar(String versionName) {
        return getVersionDir(versionName).resolve(versionName + ".jar");
    }
    
    public Path getVersionJson(String versionName) {
        return getVersionDir(versionName).resolve(versionName + ".json");
    }
    
    public Path getNativesDir(String versionName) {
        return getVersionDir(versionName).resolve("natives");
    }
    
    public void ensureDirectories() {
        mkdirs(getMinecraftDir());
        mkdirs(getVersionsDir());
        mkdirs(getModsDir());
        mkdirs(getResourcePacksDir());
        mkdirs(getShadersDir());
        mkdirs(getSavesDir());
        mkdirs(getScreenshotsDir());
        mkdirs(getLibrariesDir());
        mkdirs(getAssetsDir());
        mkdirs(getAccountsDir());
        mkdirs(getConfigsDir());
        mkdirs(getCacheDir());
        mkdirs(getLogsDir());
    }
    
    private void mkdirs(Path dir) {
        if (dir != null && !dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }
    }
    
    public File getLauncherExe() {
        return launcherDir.resolve("AuroraLauncher.exe").toFile();
    }
}
