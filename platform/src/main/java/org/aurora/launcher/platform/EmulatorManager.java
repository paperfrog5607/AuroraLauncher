package org.aurora.launcher.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class EmulatorManager {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorManager.class);
    private static EmulatorManager instance;

    private final Map<String, Emulator> configuredEmulators;
    private final Map<String, List<RomGame>> scannedGames;
    private final ExecutorService executor;

    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.aurora/emulators";

    private EmulatorManager() {
        this.configuredEmulators = new ConcurrentHashMap<>();
        this.scannedGames = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
        loadConfig();
    }

    public static synchronized EmulatorManager getInstance() {
        if (instance == null) {
            instance = new EmulatorManager();
        }
        return instance;
    }

    public void addEmulator(Emulator emulator) {
        configuredEmulators.put(emulator.id, emulator);
        saveConfig();
        logger.info("Added emulator: {}", emulator.name);
    }

    public void removeEmulator(String emulatorId) {
        configuredEmulators.remove(emulatorId);
        scannedGames.remove(emulatorId);
        saveConfig();
    }

    public void scanEmulatorRoms(String emulatorId, ScanCallback callback) {
        Emulator emulator = configuredEmulators.get(emulatorId);
        if (emulator == null) {
            callback.onError("Emulator not found: " + emulatorId);
            return;
        }

        executor.submit(() -> {
            List<RomGame> games = new ArrayList<>();
            File romsDir = new File(emulator.romPath);
            
            if (!romsDir.exists() || !romsDir.isDirectory()) {
                callback.onError("ROM directory not found: " + emulator.romPath);
                return;
            }

            String[] extensions = emulator.romExtensions.split(",");
            for (String ext : extensions) {
                File[] roms = romsDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(ext.trim()));
                if (roms != null) {
                    for (File rom : roms) {
                        RomGame game = new RomGame();
                        game.id = emulator.id + ":" + rom.getName();
                        game.emulatorId = emulator.id;
                        game.name = rom.getName().replaceFirst("[.][^.]+$", "");
                        game.filePath = rom.getAbsolutePath();
                        game.fileSize = rom.length();
                        game.platform = emulator.platform;
                        games.add(game);
                    }
                }
            }

            scannedGames.put(emulatorId, games);
            logger.info("Scanned {} ROMs for emulator {}", games.size(), emulator.name);
            callback.onSuccess(games);
        });
    }

    public void scanAllRoms(ScanCallback callback) {
        executor.submit(() -> {
            Map<String, List<RomGame>> allGames = new HashMap<>();
            
            for (String emulatorId : configuredEmulators.keySet()) {
                List<RomGame> games = new ArrayList<>();
                Emulator emulator = configuredEmulators.get(emulatorId);
                File romsDir = new File(emulator.romPath);
                
                if (!romsDir.exists()) continue;

                String[] extensions = emulator.romExtensions.split(",");
                for (String ext : extensions) {
                    File[] roms = romsDir.listFiles((dir, name) -> 
                        name.toLowerCase().endsWith(ext.trim()));
                    if (roms != null) {
                        for (File rom : roms) {
                            RomGame game = new RomGame();
                            game.id = emulator.id + ":" + rom.getName();
                            game.emulatorId = emulator.id;
                            game.name = rom.getName().replaceFirst("[.][^.]+$", "");
                            game.filePath = rom.getAbsolutePath();
                            game.fileSize = rom.length();
                            game.platform = emulator.platform;
                            games.add(game);
                        }
                    }
                }
                allGames.put(emulatorId, games);
            }

            scannedGames.putAll(allGames);
            callback.onSuccess(getAllScannedGames());
        });
    }

    public void launchGame(String gameId, LaunchCallback callback) {
        String[] parts = gameId.split(":", 2);
        if (parts.length != 2) {
            callback.onError("Invalid game ID: " + gameId);
            return;
        }

        String emulatorId = parts[0];
        String romName = parts[1];
        
        Emulator emulator = configuredEmulators.get(emulatorId);
        if (emulator == null) {
            callback.onError("Emulator not found: " + emulatorId);
            return;
        }

        executor.submit(() -> {
            try {
                File romFile = new File(emulator.romPath + "/" + romName);
                if (!romFile.exists()) {
                    callback.onError("ROM file not found: " + romName);
                    return;
                }

                List<String> command = new ArrayList<>();
                command.add(emulator.exePath);
                
                for (String param : emulator.launchParams.split(" ")) {
                    if (param.contains("%ROM%")) {
                        command.add(param.replace("%ROM%", "\"" + romFile.getAbsolutePath() + "\""));
                    } else if (!param.isEmpty()) {
                        command.add(param);
                    }
                }
                
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.start();
                callback.onSuccess();
                logger.info("Launched game: {}", romName);
            } catch (IOException e) {
                logger.error("Failed to launch game", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public List<Emulator> getConfiguredEmulators() {
        return new ArrayList<>(configuredEmulators.values());
    }

    public Emulator getEmulator(String emulatorId) {
        return configuredEmulators.get(emulatorId);
    }

    public List<RomGame> getScannedGames(String emulatorId) {
        return scannedGames.getOrDefault(emulatorId, new ArrayList<>());
    }

    public List<RomGame> getAllScannedGames() {
        List<RomGame> all = new ArrayList<>();
        for (List<RomGame> games : scannedGames.values()) {
            all.addAll(games);
        }
        return all;
    }

    private void saveConfig() {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
            Path configFile = Paths.get(CONFIG_DIR, "emulators.json");
            
            StringBuilder sb = new StringBuilder();
            sb.append("{\"emulators\":[");
            boolean first = true;
            for (Emulator em : configuredEmulators.values()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("{");
                sb.append("\"id\":\"").append(em.id).append("\",");
                sb.append("\"name\":\"").append(em.name).append("\",");
                sb.append("\"platform\":\"").append(em.platform).append("\",");
                sb.append("\"exePath\":\"").append(em.exePath.replace("\\", "\\\\")).append("\",");
                sb.append("\"romPath\":\"").append(em.romPath.replace("\\", "\\\\")).append("\",");
                sb.append("\"romExtensions\":\"").append(em.romExtensions).append("\",");
                sb.append("\"launchParams\":\"").append(em.launchParams).append("\"");
                sb.append("}");
            }
            sb.append("]}");
            
            Files.writeString(configFile, sb.toString());
        } catch (IOException e) {
            logger.error("Failed to save emulator config", e);
        }
    }

    private void loadConfig() {
        Path configFile = Paths.get(CONFIG_DIR, "emulators.json");
        if (!Files.exists(configFile)) return;

        try {
            String content = Files.readString(configFile);
            
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "\\{\"id\":\"([^\"]+)\",\"name\":\"([^\"]+)\",\"platform\":\"([^\"]+)\"," +
                "\"exePath\":\"([^\"]+)\",\"romPath\":\"([^\"]+)\"," +
                "\"romExtensions\":\"([^\"]+)\",\"launchParams\":\"([^\"]+)\"\\}");
            java.util.regex.Matcher matcher = pattern.matcher(content);
            
            while (matcher.find()) {
                Emulator em = new Emulator();
                em.id = matcher.group(1);
                em.name = matcher.group(2);
                em.platform = matcher.group(3);
                em.exePath = matcher.group(4);
                em.romPath = matcher.group(5);
                em.romExtensions = matcher.group(6);
                em.launchParams = matcher.group(7);
                configuredEmulators.put(em.id, em);
            }
        } catch (IOException e) {
            logger.error("Failed to load emulator config", e);
        }
    }

    public static class Emulator {
        public String id;
        public String name;
        public String platform;
        public String exePath;
        public String romPath;
        public String romExtensions;
        public String launchParams;
    }

    public static class RomGame {
        public String id;
        public String emulatorId;
        public String name;
        public String filePath;
        public long fileSize;
        public String platform;
    }

    public interface ScanCallback {
        void onSuccess(List<RomGame> games);
        void onError(String error);
    }

    public interface LaunchCallback {
        void onSuccess();
        void onError(String error);
    }
}