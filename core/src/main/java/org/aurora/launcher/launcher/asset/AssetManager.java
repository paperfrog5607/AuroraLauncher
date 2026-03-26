package org.aurora.launcher.launcher.asset;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.aurora.launcher.launcher.version.AssetIndex;
import org.aurora.launcher.launcher.version.AssetObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AssetManager {
    private final Path assetsDir;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public AssetManager(Path assetsDir) {
        this.assetsDir = assetsDir;
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    public AssetIndex loadIndex(String indexId) throws IOException {
        Path indexPath = assetsDir.resolve("indexes").resolve(indexId + ".json");
        if (!Files.exists(indexPath)) {
            return null;
        }
        
        String content = new String(Files.readAllBytes(indexPath));
        return parseIndex(content);
    }

    public void saveIndex(AssetIndex index) throws IOException {
        if (index == null || index.getId() == null) return;
        
        Path indexPath = assetsDir.resolve("indexes").resolve(index.getId() + ".json");
        Files.createDirectories(indexPath.getParent());
        
        JsonObject root = new JsonObject();
        root.addProperty("id", index.getId());
        root.addProperty("sha1", index.getSha1());
        root.addProperty("size", index.getSize());
        root.addProperty("totalSize", index.getTotalSize());
        root.addProperty("url", index.getUrl());
        
        if (index.getObjects() != null) {
            JsonObject objects = new JsonObject();
            for (Map.Entry<String, AssetObject> entry : index.getObjects().entrySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("hash", entry.getValue().getHash());
                obj.addProperty("size", entry.getValue().getSize());
                objects.add(entry.getKey(), obj);
            }
            root.add("objects", objects);
        }
        
        Files.write(indexPath, gson.toJson(root).getBytes());
    }

    public AssetIndex downloadIndex(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download asset index: " + response.code());
            }
            
            String content = response.body().string();
            return parseIndex(content);
        }
    }

    private AssetIndex parseIndex(String content) {
        JsonObject root = JsonParser.parseString(content).getAsJsonObject();
        AssetIndex index = new AssetIndex();
        
        index.setId(root.has("id") ? root.get("id").getAsString() : null);
        index.setSha1(root.has("sha1") ? root.get("sha1").getAsString() : null);
        index.setSize(root.has("size") ? root.get("size").getAsLong() : 0);
        index.setTotalSize(root.has("totalSize") ? root.get("totalSize").getAsLong() : 0);
        index.setUrl(root.has("url") ? root.get("url").getAsString() : null);
        
        if (root.has("objects")) {
            Map<String, AssetObject> objects = new HashMap<>();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : root.getAsJsonObject("objects").entrySet()) {
                JsonObject obj = entry.getValue().getAsJsonObject();
                AssetObject asset = new AssetObject();
                asset.setHash(obj.has("hash") ? obj.get("hash").getAsString() : null);
                asset.setSize(obj.has("size") ? obj.get("size").getAsLong() : 0);
                objects.put(entry.getKey(), asset);
            }
            index.setObjects(objects);
        }
        
        return index;
    }

    public boolean isAssetDownloaded(String hash) {
        if (hash == null || hash.length() < 2) return false;
        String prefixedPath = hash.substring(0, 2) + "/" + hash;
        Path assetPath = assetsDir.resolve("objects").resolve(prefixedPath);
        return Files.exists(assetPath);
    }

    public Path getAssetsDir() {
        return assetsDir;
    }
}