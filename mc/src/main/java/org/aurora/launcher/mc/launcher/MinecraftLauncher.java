package org.aurora.launcher.mc.launcher;

import org.aurora.launcher.mc.MinecraftManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Minecraft启动器
 */
public class MinecraftLauncher {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftLauncher.class);

    private final MinecraftManager manager;

    public MinecraftLauncher() {
        this.manager = MinecraftManager.getInstance();
    }

    /**
     * 启动Minecraft实例
     */
    public boolean launch(MinecraftManager.MinecraftInstance instance) {
        if (instance == null) {
            logger.error("Cannot launch null instance");
            return false;
        }

        logger.info("Launching Minecraft instance: {} ({})", instance.getName(), instance.getVersion());

        try {
            File javaPath = findJava();
            if (javaPath == null) {
                logger.error("Java not found");
                return false;
            }

            File gameDir = instance.getPath();
            if (gameDir == null || !gameDir.exists()) {
                logger.error("Game directory not found: {}", gameDir);
                return false;
            }

            String jvmArgs = buildJvmArgs(instance);
            String gameArgs = buildGameArgs(instance);

            ProcessBuilder pb = new ProcessBuilder(
                javaPath.getAbsolutePath(),
                jvmArgs,
                "-jar", findLauncherJar(),
                gameArgs
            );
            
            pb.directory(gameDir);
            pb.environment().put("INST_NAME", instance.getName());
            pb.environment().put("INST_ID", instance.getId());
            
            logger.info("Starting Minecraft with args: {}", gameArgs);
            pb.start();
            
            return true;
        } catch (Exception e) {
            logger.error("Failed to launch Minecraft instance: {}", instance.getName(), e);
            return false;
        }
    }

    /**
     * 快速启动（使用默认设置）
     */
    public boolean quickLaunch(String instanceId) {
        MinecraftManager.MinecraftInstance instance = manager.getInstance(instanceId);
        if (instance == null) {
            logger.error("Instance not found: {}", instanceId);
            return false;
        }
        return launch(instance);
    }

    /**
     * 使用指定参数启动
     */
    public boolean launchWithArgs(String instanceId, String... extraArgs) {
        MinecraftManager.MinecraftInstance instance = manager.getInstance(instanceId);
        if (instance == null) {
            return false;
        }

        try {
            File javaPath = findJava();
            if (javaPath == null) {
                return false;
            }

            File gameDir = instance.getPath();
            if (gameDir == null) {
                return false;
            }

            StringBuilder jvmArgs = new StringBuilder();
            jvmArgs.append("-Xmx4G -Xms2G ");
            jvmArgs.append("-XX:+UseG1GC ");
            jvmArgs.append("-XX:+ParallelRefProcEnabled ");
            jvmArgs.append("-XX:MaxGCPauseMillis=200 ");
            
            for (String arg : extraArgs) {
                jvmArgs.append(arg).append(" ");
            }

            ProcessBuilder pb = new ProcessBuilder(
                javaPath.getAbsolutePath(),
                jvmArgs.toString().split("\\s+"),
                "-jar", findLauncherJar(),
                buildGameArgs(instance)
            );
            
            pb.directory(gameDir);
            pb.start();
            return true;
        } catch (Exception e) {
            logger.error("Failed to launch with custom args", e);
            return false;
        }
    }

    private String buildJvmArgs(MinecraftManager.MinecraftInstance instance) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("-Xmx4G -Xms2G ");
        sb.append("-XX:+UseG1GC ");
        sb.append("-XX:+ParallelRefProcEnabled ");
        sb.append("-XX:MaxGCPauseMillis=200 ");
        sb.append("-XX:+UnlockExperimentalVMOptions ");
        sb.append("-XX:+DisableExplicitGC ");
        sb.append("-XX:G1NewSizePercent=30 ");
        sb.append("-XX:G1HeapRegionSize=8 ");
        sb.append("-XX:G1ReservePercent=20 ");
        
        return sb.toString();
    }

    private String buildGameArgs(MinecraftManager.MinecraftInstance instance) {
        return String.format(
            "--width 1280 --height 720 --fullscreen false --version %s --gameDir \"%s\"",
            instance.getMinecraftVersion(),
            instance.getPath().getAbsolutePath()
        );
    }

    private File findJava() {
        String[] javaPaths = {
            System.getProperty("java.home") + "/bin/java.exe",
            "C:/Program Files/Java/jdk-17/bin/java.exe",
            "C:/Program Files (x86)/Java/jre8/bin/java.exe"
        };

        for (String path : javaPaths) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    private String findLauncherJar() {
        return "libraries/com/mojang/launcher.jar";
    }
}
