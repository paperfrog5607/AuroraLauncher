package org.aurora.launcher.core.process;

import org.aurora.launcher.core.event.GameClosedEvent;
import org.aurora.launcher.core.event.GameStartedEvent;
import org.aurora.launcher.core.event.EventBus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessManager {
    private final Map<String, GameProcess> processes = new ConcurrentHashMap<>();

    public GameProcess start(String name, List<String> command, Path workDir) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(workDir.toFile());
        builder.redirectErrorStream(true);
        
        Process process = builder.start();
        GameProcess gameProcess = new GameProcess(name, process);
        processes.put(name, gameProcess);
        
        EventBus.post(new GameStartedEvent(name));
        
        new Thread(() -> {
            try {
                int exitCode = process.waitFor();
                EventBus.post(new GameClosedEvent(name, exitCode));
                processes.remove(name);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "process-" + name + "-watcher").start();
        
        return gameProcess;
    }

    public void kill(String name) {
        GameProcess process = processes.get(name);
        if (process != null) {
            process.kill();
        }
    }

    public void killAll() {
        processes.values().forEach(GameProcess::kill);
    }

    public List<GameProcess> getRunningProcesses() {
        return new ArrayList<>(processes.values());
    }

    public boolean isRunning(String name) {
        GameProcess process = processes.get(name);
        return process != null && process.isAlive();
    }

    public GameProcess getProcess(String name) {
        return processes.get(name);
    }
}