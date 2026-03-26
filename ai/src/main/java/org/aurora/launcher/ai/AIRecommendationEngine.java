package org.aurora.launcher.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AIRecommendationEngine {

    private static final Logger logger = LoggerFactory.getLogger(AIRecommendationEngine.class);
    private static AIRecommendationEngine instance;

    private final Map<String, UserPreference> userProfiles;
    private final Map<String, List<Recommendation>> recommendationsCache;
    private final ExecutorService executor;
    private final String authToken;

    private static final String PROFILE_DIR = System.getProperty("user.home") + "/.aurora/ai";

    private AIRecommendationEngine(String authToken) {
        this.userProfiles = new ConcurrentHashMap<>();
        this.recommendationsCache = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
        this.authToken = authToken;
        loadProfiles();
    }

    public static synchronized AIRecommendationEngine getInstance(String authToken) {
        if (instance == null) {
            instance = new AIRecommendationEngine(authToken);
        }
        return instance;
    }

    public void recordGamePlay(String gameId, int playTimeMinutes) {
        UserPreference profile = getOrCreateProfile();
        profile.gamePlayHistory.merge(gameId, playTimeMinutes, Integer::sum);
        profile.lastUpdated = System.currentTimeMillis();
        saveProfiles();
        
        if (profile.gamePlayHistory.size() > 50) {
            recalculatePreferences();
        }
    }

    public void recordRating(String gameId, int rating) {
        UserPreference profile = getOrCreateProfile();
        profile.ratings.put(gameId, rating);
        profile.lastUpdated = System.currentTimeMillis();
        saveProfiles();
        recalculatePreferences();
    }

    public void recordDownload(String packageId) {
        UserPreference profile = getOrCreateProfile();
        profile.downloadedPackages.add(packageId);
        profile.lastUpdated = System.currentTimeMillis();
        saveProfiles();
    }

    public void getRecommendations(int limit, RecommendationCallback callback) {
        executor.submit(() -> {
            try {
                List<Recommendation> recommendations = new ArrayList<>();
                
                UserPreference profile = getOrCreateProfile();
                Set<String> playedGames = profile.gamePlayHistory.keySet();
                
                String url = "http://localhost:8080/api/packages?limit=100";
                String response = sendGet(url, authToken);
                
                List<PackageInfo> packages = parsePackages(response);
                
                for (PackageInfo pkg : packages) {
                    if (profile.downloadedPackages.contains(pkg.id)) {
                        continue;
                    }
                    
                    double score = calculateRecommendationScore(pkg, profile);
                    if (score > 0.3) {
                        Recommendation rec = new Recommendation();
                        rec.packageId = pkg.id;
                        rec.name = pkg.name;
                        rec.score = score;
                        rec.reason = generateReason(pkg, profile);
                        rec.packageInfo = pkg;
                        recommendations.add(rec);
                    }
                }
                
                recommendations.sort((a, b) -> Double.compare(b.score, a.score));
                
                if (recommendations.size() > limit) {
                    recommendations = recommendations.subList(0, limit);
                }
                
                recommendationsCache.put("latest", recommendations);
                callback.onSuccess(recommendations);
                
            } catch (Exception e) {
                logger.error("Failed to get recommendations", e);
                callback.onError(e.getMessage());
            }
        });
    }

    private double calculateRecommendationScore(PackageInfo pkg, UserPreference profile) {
        double score = 0.0;
        
        Map<String, Double> genreScores = profile.genrePreferences;
        String[] tags = pkg.tags != null ? pkg.tags : new String[0];
        
        for (String tag : tags) {
            score += genreScores.getOrDefault(tag.toLowerCase(), 0.0);
        }
        
        if (pkg.rating > 0) {
            score += (pkg.rating / 5.0) * 0.3;
        }
        
        if (pkg.downloads > 10000) {
            score += 0.1;
        } else if (pkg.downloads > 1000) {
            score += 0.05;
        }
        
        return Math.min(score, 1.0);
    }

    private String generateReason(PackageInfo pkg, UserPreference profile) {
        List<String> reasons = new ArrayList<>();
        
        for (String tag : pkg.tags) {
            if (profile.genrePreferences.getOrDefault(tag.toLowerCase(), 0.0) > 0.5) {
                reasons.add("Similar to " + tag + " games you enjoy");
                break;
            }
        }
        
        if (pkg.rating >= 4.0) {
            reasons.add("Highly rated (" + pkg.rating + "/5)");
        }
        
        if (pkg.downloads > 50000) {
            reasons.add("Popular choice");
        }
        
        if (reasons.isEmpty()) {
            reasons.add("Recommended based on your activity");
        }
        
        return String.join(". ", reasons);
    }

    private void recalculatePreferences() {
        UserPreference profile = getOrCreateProfile();
        
        Map<String, Integer> genreCounts = new HashMap<>();
        int totalPlaytime = profile.gamePlayHistory.values().stream().mapToInt(Integer::intValue).sum();
        
        for (Map.Entry<String, Integer> entry : profile.gamePlayHistory.entrySet()) {
            String gameId = entry.getKey();
            int playTime = entry.getValue();
            
            String url = "http://localhost:8080/api/packages/" + gameId;
            try {
                String response = sendGet(url, authToken);
                String tagsStr = extractString(response, "tags");
                if (!tagsStr.isEmpty()) {
                    for (String tag : tagsStr.split(",")) {
                        String normalizedTag = tag.trim().toLowerCase();
                        int weightedCount = (int) (playTime * 10 / totalPlaytime);
                        genreCounts.merge(normalizedTag, weightedCount, Integer::sum);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to get package info for: " + gameId, e);
            }
        }
        
        int maxCount = genreCounts.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        
        profile.genrePreferences.clear();
        for (Map.Entry<String, Integer> entry : genreCounts.entrySet()) {
            profile.genrePreferences.put(entry.getKey(), (double) entry.getValue() / maxCount);
        }
        
        profile.lastUpdated = System.currentTimeMillis();
        saveProfiles();
        
        logger.info("Recalculated genre preferences for user");
    }

    public void getSimilarUsers(String userId, int limit, UsersCallback callback) {
        executor.submit(() -> {
            try {
                String url = "http://localhost:8080/api/users/" + userId + "/similar?limit=" + limit;
                String response = sendGet(url, authToken);
                List<SimilarUser> users = parseSimilarUsers(response);
                callback.onSuccess(users);
            } catch (Exception e) {
                logger.error("Failed to get similar users", e);
                callback.onError(e.getMessage());
            }
        });
    }

    private UserPreference getOrCreateProfile() {
        return userProfiles.computeIfAbsent("current", k -> new UserPreference());
    }

    private void saveProfiles() {
        try {
            Files.createDirectories(Paths.get(PROFILE_DIR));
            Path profileFile = Paths.get(PROFILE_DIR, "user_profiles.json");
            
            StringBuilder sb = new StringBuilder();
            sb.append("{\"profiles\":{");
            boolean firstProfile = true;
            for (Map.Entry<String, UserPreference> entry : userProfiles.entrySet()) {
                if (!firstProfile) sb.append(",");
                firstProfile = false;
                sb.append("\"").append(entry.getKey()).append("\":{");
                sb.append("\"genrePreferences\":{");
                boolean first = true;
                for (Map.Entry<String, Double> ge : entry.getValue().genrePreferences.entrySet()) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append("\"").append(ge.getKey()).append("\":").append(ge.getValue());
                }
                sb.append("}}");
            }
            sb.append("}}");
            
            Files.writeString(profileFile, sb.toString());
        } catch (IOException e) {
            logger.error("Failed to save profiles", e);
        }
    }

    private void loadProfiles() {
        Path profileFile = Paths.get(PROFILE_DIR, "user_profiles.json");
        if (!Files.exists(profileFile)) return;
        
        try {
            String content = Files.readString(profileFile);
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "\"([^\"]+)\":\\{\"genrePreferences\":\\{([^}]*)\\}\\}");
            java.util.regex.Matcher m = p.matcher(content);
            
            while (m.find()) {
                String key = m.group(1);
                String prefs = m.group(2);
                
                UserPreference profile = new UserPreference();
                java.util.regex.Pattern pp = java.util.regex.Pattern.compile("\"([^\"]+)\":([\\d.]+)");
                java.util.regex.Matcher mm = pp.matcher(prefs);
                
                while (mm.find()) {
                    profile.genrePreferences.put(mm.group(1), Double.parseDouble(mm.group(2)));
                }
                
                userProfiles.put(key, profile);
            }
        } catch (IOException e) {
            logger.error("Failed to load profiles", e);
        }
    }

    private List<PackageInfo> parsePackages(String json) {
        List<PackageInfo> packages = new ArrayList<>();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\\{\"id\":\"([^\"]+)\",\"name\":\"([^\"]+)\",\"rating\":([\\d.]+),\"downloads\":(\\d+)[^}]*\\}");
        java.util.regex.Matcher m = p.matcher(json);
        
        while (m.find()) {
            PackageInfo pkg = new PackageInfo();
            pkg.id = m.group(1);
            pkg.name = m.group(2);
            pkg.rating = Double.parseDouble(m.group(3));
            pkg.downloads = Integer.parseInt(m.group(4));
            packages.add(pkg);
        }
        return packages;
    }

    private List<SimilarUser> parseSimilarUsers(String json) {
        List<SimilarUser> users = new ArrayList<>();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\\{\"id\":\"([^\"]+)\",\"username\":\"([^\"]+)\",\"similarity\":([\\d.]+)\\}");
        java.util.regex.Matcher m = p.matcher(json);
        
        while (m.find()) {
            SimilarUser user = new SimilarUser();
            user.id = m.group(1);
            user.username = m.group(2);
            user.similarity = Double.parseDouble(m.group(3));
            users.add(user);
        }
        return users;
    }

    private String extractString(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private String sendGet(String url, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return new String(conn.getInputStream().readAllBytes());
    }

    public static class UserPreference {
        public Map<String, Integer> gamePlayHistory = new HashMap<>();
        public Map<String, Integer> ratings = new HashMap<>();
        public Set<String> downloadedPackages = new HashSet<>();
        public Map<String, Double> genrePreferences = new HashMap<>();
        public long lastUpdated;
    }

    public static class PackageInfo {
        public String id;
        public String name;
        public double rating;
        public int downloads;
        public String[] tags;
    }

    public static class Recommendation {
        public String packageId;
        public String name;
        public double score;
        public String reason;
        public PackageInfo packageInfo;
    }

    public static class SimilarUser {
        public String id;
        public String username;
        public double similarity;
    }

    public interface RecommendationCallback {
        void onSuccess(List<Recommendation> recommendations);
        void onError(String error);
    }

    public interface UsersCallback {
        void onSuccess(List<SimilarUser> users);
        void onError(String error);
    }
}