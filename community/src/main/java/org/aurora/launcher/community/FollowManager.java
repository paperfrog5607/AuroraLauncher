package org.aurora.launcher.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class FollowManager {

    private static final Logger logger = LoggerFactory.getLogger(FollowManager.class);
    private static FollowManager instance;

    private final Set<String> followedUsers;
    private final Set<String> favoritePackages;
    private final String authToken;
    private final Path cacheFile;

    private FollowManager(String authToken) {
        this.followedUsers = ConcurrentHashMap.newKeySet();
        this.favoritePackages = ConcurrentHashMap.newKeySet();
        this.authToken = authToken;
        this.cacheFile = Paths.get(System.getProperty("user.home"), ".aurora", "social.dat");
        loadCache();
    }

    public static synchronized FollowManager getInstance(String authToken) {
        if (instance == null) {
            instance = new FollowManager(authToken);
        }
        return instance;
    }

    public void followUser(String userId, FollowCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/users/" + userId + "/follow";
                sendPost(url, "", authToken);
                followedUsers.add(userId);
                saveCache();
                callback.onSuccess("Followed user");
            } catch (Exception e) {
                logger.error("Follow failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void unfollowUser(String userId, FollowCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/users/" + userId + "/unfollow";
                sendPost(url, "", authToken);
                followedUsers.remove(userId);
                saveCache();
                callback.onSuccess("Unfollowed user");
            } catch (Exception e) {
                logger.error("Unfollow failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void addFavorite(String packageId, FollowCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/packages/" + packageId + "/favorite";
                sendPost(url, "", authToken);
                favoritePackages.add(packageId);
                saveCache();
                callback.onSuccess("Added to favorites");
            } catch (Exception e) {
                logger.error("Add favorite failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void removeFavorite(String packageId, FollowCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/packages/" + packageId + "/unfavorite";
                sendPost(url, "", authToken);
                favoritePackages.remove(packageId);
                saveCache();
                callback.onSuccess("Removed from favorites");
            } catch (Exception e) {
                logger.error("Remove favorite failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public boolean isFollowing(String userId) {
        return followedUsers.contains(userId);
    }

    public boolean isFavorite(String packageId) {
        return favoritePackages.contains(packageId);
    }

    public Set<String> getFollowedUsers() {
        return new HashSet<>(followedUsers);
    }

    public Set<String> getFavoritePackages() {
        return new HashSet<>(favoritePackages);
    }

    public void getFollowers(String userId, UsersCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/users/" + userId + "/followers";
                String response = sendGet(url, authToken);
                List<UserInfo> users = parseUsers(response);
                callback.onSuccess(users);
            } catch (Exception e) {
                logger.error("Get followers failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getFollowing(String userId, UsersCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/users/" + userId + "/following";
                String response = sendGet(url, authToken);
                List<UserInfo> users = parseUsers(response);
                callback.onSuccess(users);
            } catch (Exception e) {
                logger.error("Get following failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private void saveCache() {
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(cacheFile.toFile()))) {
            dos.writeInt(followedUsers.size());
            for (String userId : followedUsers) {
                dos.writeUTF(userId);
            }
            dos.writeInt(favoritePackages.size());
            for (String pkgId : favoritePackages) {
                dos.writeUTF(pkgId);
            }
        } catch (IOException e) {
            logger.error("Failed to save social cache", e);
        }
    }

    private void loadCache() {
        if (!Files.exists(cacheFile)) return;
        
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(cacheFile.toFile()))) {
            int userCount = dis.readInt();
            for (int i = 0; i < userCount; i++) {
                followedUsers.add(dis.readUTF());
            }
            int pkgCount = dis.readInt();
            for (int i = 0; i < pkgCount; i++) {
                favoritePackages.add(dis.readUTF());
            }
        } catch (IOException e) {
            logger.error("Failed to load social cache", e);
        }
    }

    private List<UserInfo> parseUsers(String json) {
        List<UserInfo> users = new ArrayList<>();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\\{\"id\":\"([^\"]+)\",\"username\":\"([^\"]+)\"[^}]*\\}");
        java.util.regex.Matcher m = p.matcher(json);
        while (m.find()) {
            UserInfo user = new UserInfo();
            user.id = m.group(1);
            user.username = m.group(2);
            users.add(user);
        }
        return users;
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

    private String sendGet(String url, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return new String(conn.getInputStream().readAllBytes());
    }

    public static class UserInfo {
        public String id;
        public String username;
        public String avatar;
    }

    public interface FollowCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface UsersCallback {
        void onSuccess(List<UserInfo> users);
        void onError(String error);
    }
}