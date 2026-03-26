package org.aurora.launcher.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

public class ModCompatibilityChecker {

    private static final Logger logger = LoggerFactory.getLogger(ModCompatibilityChecker.class);
    private static ModCompatibilityChecker instance;

    private final Map<String, ModInfo> modCache;
    private final ExecutorService executor;

    private static final String MOD_CACHE_DIR = System.getProperty("user.home") + "/.aurora/mod_cache";

    private ModCompatibilityChecker() {
        this.modCache = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
        loadModCache();
    }

    public static synchronized ModCompatibilityChecker getInstance() {
        if (instance == null) {
            instance = new ModCompatibilityChecker();
        }
        return instance;
    }

    public void checkCompatibility(List<String> modIds, CompatibilityCallback callback) {
        executor.submit(() -> {
            CompatibilityResult result = new CompatibilityResult();
            result.checkedMods = new ArrayList<>();
            result.issues = new ArrayList<>();
            result.warnings = new ArrayList<>();
            
            List<ModInfo> mods = new ArrayList<>();
            for (String modId : modIds) {
                ModInfo mod = getOrLoadModInfo(modId);
                if (mod != null) {
                    mods.add(mod);
                    result.checkedMods.add(mod);
                }
            }
            
            for (int i = 0; i < mods.size(); i++) {
                for (int j = i + 1; j < mods.size(); j++) {
                    checkPairCompatibility(mods.get(i), mods.get(j), result);
                }
            }
            
            checkDuplicateMods(mods, result);
            
            logger.info("Compatibility check complete: {} issues, {} warnings", 
                result.issues.size(), result.warnings.size());
            callback.onSuccess(result);
        });
    }

    private void checkPairCompatibility(ModInfo mod1, ModInfo mod2, CompatibilityResult result) {
        for (String conflict : mod1.conflicts) {
            if (conflict.equals(mod2.id) || conflict.equals(mod2.name)) {
                result.issues.add(new CompatibilityIssue(
                    CompatibilityIssue.Level.ERROR,
                    mod1.name + " conflicts with " + mod2.name,
                    "Remove one of the conflicting mods"
                ));
                return;
            }
        }
        
        for (String dep : mod1.dependencies) {
            if (dep.equals(mod2.id) || dep.equals(mod2.name)) {
                return;
            }
        }
        
        if (!mod1.requiredApi.equals(mod2.requiredApi) && !mod1.requiredApi.isEmpty()) {
            if (!mod2.requiredApi.isEmpty() && !mod1.requiredApi.equals(mod2.requiredApi)) {
                result.warnings.add(new CompatibilityIssue(
                    CompatibilityIssue.Level.WARNING,
                    mod1.name + " may not be compatible with " + mod2.name,
                    "Different API requirements detected"
                ));
            }
        }
        
        if (mod1.mcVersion != null && mod2.mcVersion != null) {
            if (!mod1.mcVersion.equals(mod2.mcVersion)) {
                result.issues.add(new CompatibilityIssue(
                    CompatibilityIssue.Level.ERROR,
                    mod1.name + " requires Minecraft " + mod1.mcVersion + 
                    " but " + mod2.name + " requires " + mod2.mcVersion,
                    "Minecraft version mismatch - use mods for the same version"
                ));
            }
        }
        
        if (mod1.forgeVersion != null && mod2.forgeVersion != null) {
            if (!mod1.forgeVersion.equals(mod2.forgeVersion)) {
                result.warnings.add(new CompatibilityIssue(
                    CompatibilityIssue.Level.WARNING,
                    mod1.name + " requires Forge " + mod1.forgeVersion + 
                    " but " + mod2.name + " requires " + mod2.forgeVersion,
                    "Forge version mismatch"
                ));
            }
        }
    }

    private void checkDuplicateMods(List<ModInfo> mods, CompatibilityResult result) {
        Map<String, List<ModInfo>> byName = new HashMap<>();
        for (ModInfo mod : mods) {
            String key = mod.name.toLowerCase();
            byName.computeIfAbsent(key, k -> new ArrayList<>()).add(mod);
        }
        
        for (Map.Entry<String, List<ModInfo>> entry : byName.entrySet()) {
            if (entry.getValue().size() > 1) {
                result.warnings.add(new CompatibilityIssue(
                    CompatibilityIssue.Level.WARNING,
                    "Multiple versions of " + entry.getValue().get(0).name + " detected",
                    "Keep only one version, remove duplicates"
                ));
            }
        }
    }

