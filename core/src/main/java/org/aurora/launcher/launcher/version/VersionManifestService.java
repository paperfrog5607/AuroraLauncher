package org.aurora.launcher.launcher.version;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VersionManifestService {
    private static final String MANIFEST_URL = 
        "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    
    private final OkHttpClient httpClient;
    private final Gson gson;

    public VersionManifestService() {
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new GsonBuilder().create();
    }

    public CompletableFuture<VersionManifest> getManifest() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                    .url(MANIFEST_URL)
                    .get()
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("Failed to fetch manifest: " + response.code());
                    }
                    
                    String body = response.body().string();
                    return parseManifest(body);
                }
            } catch (IOException e) {
                throw new RuntimeException("Network error while fetching manifest", e);
            }
        });
    }

    public CompletableFuture<VersionInfo> getVersionInfo(String versionId) {
        return getManifest().thenApply(manifest -> {
            VersionInfo info = manifest.getVersionById(versionId);
            if (info == null) {
                throw new RuntimeException("Version not found: " + versionId);
            }
            return info;
        });
    }

    public CompletableFuture<List<VersionInfo>> getVersions(VersionType type) {
        return getManifest().thenApply(manifest -> manifest.getVersionsByType(type));
    }

    public CompletableFuture<VersionInfo> getLatestRelease() {
        return getManifest().thenApply(VersionManifest::getLatestRelease);
    }

    public CompletableFuture<VersionInfo> getLatestSnapshot() {
        return getManifest().thenApply(VersionManifest::getLatestSnapshot);
    }

    private VersionManifest parseManifest(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        VersionManifest manifest = new VersionManifest();
        
        List<VersionInfo> versions = new ArrayList<>();
        if (root.has("versions")) {
            for (JsonElement element : root.getAsJsonArray("versions")) {
                JsonObject v = element.getAsJsonObject();
                VersionInfo info = new VersionInfo();
                info.setId(v.get("id").getAsString());
                info.setType(VersionType.fromString(v.get("type").getAsString()));
                info.setUrl(v.has("url") ? v.get("url").getAsString() : null);
                if (v.has("releaseTime")) {
                    info.setReleaseTime(Instant.parse(v.get("releaseTime").getAsString()));
                }
                versions.add(info);
            }
        }
        manifest.setVersions(versions);
        
        if (root.has("latest")) {
            JsonObject latest = root.getAsJsonObject("latest");
            String releaseId = latest.has("release") ? latest.get("release").getAsString() : null;
            String snapshotId = latest.has("snapshot") ? latest.get("snapshot").getAsString() : null;
            
            if (releaseId != null) {
                manifest.setLatestRelease(manifest.getVersionById(releaseId));
            }
            if (snapshotId != null) {
                manifest.setLatestSnapshot(manifest.getVersionById(snapshotId));
            }
        }
        
        return manifest;
    }
}