package org.aurora.launcher.resource.language;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LanguageFileManager {
    
    public CompletableFuture<List<LanguageFile>> scanModLanguages(Path modFile) {
        return CompletableFuture.supplyAsync(() -> {
            List<LanguageFile> files = new ArrayList<>();
            
            if (!Files.exists(modFile)) {
                return files;
            }
            
            String modId = modFile.getFileName().toString().replaceAll("\\.[^.]+$", "");
            
            try (ZipFile zip = new ZipFile(modFile.toFile())) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    
                    if (name.matches("assets/[^/]+/lang/[a-z]{2}_[a-z]{2}\\.json")) {
                        String langCode = name.replaceAll(".*/([a-z]{2}_[a-z]{2})\\.json", "$1");
                        
                        LanguageFile langFile = new LanguageFile(langCode, modId);
                        langFile.setFilePath(modFile);
                        
                        try (InputStream is = zip.getInputStream(entry)) {
                            loadFromJson(is, langFile);
                        }
                        
                        files.add(langFile);
                    }
                }
            } catch (IOException e) {
                // Return what we have
            }
            
            return files;
        });
    }
    
    public CompletableFuture<List<LanguageFile>> scanResourcePackLanguages(Path packPath) {
        return CompletableFuture.supplyAsync(() -> {
            List<LanguageFile> files = new ArrayList<>();
            
            if (!Files.exists(packPath)) {
                return files;
            }
            
            String packId = packPath.getFileName().toString().replaceAll("\\.[^.]+$", "");
            
            if (Files.isDirectory(packPath)) {
                Path langDir = packPath.resolve("assets");
                if (Files.exists(langDir)) {
                    try (Stream<Path> namespaces = Files.list(langDir)) {
                        namespaces.forEach(namespace -> {
                            Path lang = namespace.resolve("lang");
                            if (Files.exists(lang)) {
                                try (Stream<Path> langFiles = Files.list(lang)) {
                                    langFiles.forEach(file -> {
                                        String name = file.getFileName().toString();
                                        if (name.endsWith(".json")) {
                                            String langCode = name.replace(".json", "");
                                            LanguageFile langFile = new LanguageFile(langCode, packId);
                                            langFile.setFilePath(file);
                                            
                                            try {
                                                loadFromJson(Files.newInputStream(file), langFile);
                                                files.add(langFile);
                                            } catch (Exception e) {
                                                // Skip
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    // Skip
                                }
                            }
                        });
                    } catch (IOException e) {
                        // Return what we have
                    }
                }
            } else if (packPath.toString().endsWith(".zip")) {
                try (ZipFile zip = new ZipFile(packPath.toFile())) {
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        String name = entry.getName();
                        
                        if (name.matches("assets/[^/]+/lang/[a-z]{2}_[a-z]{2}\\.json")) {
                            String langCode = name.replaceAll(".*/([a-z]{2}_[a-z]{2})\\.json", "$1");
                            
                            LanguageFile langFile = new LanguageFile(langCode, packId);
                            langFile.setFilePath(packPath);
                            
                            try (InputStream is = zip.getInputStream(entry)) {
                                loadFromJson(is, langFile);
                            }
                            
                            files.add(langFile);
                        }
                    }
                } catch (IOException e) {
                    // Return what we have
                }
            }
            
            return files;
        });
    }
    
    public CompletableFuture<LanguageFile> load(Path filePath) {
        return CompletableFuture.supplyAsync(() -> {
            LanguageFile langFile = new LanguageFile();
            langFile.setFilePath(filePath);
            
            String fileName = filePath.getFileName().toString();
            langFile.setLanguageCode(fileName.replace(".json", ""));
            
            try (InputStream is = Files.newInputStream(filePath)) {
                loadFromJson(is, langFile);
            } catch (IOException e) {
                // Return empty file
            }
            
            return langFile;
        });
    }
    
    public CompletableFuture<Void> save(LanguageFile file) {
        return CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(file.getFilePath().getParent());
                
                String json = toJson(file);
                Files.write(file.getFilePath(), json.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save language file", e);
            }
        });
    }
    
    public CompletableFuture<LanguageFile> create(String modId, String langCode) {
        return CompletableFuture.supplyAsync(() -> {
            LanguageFile file = new LanguageFile(langCode, modId);
            file.setEntries(new LinkedHashMap<>());
            return file;
        });
    }
    
    public Map<String, LanguageDiff> compare(LanguageFile base, LanguageFile other) {
        Map<String, LanguageDiff> diffs = new LinkedHashMap<>();
        
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(base.getKeys());
        allKeys.addAll(other.getKeys());
        
        for (String key : allKeys) {
            String baseVal = base.get(key);
            String otherVal = other.get(key);
            
            if (baseVal == null && otherVal != null) {
                diffs.put(key, new LanguageDiff(key, LanguageDiff.DiffType.ADDED, null, otherVal));
            } else if (baseVal != null && otherVal == null) {
                diffs.put(key, new LanguageDiff(key, LanguageDiff.DiffType.REMOVED, baseVal, null));
            } else if (baseVal != null && !baseVal.equals(otherVal)) {
                diffs.put(key, new LanguageDiff(key, LanguageDiff.DiffType.MODIFIED, baseVal, otherVal));
            }
        }
        
        return diffs;
    }
    
    private void loadFromJson(InputStream is, LanguageFile file) throws IOException {
        byte[] bytes = readAllBytes(is);
        String json = new String(bytes);
        
        try {
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            
            for (Map.Entry<String, com.google.gson.JsonElement> entry : obj.entrySet()) {
                file.set(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (Exception e) {
            // Invalid JSON
        }
    }
    
    private String toJson(LanguageFile file) {
        com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
        
        for (Map.Entry<String, String> entry : file.getEntries().entrySet()) {
            obj.addProperty(entry.getKey(), entry.getValue());
        }
        
        return new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(obj);
    }
    
    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}