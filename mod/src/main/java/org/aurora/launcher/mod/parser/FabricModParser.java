package org.aurora.launcher.mod.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.aurora.launcher.mod.scanner.ModInfo;
import org.aurora.launcher.mod.scanner.Dependency;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FabricModParser implements ModParser {
    
    private static final String FABRIC_MOD_JSON = "fabric.mod.json";
    private final Gson gson = new Gson();
    
    @Override
    public boolean canParse(Path modFile) {
        if (!modFile.getFileName().toString().endsWith(".jar")) {
            return false;
        }
        
        try (ZipFile zipFile = new ZipFile(modFile.toFile())) {
            return zipFile.getEntry(FABRIC_MOD_JSON) != null;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public CompletableFuture<ModInfo> parse(Path modFile) {
        return CompletableFuture.supplyAsync(() -> {
            try (ZipFile zipFile = new ZipFile(modFile.toFile())) {
                ZipEntry entry = zipFile.getEntry(FABRIC_MOD_JSON);
                if (entry == null) {
                    throw new RuntimeException("fabric.mod.json not found");
                }
                
                try (InputStreamReader reader = new InputStreamReader(zipFile.getInputStream(entry))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    return parseModInfo(json, modFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse Fabric mod", e);
            }
        });
    }
    
    private ModInfo parseModInfo(JsonObject json, Path filePath) {
        ModInfo info = new ModInfo();
        info.setFilePath(filePath);
        info.setLoader(ModInfo.ModLoader.FABRIC);
        
        if (json.has("id")) {
            info.setId(json.get("id").getAsString());
        }
        if (json.has("name")) {
            info.setName(json.get("name").getAsString());
        }
        if (json.has("version")) {
            info.setVersion(json.get("version").getAsString());
        }
        if (json.has("description")) {
            info.setDescription(json.get("description").getAsString());
        }
        if (json.has("contact")) {
            JsonObject contact = json.getAsJsonObject("contact");
            if (contact.has("homepage")) {
                info.setHomepage(contact.get("homepage").getAsString());
            }
            if (contact.has("sources")) {
                info.setSource(contact.get("sources").getAsString());
            }
        }
        if (json.has("authors")) {
            for (JsonElement author : json.getAsJsonArray("authors")) {
                if (author.isJsonPrimitive()) {
                    info.addAuthor(author.getAsString());
                } else if (author.isJsonObject()) {
                    JsonObject authorObj = author.getAsJsonObject();
                    if (authorObj.has("name")) {
                        info.addAuthor(authorObj.get("name").getAsString());
                    }
                }
            }
        }
        if (json.has("license")) {
            if (json.get("license").isJsonArray()) {
                com.google.gson.JsonArray licenses = json.getAsJsonArray("license");
                if (!licenses.isEmpty()) {
                    info.setLicense(licenses.get(0).getAsString());
                }
            } else {
                info.setLicense(json.get("license").getAsString());
            }
        }
        if (json.has("depends")) {
            JsonObject depends = json.getAsJsonObject("depends");
            for (String depId : depends.keySet()) {
                Dependency dep = new Dependency(depId, Dependency.DependencyType.DEPENDS);
                if (depends.get(depId).isJsonPrimitive()) {
                    dep.setVersionRange(depends.get(depId).getAsString());
                }
                info.addDependency(dep);
            }
        }
        if (json.has("breaks")) {
            JsonObject breaks = json.getAsJsonObject("breaks");
            for (String depId : breaks.keySet()) {
                Dependency dep = new Dependency(depId, Dependency.DependencyType.BREAKS);
                if (breaks.get(depId).isJsonPrimitive()) {
                    dep.setVersionRange(breaks.get(depId).getAsString());
                }
                info.addDependency(dep);
            }
        }
        
        return info;
    }
}