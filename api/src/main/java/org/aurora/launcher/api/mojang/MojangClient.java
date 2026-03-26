package org.aurora.launcher.api.mojang;

import org.aurora.launcher.api.cache.ApiCache;
import org.aurora.launcher.api.common.ApiClient;
import org.aurora.launcher.api.common.RateLimiter;
import org.aurora.launcher.core.mirror.MirrorManager;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MojangClient extends ApiClient {
    
    private static final String API_URL = "https://api.mojang.com";
    private static final String SESSION_URL = "https://sessionserver.mojang.com";
    private static final String LAUNCHER_URL = "https://piston-meta.mojang.com";
    
    public MojangClient() {
        super(API_URL, new ApiCache(), new RateLimiter(600, Duration.ofMinutes(1)));
    }
    
    public MojangClient(ApiCache cache) {
        super(API_URL, cache, new RateLimiter(600, Duration.ofMinutes(1)));
    }
    
    public CompletableFuture<VersionManifest> getVersionManifest() {
        String url = LAUNCHER_URL + "/mc/game/version_manifest_v2.json";
        String mirroredUrl = MirrorManager.getInstance().transformUrl(url);
        return get(mirroredUrl, VersionManifest.class);
    }
    
    public CompletableFuture<VersionInfo> getVersionInfo(String versionId) {
        return getVersionManifest().thenCompose(manifest -> {
            for (VersionInfo info : manifest.getVersions()) {
                if (info.getId().equals(versionId)) {
                    String mirroredUrl = MirrorManager.getInstance().transformUrl(info.getUrl());
                    return get(mirroredUrl, VersionInfo.class);
                }
            }
            return CompletableFuture.completedFuture(null);
        });
    }
    
    public CompletableFuture<MojangProfile> getProfile(String uuid) {
        return get(SESSION_URL + "/session/minecraft/profile/" + uuid, MojangProfile.class);
    }
    
    public CompletableFuture<MojangProfile> getProfileByUsername(String username) {
        return get(API_URL + "/users/profiles/minecraft/" + username, MojangProfile.class);
    }
    
    public CompletableFuture<List<MojangProfile>> getProfiles(List<String> usernames) {
        Type type = new com.google.gson.reflect.TypeToken<List<MojangProfile>>(){}.getType();
        return post(API_URL + "/profiles/minecraft", usernames, type);
    }
    
    public CompletableFuture<List<MojangStatus>> getStatus() {
        Type type = new com.google.gson.reflect.TypeToken<List<MojangStatus>>(){}.getType();
        return get("https://status.mojang.com/check", type);
    }
}