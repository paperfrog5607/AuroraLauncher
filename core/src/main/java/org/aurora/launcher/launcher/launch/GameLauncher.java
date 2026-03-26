package org.aurora.launcher.launcher.launch;

import org.aurora.launcher.core.process.ProcessManager;
import org.aurora.launcher.launcher.java.JavaVersion;
import org.aurora.launcher.launcher.memory.MemoryConfig;
import org.aurora.launcher.launcher.memory.MemoryManager;
import org.aurora.launcher.launcher.profile.GameProfile;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GameLauncher {
    private final ProcessManager processManager;
    private final Map<String, GameProcess> runningProcesses = new HashMap<>();
    private final Map<String, Consumer<String>> logHandlers = new HashMap<>();
    private final Map<String, Consumer<Integer>> exitHandlers = new HashMap<>();

    public GameLauncher() {
        this.processManager = new ProcessManager();
    }

    public CompletableFuture<Process> launch(LaunchProfile profile, LaunchOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateProfile(profile);
                
                List<String> args = buildLaunchArguments(profile, options);
                
                ProcessBuilder pb = new ProcessBuilder(args);
                pb.directory(profile.getGameDir().toFile());
                pb.redirectErrorStream(true);
                
                Map<String, String> env = pb.environment();
                env.put("INST_ID", profile.getInstanceId());
                
                Process process = pb.start();
                
                GameProcess gameProcess = new GameProcess();
                gameProcess.setInstanceId(profile.getInstanceId());
                gameProcess.setProcess(process);
                gameProcess.setStartTime(Instant.now());
                gameProcess.setState(ProcessState.RUNNING);
                
                runningProcesses.put(profile.getInstanceId(), gameProcess);
                
                startLogReader(profile.getInstanceId(), process);
                
                startExitWatcher(profile.getInstanceId(), process, gameProcess);
                
                return process;
            } catch (Exception e) {
                throw new RuntimeException("Failed to launch game", e);
            }
        });
    }

    private void validateProfile(LaunchProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Launch profile is null");
        }
        if (profile.getVersion() == null) {
            throw new IllegalArgumentException("Version info is null");
        }
        if (profile.getJavaVersion() == null) {
            throw new IllegalArgumentException("Java version is null");
        }
        if (profile.getGameDir() == null) {
            throw new IllegalArgumentException("Game directory is null");
        }
    }

    private List<String> buildLaunchArguments(LaunchProfile profile, LaunchOptions options) throws IOException {
        List<String> args = new ArrayList<>();
        
        JavaVersion java = profile.getJavaVersion();
        args.add(java.getJavaExecutable().toString());
        
        Path nativesDir = profile.getGameDir().resolve("natives");
        Files.createDirectories(nativesDir);
        
        Path librariesDir = profile.getGameDir().getParent().resolve("libraries");
        Path assetsDir = profile.getGameDir().getParent().resolve("assets");
        
        LaunchArgumentBuilder argBuilder = new LaunchArgumentBuilder(
            profile.getVersion(),
            profile.getGameConfig(),
            profile.getAccount(),
            profile.getGameDir(),
            librariesDir,
            assetsDir,
            nativesDir,
            profile.getMemoryConfig(),
            options
        );
        
        args.addAll(argBuilder.build());
        
        return args;
    }

    private void startLogReader(String instanceId, Process process) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Consumer<String> handler = logHandlers.get(instanceId);
                    if (handler != null) {
                        handler.accept(line);
                    }
                }
            } catch (IOException ignored) {
            }
        }, "GameLog-" + instanceId).start();
    }

    private void startExitWatcher(String instanceId, Process process, GameProcess gameProcess) {
        new Thread(() -> {
            try {
                process.waitFor();
                gameProcess.setState(ProcessState.STOPPED);
                Consumer<Integer> handler = exitHandlers.get(instanceId);
                if (handler != null) {
                    handler.accept(process.exitValue());
                }
                runningProcesses.remove(instanceId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "GameExit-" + instanceId).start();
    }

    public void kill(String instanceId) {
        GameProcess gameProcess = runningProcesses.get(instanceId);
        if (gameProcess != null && gameProcess.getProcess() != null) {
            gameProcess.getProcess().destroy();
            gameProcess.setState(ProcessState.STOPPED);
        }
    }

    public void killAll() {
        for (String instanceId : new ArrayList<>(runningProcesses.keySet())) {
            kill(instanceId);
        }
    }

    public List<GameProcess> getRunningInstances() {
        return new ArrayList<>(runningProcesses.values());
    }

    public Optional<GameProcess> getInstance(String instanceId) {
        return Optional.ofNullable(runningProcesses.get(instanceId));
    }

    public void setLogHandler(String instanceId, Consumer<String> handler) {
        logHandlers.put(instanceId, handler);
    }

    public void setExitHandler(String instanceId, Consumer<Integer> handler) {
        exitHandlers.put(instanceId, handler);
    }

    public void removeLogHandler(String instanceId) {
        logHandlers.remove(instanceId);
    }

    public void removeExitHandler(String instanceId) {
        exitHandlers.remove(instanceId);
    }
}