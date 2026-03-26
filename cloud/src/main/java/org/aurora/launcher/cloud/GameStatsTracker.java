package org.aurora.launcher.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class GameStatsTracker {

    private static final Logger logger = LoggerFactory.getLogger(GameStatsTracker.class);
    private static GameStatsTracker instance;

    private final String authToken;
    private final Map<String, GameSession> activeSessions;
    private final Map<String, GameStats> gameStats;
    private final ExecutorService executor;
    private final Path statsFile;

    private static final String STATS_DIR = System.getProperty("user.home") + "/.aurora/stats";

    private GameStatsTracker(String authToken) {
        this.authToken = authToken;
        this.activeSessions = new ConcurrentHashMap<>();
        this.gameStats = new ConcurrentHashMap<>();
        this.executor = Executors.newScheduledThreadPool(1);
        this.statsFile = Paths.get(STATS_DIR, "game_stats.json");
        loadStats();
    }

    public static synchronized GameStatsTracker getInstance(String authToken) {
        if (instance == null) {
            instance = new GameStatsTracker(authToken);
        }
        return instance;
    }

    public void startSession(String gameId, String gameName) {
        GameSession session = new GameSession();
        session.gameId = gameId;
        session.gameName = gameName;
        session.startTime = System.currentTimeMillis();
        
        activeSessions.put(gameId, session);
        logger.info("Started game session: {}", gameName);
    }

    public void endSession(String gameId) {
        GameSession session = activeSessions.remove(gameId);
        if (session == null) {
            logger.warn("No active session found for game: {}", gameId);
            return;
        }
        
        session.endTime = System.currentTimeMillis();
        session.durationMinutes = (int) ((session.endTime - session.startTime) / 60000);
        
        GameStats stats = gameStats.computeIfAbsent(gameId, k -> new GameStats());
        stats.gameId = gameId;
        stats.gameName = session.gameName;
        stats.totalPlayTime += session.durationMinutes;
        stats.playCount++;
        stats.lastPlayed = session.endTime;
        
        if (session.durationMinutes > stats.longestSession) {
            stats.longestSession = session.durationMinutes;
        }
        
        if (stats.firstPlayed == 0) {
            stats.firstPlayed = session.startTime;
        }
        
        saveStats();
        syncToCloud();
        
        logger.info("Ended game session: {} ({} minutes)", session.gameName, session.durationMinutes);
    }

    public void recordAchievement(String gameId, String achievementId, String achievementName) {
        GameStats stats = gameStats.get(gameId);
        if (stats == null) {
            stats = new GameStats();
            stats.gameId = gameId;
            gameStats.put(gameId, stats);
        }
        
        if (!stats.achievements.contains(achievementId)) {
            stats.achievements.add(achievementId);
            stats.totalAchievements = stats.achievements.size();
        }
        
        saveStats();
    }

    public void getGameStats(String gameId, StatsCallback callback) {
        executor.submit(() -> {
            try {
                GameStats stats = gameStats.get(gameId);
                if (stats != null) {
                    callback.onSuccess(stats);
                } else {
                    String url = "http://localhost:8080/api/stats/game/" + gameId;
                    String response = sendGet(url, authToken);
                    GameStats cloudStats = parseGameStats(response);
                    if (cloudStats != null) {
                        gameStats.put(gameId, cloudStats);
                        callback.onSuccess(cloudStats);
                    } else {
                        callback.onSuccess(new GameStats());
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to get game stats", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void getAllStats(StatsCallback callback) {
        executor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/stats";
                String response = sendGet(url, authToken);
                List<GameStats> cloudStats = parseStatsList(response);
                
                for (GameStats cs : cloudStats) {
                    GameStats local = gameStats.get(cs.gameId);
                    if (local == null || cs.lastPlayed > local.lastPlayed) {
                        gameStats.put(cs.gameId, cs);
                    } else if (local.lastPlayed > cs.lastPlayed) {
                        cs.merge(local);
                    }
                }
                
                List<GameStats> merged = new ArrayList<>(gameStats.values());
                merged.sort((a, b) -> Long.compare(b.lastPlayed, a.lastPlayed));
                callback.onSuccess(merged);
            } catch (Exception e) {
                logger.error("Failed to get all stats", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void getPlayTimeLeaderboard(int limit, LeaderboardCallback callback) {
        executor.submit(() -> {
            try {
                List<GameStats> sorted = new ArrayList<>(gameStats.values());
                sorted.sort((a, b) -> Integer.compare(b.totalPlayTime, a.totalPlayTime));
                
                if (sorted.size() > limit) {
                    sorted = sorted.subList(0, limit);
                }
                
                List<LeaderboardEntry> leaderboard = new ArrayList<>();
                int rank = 1;
                for (GameStats stats : sorted) {
                    LeaderboardEntry entry = new LeaderboardEntry();
                    entry.rank = rank++;
                    entry.gameId = stats.gameId;
                    entry.gameName = stats.gameName;
                    entry.playTime = stats.totalPlayTime;
                    leaderboard.add(entry);
                }
                
                callback.onSuccess(leaderboard);
            } catch (Exception e) {
                logger.error("Failed to get leaderboard", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void getRecentlyPlayed(int limit, RecentlyPlayedCallback callback) {
        executor.submit(() -> {
            List<GameStats> sorted = new ArrayList<>(gameStats.values());
            sorted.sort((a, b) -> Long.compare(b.lastPlayed, a.lastPlayed));
            
            if (sorted.size() > limit) {
                sorted = sorted.subList(0, limit);
            }
            
            List<RecentlyPlayedEntry> recent = new ArrayList<>();
            for (GameStats stats : sorted) {
                RecentlyPlayedEntry entry = new RecentlyPlayedEntry();
                entry.gameId = stats.gameId;
                entry.gameName = stats.gameName;
                entry.totalPlayTime = stats.totalPlayTime;
                entry.lastPlayed = stats.lastPlayed;
                entry.playCount = stats.playCount;
                recent.add(entry);
            }
            
            callback.onSuccess(recent);
        });
    }

    public GameStats getStatsForGame(String gameId) {
        return gameStats.get(gameId);
    }

    public int getTotalPlayTime() {
        return gameStats.values().stream()
            .mapToInt(s -> s.totalPlayTime)
            .sum();
    }

    private void syncToCloud() {
        executor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/stats/batch";
                StringBuilder json = new StringBuilder();
                json.append("{\"stats\":[");
                boolean first = true;
                for (GameStats stats : gameStats.values()) {
                    if (!first) json.append(",");
                    first = false;
                    json.append("{");
                    json.append("\"gameId\":\"").append(stats.gameId).append("\",");
                    json.append("\"gameName\":\"").append(stats.gameName).append("\",");
                    json.append("\"totalPlayTime\":").append(stats.totalPlayTime).append(",");
                    json.append("\"playCount\":").append(stats.playCount).append(",");
                    json.append("\"lastPlayed\":").append(stats.lastPlayed);
                    json.append("}");
                }
                json.append("]}");
                
                sendPost(url, json.toString(), authToken);
                logger.info("Synced stats to cloud");
            } catch (Exception e) {
                logger.error("Failed to sync stats to cloud", e);
            }
        });
    }

    private void saveStats() {
        try {
            Files.createDirectories(Paths.get(STATS_DIR));
            
            StringBuilder sb = new StringBuilder();
            sb.append("{\"games\":[");
            boolean first = true;
            for (GameStats stats : gameStats.values()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("{");
                sb.append("\"gameId\":\"").append(stats.gameId).append("\",");
                sb.append("\"gameName\":\"").append(stats.gameName).append("\",");
                sb.append("\"totalPlayTime\":").append(stats.totalPlayTime).append(",");
                sb.append("\"playCount\":").append(stats.playCount).append(",");
                sb.append("\"lastPlayed\":").append(stats.lastPlayed).append(",");
                sb.append("\"longestSession\":").append(stats.longestSession).append(",");
                sb.append("\"firstPlayed\":").append(stats.firstPlayed).append(",");
                sb.append("\"achievements\":[");
                boolean firstAch = true;
                for (String ach : stats.achievements) {
                    if (!firstAch) sb.append(",");
                    firstAch = false;
                    sb.append("\"").append(ach).append("\"");
                }
                sb.append("]");
                sb.append("}");
            }
            sb.append("]}");
            
            Files.writeString(statsFile, sb.toString());
        } catch (IOException e) {
            logger.error("Failed to save stats", e);
        }
    }

    private void loadStats() {
        if (!Files.exists(statsFile)) return;
        
        try {
            String content = Files.readString(statsFile);
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "\\{\"gameId\":\"([^\"]+)\",\"gameName\":\"([^\"]+)\"," +
                "\"totalPlayTime\":(\\d+),\"playCount\":(\\d+)," +
                "\"lastPlayed\":(\\d+),\"longestSession\":(\\d+)," +
                "\"firstPlayed\":(\\d+),\"achievements\":\\[(.*?)\\]\\}");
            java.util.regex.Matcher m = p.matcher(content);
            
            while (m.find()) {
                GameStats stats = new GameStats();
                stats.gameId = m.group(1);
                stats.gameName = m.group(2);
                stats.totalPlayTime = Integer.parseInt(m.group(3));
                stats.playCount = Integer.parseInt(m.group(4));
                stats.lastPlayed = Long.parseLong(m.group(5));
                stats.longestSession = Integer.parseInt(m.group(6));
                stats.firstPlayed = Long.parseLong(m.group(7));
                
                String achievements = m.group(8);
                if (!achievements.isEmpty()) {
                    for (String ach : achievements.split(",")) {
                        String cleanAch = ach.replace("\"", "").trim();
                        if (!cleanAch.isEmpty()) {
                            stats.achievements.add(cleanAch);
                        }
                    }
                }
                stats.totalAchievements = stats.achievements.size();
                
                gameStats.put(stats.gameId, stats);
            }
        } catch (IOException e) {
            logger.error("Failed to load stats", e);
        }
    }

    private GameStats parseGameStats(String json) {
        GameStats stats = new GameStats();
        stats.gameId = extractString(json, "gameId");
        stats.gameName = extractString(json, "gameName");
        stats.totalPlayTime = extractInt(json, "totalPlayTime");
        stats.playCount = extractInt(json, "playCount");
        stats.lastPlayed = extractLong(json, "lastPlayed");
        return stats;
    }

    private List<GameStats> parseStatsList(String json) {
        List<GameStats> list = new ArrayList<>();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\\{\"gameId\":\"([^\"]+)\",\"gameName\":\"([^\"]+)\"," +
            "\"totalPlayTime\":(\\d+),\"playCount\":(\\d+)," +
            "\"lastPlayed\":(\\d+)\\}");
        java.util.regex.Matcher m = p.matcher(json);
        
        while (m.find()) {
            GameStats stats = new GameStats();
            stats.gameId = m.group(1);
            stats.gameName = m.group(2);
            stats.totalPlayTime = Integer.parseInt(m.group(3));
            stats.playCount = Integer.parseInt(m.group(4));
            stats.lastPlayed = Long.parseLong(m.group(5));
            list.add(stats);
        }
        return list;
    }

    private String sendGet(String url, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return new String(conn.getInputStream().readAllBytes());
    }

    private String sendPost(String url, String json, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setDoOutput(true);
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return new String(conn.getInputStream().readAllBytes());
    }

    private String extractString(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private int extractInt(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*(\\d+)");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private long extractLong(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*(\\d+)");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? Long.parseLong(m.group(1)) : 0;
    }

    public static class GameSession {
        public String gameId;
        public String gameName;
        public long startTime;
        public long endTime;
        public int durationMinutes;
    }

    public static class GameStats {
        public String gameId;
        public String gameName;
        public int totalPlayTime;
        public int playCount;
        public long lastPlayed;
        public int longestSession;
        public long firstPlayed;
        public List<String> achievements = new ArrayList<>();
        public int totalAchievements;
        
        public void merge(GameStats other) {
            if (other.totalPlayTime > this.totalPlayTime) {
                this.totalPlayTime = other.totalPlayTime;
            }
            if (other.playCount > this.playCount) {
                this.playCount = other.playCount;
            }
            this.lastPlayed = Math.max(this.lastPlayed, other.lastPlayed);
        }
    }

    public static class LeaderboardEntry {
        public int rank;
        public String gameId;
        public String gameName;
        public int playTime;
    }

    public static class RecentlyPlayedEntry {
        public String gameId;
        public String gameName;
        public int totalPlayTime;
        public long lastPlayed;
        public int playCount;
    }

    public interface StatsCallback {
        void onSuccess(List<GameStats> stats);
        void onSuccess(GameStats stats);
        void onError(String error);
    }

    public interface LeaderboardCallback {
        void onSuccess(List<LeaderboardEntry> leaderboard);
        void onError(String error);
    }

    public interface RecentlyPlayedCallback {
        void onSuccess(List<RecentlyPlayedEntry> recent);
        void onError(String error);
    }
}