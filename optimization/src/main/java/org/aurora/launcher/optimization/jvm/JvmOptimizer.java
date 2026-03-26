package org.aurora.launcher.optimization.jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JvmOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(JvmOptimizer.class);

    private static JvmOptimizer instance;

    private static final String[][] RECOMMENDED_ARGS = {
        {"-XX:+UseG1GC", "G1垃圾收集器"},
        {"-XX:MaxGCPauseMillis=200", "最大GC停顿时间"},
        {"-XX:+ParallelRefProcEnabled", "并行引用处理"},
        {"-XX:MaxRAMPercentage=50.0", "最大内存使用比例"},
        {"-XX:+AlwaysPreTouch", "预加载内存页面"},
        {"-XX:+DisableExplicitGC", "禁用显式GC"}
    };

    private JvmOptimizer() {}

    public static synchronized JvmOptimizer getInstance() {
        if (instance == null) {
            instance = new JvmOptimizer();
        }
        return instance;
    }

    public List<String> getRecommendedArgs(GameProfile profile) {
        List<String> args = new ArrayList<>();
        
        args.add("-XX:+UseG1GC");
        args.add("-XX:MaxGCPauseMillis=200");
        args.add("-XX:+ParallelRefProcEnabled");
        
        long ram = profile.getRamMB();
        double ramPercentage = ram > 4096 ? 50.0 : 60.0;
        args.add("-XX:MaxRAMPercentage=" + ramPercentage);
        
        if (profile.is64Bit()) {
            args.add("-XX:+AlwaysPreTouch");
        }
        
        args.add("-XX:+DisableExplicitGC");
        
        if (profile.getCpuCores() >= 4) {
            args.add("-XX:+UseStringDeduplication");
        }
        
        return args;
    }

    public String buildJvmArgs(GameProfile profile, Map<String, String> customArgs) {
        StringBuilder sb = new StringBuilder();
        
        for (String arg : getRecommendedArgs(profile)) {
            sb.append(arg).append(" ");
        }
        
        for (Map.Entry<String, String> entry : customArgs.entrySet()) {
            sb.append("-").append(entry.getKey()).append(entry.getValue()).append(" ");
        }
        
        return sb.toString().trim();
    }

    public OptimizationResult optimizeForGame(File gameExe, GameProfile profile) {
        OptimizationResult result = new OptimizationResult();
        
        if (!gameExe.exists()) {
            result.setSuccess(false);
            result.setMessage("Game executable not found");
            return result;
        }
        
        String optimizedArgs = buildJvmArgs(profile, Collections.emptyMap());
        result.setJvmArgs(optimizedArgs);
        result.setSuccess(true);
        result.setMessage("Optimization applied successfully");
        
        logger.info("Applied JVM optimization for {}: {}", gameExe.getName(), optimizedArgs);
        
        return result;
    }

    public boolean validateJvmArgs(String args) {
        if (args == null || args.isEmpty()) {
            return false;
        }
        
        Set<String> validFlags = new HashSet<>(Arrays.asList(
            "-XX:+UseG1GC", "-XX:+UseParallelGC", "-XX:+UseConcMarkSweepGC",
            "-XX:MaxGCPauseMillis=", "-XX:MaxRAMPercentage=",
            "-XX:+AlwaysPreTouch", "-XX:+DisableExplicitGC",
            "-XX:+ParallelRefProcEnabled", "-XX:+UseStringDeduplication"
        ));
        
        for (String token : args.split("\\s+")) {
            if (token.startsWith("-XX:")) {
                boolean valid = false;
                for (String flag : validFlags) {
                    if (token.startsWith(flag)) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    logger.warn("Unknown JVM flag: {}", token);
                    return false;
                }
            }
        }
        
        return true;
    }

    public static class GameProfile {
        private long ramMB;
        private int cpuCores;
        private boolean is64Bit;
        private String gameType;
        
        public long getRamMB() { return ramMB; }
        public void setRamMB(long ramMB) { this.ramMB = ramMB; }
        public int getCpuCores() { return cpuCores; }
        public void setCpuCores(int cpuCores) { this.cpuCores = cpuCores; }
        public boolean is64Bit() { return is64Bit; }
        public void set64Bit(boolean is64Bit) { this.is64Bit = is64Bit; }
        public String getGameType() { return gameType; }
        public void setGameType(String gameType) { this.gameType = gameType; }
    }

    public static class OptimizationResult {
        private boolean success;
        private String message;
        private String jvmArgs;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getJvmArgs() { return jvmArgs; }
        public void setJvmArgs(String jvmArgs) { this.jvmArgs = jvmArgs; }
    }
}