    public void analyzeModpack(File modpackZip, AnalysisCallback callback) {
        executor.submit(() -> {
            AnalysisResult result = new AnalysisResult();
            result.mods = new ArrayList<>();
            result.issues = new ArrayList<>();
            result.suggestions = new ArrayList<>();
            
            try (ZipFile zip = new ZipFile(modpackZip)) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName().toLowerCase();
                    
                    if (name.endsWith(".jar") && name.contains("mods")) {
                        ModInfo mod = analyzeJar(zip.getInputStream(entry), entry.getName());
                        if (mod != null) {
                            result.mods.add(mod);
                            modCache.put(mod.id, mod);
                        }
                    }
                }
                
                checkCompatibilityInternal(result.mods, result);
                
            } catch (IOException e) {
                logger.error("Failed to analyze modpack", e);
                callback.onError(e.getMessage());
                return;
            }
            
            if (result.issues.isEmpty()) {
                result.suggestions.add("Modpack looks compatible!");
            }
            
            callback.onSuccess(result);
        });
    }

    private ModInfo analyzeJar(InputStream is, String filename) {
        ModInfo mod = new ModInfo();
        mod.id = filename;
        mod.name = filename.replaceFirst("[.][^.]+$", "")
            .replace("mods/", "")
            .replace(".jar", "");
        
        try {
            byte[] bytes = is.readAllBytes();
            String content = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            
            if (content.contains("mcmod.info")) {
                int start = content.indexOf("mcmod.info");
                int braceStart = content.indexOf("{", start);
                int braceEnd = findMatchingBrace(content, braceStart);
                if (braceStart > 0 && braceEnd > braceStart) {
                    String json = content.substring(braceStart, braceEnd + 1);
                    parseMcmodInfo(mod, json);
                }
            }
            
            if (content.contains("1.12.2")) mod.mcVersion = "1.12.2";
            else if (content.contains("1.16.5")) mod.mcVersion = "1.16.5";
            else if (content.contains("1.18.2")) mod.mcVersion = "1.18.2";
            else if (content.contains("1.19.2")) mod.mcVersion = "1.19.2";
            else if (content.contains("1.20.1")) mod.mcVersion = "1.20.1";
            
        } catch (IOException e) {
            logger.error("Failed to analyze jar: " + filename, e);
        }
        
        return mod;
    }

    private void parseMcmodInfo(ModInfo mod, String json) {
        mod.id = extractString(json, "modid", mod.id);
        mod.name = extractString(json, "name", mod.name);
        mod.version = extractString(json, "version", "");
        mod.description = extractString(json, "description", "");
        mod.mcVersion = extractString(json, "mcversion", mod.mcVersion);
        mod.forgeVersion = extractString(json, "forgeVersion", "");
        
        String authorList = extractString(json, "authorList", "");
        if (!authorList.isEmpty()) {
            mod.author = authorList.split(",")[0].trim();
        }
    }

    private int findMatchingBrace(String content, int start) {
        int depth = 0;
        for (int i = start; i < content.length(); i++) {
            if (content.charAt(i) == '{') depth++;
            else if (content.charAt(i) == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private String extractString(String json, String key, String defaultValue) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : defaultValue;
    }

    private void checkCompatibilityInternal(List<ModInfo> mods, AnalysisResult result) {
        for (int i = 0; i < mods.size(); i++) {
            for (int j = i + 1; j < mods.size(); j++) {
                checkPairCompatibility(mods.get(i), mods.get(j), result);
            }
        }
        checkDuplicateMods(mods, result);
    }

    private ModInfo getOrLoadModInfo(String modId) {
        return modCache.computeIfAbsent(modId, k -> {
            ModInfo mod = new ModInfo();
            mod.id = modId;
            mod.name = modId;
            return mod;
        });
    }

    private void loadModCache() {
        Path cacheFile = Paths.get(MOD_CACHE_DIR, "mod_cache.json");
        if (!Files.exists(cacheFile)) return;
        
        try {
            String content = Files.readString(cacheFile);
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "\"'id': '([^']+)', 'name': '([^']+)'[^}]*\\}");
            java.util.regex.Matcher m = p.matcher(content);
            
            while (m.find()) {
                ModInfo mod = new ModInfo();
                mod.id = m.group(1);
                mod.name = m.group(2);
                modCache.put(mod.id, mod);
            }
        } catch (IOException e) {
            logger.error("Failed to load mod cache", e);
        }
    }

    public static class ModInfo {
        public String id;
        public String name;
        public String version;
        public String description;
        public String mcVersion;
        public String forgeVersion;
        public String requiredApi;
        public String author;
        public List<String> dependencies = new ArrayList<>();
        public List<String> conflicts = new ArrayList<>();
    }

    public static class CompatibilityResult {
        public List<ModInfo> checkedMods;
        public List<CompatibilityIssue> issues;
        public List<CompatibilityIssue> warnings;
    }

    public static class CompatibilityIssue {
        public enum Level { INFO, WARNING, ERROR }
        public Level level;
        public String message;
        public String suggestion;
        
        public CompatibilityIssue(Level level, String message, String suggestion) {
            this.level = level;
            this.message = message;
            this.suggestion = suggestion;
        }
    }

    public static class AnalysisResult extends CompatibilityResult {
        public List<ModInfo> mods;
        public List<String> suggestions;
    }

    public interface CompatibilityCallback {
        void onSuccess(CompatibilityResult result);
        void onError(String error);
    }

    public interface AnalysisCallback {
        void onSuccess(AnalysisResult result);
        void onError(String error);
    }
}