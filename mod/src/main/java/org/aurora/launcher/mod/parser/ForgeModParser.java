package org.aurora.launcher.mod.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.aurora.launcher.mod.scanner.ModInfo;
import org.aurora.launcher.mod.scanner.Dependency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ForgeModParser implements ModParser {
    
    private static final String MODS_TOML = "META-INF/mods.toml";
    private final Gson gson = new Gson();
    
    @Override
    public boolean canParse(Path modFile) {
        if (!modFile.getFileName().toString().endsWith(".jar")) {
            return false;
        }
        
        try (ZipFile zipFile = new ZipFile(modFile.toFile())) {
            return zipFile.getEntry(MODS_TOML) != null;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public CompletableFuture<ModInfo> parse(Path modFile) {
        return CompletableFuture.supplyAsync(() -> {
            try (ZipFile zipFile = new ZipFile(modFile.toFile())) {
                ZipEntry entry = zipFile.getEntry(MODS_TOML);
                if (entry == null) {
                    throw new RuntimeException("mods.toml not found");
                }
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)))) {
                    return parseModInfo(reader, modFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse Forge mod", e);
            }
        });
    }
    
    private ModInfo parseModInfo(BufferedReader reader, Path filePath) throws IOException {
        ModInfo info = new ModInfo();
        info.setFilePath(filePath);
        info.setLoader(ModInfo.ModLoader.FORGE);
        
        Map<String, String> properties = new HashMap<>();
        String line;
        StringBuilder currentMod = new StringBuilder();
        boolean inMod = false;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            
            if (line.startsWith("[[mods]]")) {
                inMod = true;
                continue;
            }
            
            if (line.startsWith("[[") && !line.startsWith("[[mods]]")) {
                inMod = false;
                continue;
            }
            
            if (line.startsWith("modId") && inMod) {
                String value = extractValue(line);
                if (value != null) info.setId(value);
            } else if (line.startsWith("version") && inMod) {
                String value = extractValue(line);
                if (value != null) {
                    if (value.startsWith("${") && properties.containsKey("version")) {
                        info.setVersion(properties.get("version"));
                    } else {
                        info.setVersion(value);
                    }
                }
            } else if (line.startsWith("displayName") && inMod) {
                String value = extractValue(line);
                if (value != null) info.setName(value);
            } else if (line.startsWith("description") && inMod) {
                String value = extractValue(line);
                if (value != null) info.setDescription(value.substring(1, value.length() - 1));
            } else if (line.startsWith("author") && inMod) {
                String value = extractValue(line);
                if (value != null) {
                    for (String author : value.split(",")) {
                        info.addAuthor(author.trim());
                    }
                }
            } else if (line.startsWith("license") && inMod) {
                String value = extractValue(line);
                if (value != null) info.setLicense(value);
            }
        }
        
        return info;
    }
    
    private String extractValue(String line) {
        int eqIndex = line.indexOf('=');
        if (eqIndex < 0) return null;
        
        String value = line.substring(eqIndex + 1).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}