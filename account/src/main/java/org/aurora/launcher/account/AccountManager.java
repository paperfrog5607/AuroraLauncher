package org.aurora.launcher.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountManager {

    private static final Logger logger = LoggerFactory.getLogger(AccountManager.class);
    private static AccountManager instance;

    private User currentUser;
    private String authToken;
    private final CopyOnWriteArrayList<AccountListener> listeners;

    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.aurora";
    private static final String AUTH_FILE = CONFIG_DIR + "/auth.dat";

    private AccountManager() {
        this.listeners = new CopyOnWriteArrayList<>();
        loadAuth();
    }

    public static synchronized AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    public void addListener(AccountListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AccountListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(AccountEvent event) {
        for (AccountListener listener : listeners) {
            listener.onAccountEvent(event);
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null && authToken != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getAuthToken() {
        return authToken;
    }

    public synchronized void login(String email, String password, LoginCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/auth/login";
                String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
                
                String response = sendPost(url, json);
                AuthResponse authResponse = parseAuthResponse(response);
                
                this.authToken = authResponse.token;
                this.currentUser = authResponse.user;
                saveAuth();
                
                notifyListeners(new AccountEvent(AccountEvent.Type.LOGIN_SUCCESS, currentUser));
                callback.onSuccess(currentUser);
            } catch (Exception e) {
                logger.error("Login failed", e);
                notifyListeners(new AccountEvent(AccountEvent.Type.LOGIN_FAILED, null));
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public synchronized void register(String username, String email, String password, RegisterCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/auth/register";
                String json = String.format("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", 
                    username, email, password);
                
                String response = sendPost(url, json);
                AuthResponse authResponse = parseAuthResponse(response);
                
                this.authToken = authResponse.token;
                this.currentUser = authResponse.user;
                saveAuth();
                
                notifyListeners(new AccountEvent(AccountEvent.Type.REGISTER_SUCCESS, currentUser));
                callback.onSuccess(currentUser);
            } catch (Exception e) {
                logger.error("Registration failed", e);
                notifyListeners(new AccountEvent(AccountEvent.Type.REGISTER_FAILED, null));
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public synchronized void logout() {
        this.currentUser = null;
        this.authToken = null;
        deleteAuth();
        notifyListeners(new AccountEvent(AccountEvent.Type.LOGOUT, null));
    }

    public void updateProfile(String username, String avatar, String bio, UpdateCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/user";
                String json = String.format("{\"username\":\"%s\",\"avatar\":\"%s\",\"bio\":\"%s\"}",
                    username, avatar, bio);
                
                String response = sendPut(url, json, authToken);
                User updatedUser = parseUser(response);
                
                this.currentUser = updatedUser;
                saveAuth();
                
                notifyListeners(new AccountEvent(AccountEvent.Type.PROFILE_UPDATED, updatedUser));
                callback.onSuccess(updatedUser);
            } catch (Exception e) {
                logger.error("Profile update failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void refreshCurrentUser() {
        if (!isLoggedIn()) return;
        
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/user/me";
                String response = sendGet(url, authToken);
                User user = parseUser(response);
                this.currentUser = user;
                notifyListeners(new AccountEvent(AccountEvent.Type.PROFILE_UPDATED, user));
            } catch (Exception e) {
                logger.error("Failed to refresh user", e);
            }
        }).start();
    }

    private void saveAuth() {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(AUTH_FILE))) {
                dos.writeUTF(authToken);
                dos.writeUTF(currentUser.getId().toString());
                dos.writeUTF(currentUser.getUsername());
                dos.writeUTF(currentUser.getEmail());
                dos.writeUTF(currentUser.getAvatar() != null ? currentUser.getAvatar() : "");
            }
        } catch (IOException e) {
            logger.error("Failed to save auth", e);
        }
    }

    private void loadAuth() {
        File authFile = new File(AUTH_FILE);
        if (!authFile.exists()) return;
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(authFile))) {
            authToken = dis.readUTF();
            String id = dis.readUTF();
            String username = dis.readUTF();
            String email = dis.readUTF();
            String avatar = dis.readUTF();
            
            currentUser = new User();
            currentUser.setId(java.util.UUID.fromString(id));
            currentUser.setUsername(username);
            currentUser.setEmail(email);
            currentUser.setAvatar(avatar.isEmpty() ? null : avatar);
        } catch (Exception e) {
            logger.error("Failed to load auth", e);
        }
    }

    private void deleteAuth() {
        new File(AUTH_FILE).delete();
    }

    private String sendPost(String url, String json) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return new String(conn.getInputStream().readAllBytes());
    }

    private String sendPut(String url, String json, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("PUT");
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

    private AuthResponse parseAuthResponse(String json) {
        AuthResponse resp = new AuthResponse();
        resp.token = extractJsonString(json, "token");
        resp.user = parseUser(extractJsonObject(json, "user"));
        return resp;
    }

    private User parseUser(String json) {
        User user = new User();
        user.setId(java.util.UUID.fromString(extractJsonString(json, "id")));
        user.setUsername(extractJsonString(json, "username"));
        user.setEmail(extractJsonString(json, "email"));
        user.setAvatar(extractJsonString(json, "avatar"));
        user.setBio(extractJsonString(json, "bio"));
        return user;
    }

    private String extractJsonString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private String extractJsonObject(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return "{}";
        int colon = json.indexOf(":", start);
        int braceStart = json.indexOf("{", colon);
        int braceEnd = json.lastIndexOf("}");
        return json.substring(braceStart, braceEnd + 1);
    }

    public static class User implements Serializable {
        private java.util.UUID id;
        private String username;
        private String email;
        private String avatar;
        private String bio;

        public java.util.UUID getId() { return id; }
        public void setId(java.util.UUID id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }

    public static class AuthResponse {
        String token;
        User user;
    }

    public interface AccountListener {
        void onAccountEvent(AccountEvent event);
    }

    public static class AccountEvent {
        public enum Type { LOGIN_SUCCESS, LOGIN_FAILED, REGISTER_SUCCESS, REGISTER_FAILED, LOGOUT, PROFILE_UPDATED }
        public Type type;
        public User user;
        
        public AccountEvent(Type type, User user) {
            this.type = type;
            this.user = user;
        }
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface RegisterCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UpdateCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}