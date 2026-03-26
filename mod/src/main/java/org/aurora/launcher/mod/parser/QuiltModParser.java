package org.aurora.launcher.mod.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.aurora.launcher.mod.scanner.ModInfo;
import org.aurora.launcher.mod.scanner.Dependency;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class QuiltModParser implements ModParser {
    
    private static final String QUILT_MOD_JSON = "quilt.mod.json";
    private final Gson gson = new Gson();
    
    @Override
    public boolean canParse(Path modFile) {
        if (!modFile.getFileName().toString().endsWith(".jar")) {
            return false;
        }
        
        try (ZipFile zipFile = new ZipFile(modFile.toFile())) {
            return zipFile.getEntry(QUILT_MOD_JSON) != null;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public CompletableFuture<ModInfo> parse(Path modFile) {
        return CompletableFuture.supplyAsync(() -> {
            try (ZipFile zipFile = new ZipFile(modFile.toFile())) {
                ZipEntry entry = zipFile.getEntry(QUILT_MOD_JSON);
                if (entry == null) {
                    throw new RuntimeException("quilt.mod.json not found");
                }
                
                try (InputStreamReader reader = new InputStreamReader(zipFile.getInputStream(entry))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    return parseModInfo(json, modFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse Quilt mod", e);
            }
        });
    }
    
    private ModInfo parseModInfo(JsonObject json, Path filePath) {
        ModInfo info = new ModInfo();
        info.setFilePath(filePath);
        info.setLoader(ModInfo.ModLoader.QUILT);
        
        if (json.has("quilt_loader")) {
            JsonObject loader = json.getAsJsonObject("quilt_loader");
            
            if (loader.has("id")) {
                info.setId(loader.get("id").getAsString());
            }
            if (loader.has("version")) {
                info.setVersion(loader.get("version").getAsString());
            }
            if (loader.has("depends")) {
                for (JsonElement dep : loader.getAsJsonArray("depends")) {
                    if (dep.isJsonObject()) {
                        JsonObject depObj = dep.getAsJsonObject();
                        if (depObj.has("id")) {
                            Dependency dependency = new Dependency(
                                    depObj.get("id").getAsString(),
                                    Dependency.DependencyType.DEPENDS
                            );
                            if (depObj.has("versions")) {
                                dependency.setVersionRange(depObj.get("versions").getAsString());
                            }
                            info.addDependency(dependency);
                        }
                    }
                }
            }
        }
        
        return info;
    }
}