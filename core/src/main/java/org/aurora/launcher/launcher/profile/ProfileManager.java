package org.aurora.launcher.launcher.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileManager {
    private final Path profilesPath;
    private final List<GameProfile> profiles = new ArrayList<>();
    private String selectedProfileId;
    private final Gson gson;

    public ProfileManager(Path profilesPath) {
        this.profilesPath = profilesPath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void load() throws IOException {
        if (!Files.exists(profilesPath)) {
            return;
        }
        
        String content = new String(Files.readAllBytes(profilesPath));
        JsonObject root = gson.fromJson(content, JsonObject.class);
        
        profiles.clear();
        
        if (root.has("profiles")) {
            JsonObject profilesObj = root.getAsJsonObject("profiles");
            for (String key : profilesObj.keySet()) {
                JsonObject profileJson = profilesObj.getAsJsonObject(key);
                GameProfile profile = parseProfile(profileJson);
                profiles.add(profile);
            }
        }
        
        if (root.has("selectedProfile")) {
            selectedProfileId = root.get("selectedProfile").getAsString();
        }
    }

    public void save() throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        
        JsonObject profilesObj = new JsonObject();
        for (GameProfile profile : profiles) {
            profilesObj.add(profile.getId(), toJson(profile));
        }
        root.add("profiles", profilesObj);
        
        if (selectedProfileId != null) {
            root.addProperty("selectedProfile", selectedProfileId);
        }
        
        Files.createDirectories(profilesPath.getParent());
        Files.write(profilesPath, gson.toJson(root).getBytes());
    }

    public void addProfile(GameProfile profile) {
        if (profile == null || profile.getId() == null) return;
        
        profiles.removeIf(p -> profile.getId().equals(p.getId()));
        profiles.add(profile);
    }

    public void removeProfile(String profileId) {
        profiles.removeIf(p -> profileId.equals(p.getId()));
        if (profileId.equals(selectedProfileId)) {
            selectedProfileId = profiles.isEmpty() ? null : profiles.get(0).getId();
        }
    }

    public Optional<GameProfile> getProfile(String profileId) {
        return profiles.stream()
            .filter(p -> profileId.equals(p.getId()))
            .findFirst();
    }

    public List<GameProfile> getAllProfiles() {
        return new ArrayList<>(profiles);
    }

    public void selectProfile(String profileId) {
        if (profiles.stream().anyMatch(p -> profileId.equals(p.getId()))) {
            selectedProfileId = profileId;
        }
    }

    public GameProfile getSelectedProfile() {
        if (selectedProfileId == null) {
            return profiles.isEmpty() ? null : profiles.get(0);
        }
        return getProfile(selectedProfileId).orElse(null);
    }

    public String getSelectedProfileId() {
        return selectedProfileId;
    }

    private GameProfile parseProfile(JsonObject json) {
        GameProfile profile = new GameProfile();
        
        profile.setId(json.has("id") ? json.get("id").getAsString() : null);
        profile.setName(json.has("name") ? json.get("name").getAsString() : null);
        profile.setVersionId(json.has("versionId") ? json.get("versionId").getAsString() : null);
        profile.setGameDir(json.has("gameDir") ? Paths.get(json.get("gameDir").getAsString()) : null);
        profile.setJavaPath(json.has("javaPath") ? json.get("javaPath").getAsString() : null);
        profile.setMemoryPreset(json.has("memoryPreset") ? json.get("memoryPreset").getAsString() : "auto");
        profile.setMinMemory(json.has("minMemory") ? json.get("minMemory").getAsLong() : 512);
        profile.setMaxMemory(json.has("maxMemory") ? json.get("maxMemory").getAsLong() : 2048);
        profile.setAutoMemory(!json.has("autoMemory") || json.get("autoMemory").getAsBoolean());
        profile.setLastPlayed(json.has("lastPlayed") ? json.get("lastPlayed").getAsString() : null);
        profile.setIcon(json.has("icon") ? json.get("icon").getAsString() : null);
        
        if (json.has("customJvmArgs")) {
            JsonArray args = json.getAsJsonArray("customJvmArgs");
            List<String> jvmArgs = new ArrayList<>();
            for (int i = 0; i < args.size(); i++) {
                jvmArgs.add(args.get(i).getAsString());
            }
            profile.setCustomJvmArgs(jvmArgs);
        }
        
        if (json.has("customGameArgs")) {
            JsonArray args = json.getAsJsonArray("customGameArgs");
            List<String> gameArgs = new ArrayList<>();
            for (int i = 0; i < args.size(); i++) {
                gameArgs.add(args.get(i).getAsString());
            }
            profile.setCustomGameArgs(gameArgs);
        }
        
        return profile;
    }

    private JsonObject toJson(GameProfile profile) {
        JsonObject json = new JsonObject();
        
        json.addProperty("id", profile.getId());
        json.addProperty("name", profile.getName());
        json.addProperty("versionId", profile.getVersionId());
        if (profile.getGameDir() != null) {
            json.addProperty("gameDir", profile.getGameDir().toString());
        }
        json.addProperty("javaPath", profile.getJavaPath());
        json.addProperty("memoryPreset", profile.getMemoryPreset());
        json.addProperty("minMemory", profile.getMinMemory());
        json.addProperty("maxMemory", profile.getMaxMemory());
        json.addProperty("autoMemory", profile.isAutoMemory());
        json.addProperty("lastPlayed", profile.getLastPlayed());
        json.addProperty("icon", profile.getIcon());
        
        JsonArray jvmArgs = new JsonArray();
        for (String arg : profile.getCustomJvmArgs()) {
            jvmArgs.add(arg);
        }
        json.add("customJvmArgs", jvmArgs);
        
        JsonArray gameArgs = new JsonArray();
        for (String arg : profile.getCustomGameArgs()) {
            gameArgs.add(arg);
        }
        json.add("customGameArgs", gameArgs);
        
        return json;
    }

    public Path getProfilesPath() {
        return profilesPath;
    }
}