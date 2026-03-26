package org.aurora.launcher.core.mirror;

import java.util.HashMap;
import java.util.Map;

public class MirrorManager {
    private static MirrorManager instance;
    
    private final Map<String, MirrorProvider> mirrors = new HashMap<>();
    private String currentMirror = "official";
    
    public interface MirrorProvider {
        String getName();
        String getDescription();
        String transformUrl(String originalUrl);
    }
    
    public MirrorManager() {
        registerDefaultMirrors();
    }
    
    public static synchronized MirrorManager getInstance() {
        if (instance == null) {
            instance = new MirrorManager();
        }
        return instance;
    }
    
    private void registerDefaultMirrors() {
        register("official", new OfficialMirror());
        register("bmclapi", new BMCLAPIMirror());
        register("mcbbs", new MCBBSMirror());
    }
    
    public void register(String id, MirrorProvider mirror) {
        mirrors.put(id, mirror);
    }
    
    public void setCurrentMirror(String mirrorId) {
        if (mirrors.containsKey(mirrorId)) {
            this.currentMirror = mirrorId;
        }
    }
    
    public String getCurrentMirror() {
        return currentMirror;
    }
    
    public String transformUrl(String originalUrl) {
        MirrorProvider mirror = mirrors.get(currentMirror);
        if (mirror != null) {
            return mirror.transformUrl(originalUrl);
        }
        return originalUrl;
    }
    
    public Map<String, MirrorProvider> getAvailableMirrors() {
        return new HashMap<>(mirrors);
    }
    
    public static class OfficialMirror implements MirrorProvider {
        @Override
        public String getName() {
            return "Official";
        }
        
        @Override
        public String getDescription() {
            return "Official sources (requires VPN in China)";
        }
        
        @Override
        public String transformUrl(String originalUrl) {
            return originalUrl;
        }
    }
    
    public static class BMCLAPIMirror implements MirrorProvider {
        private static final String MOJANG_META = "https://piston-meta.mojang.com/";
        private static final String MOJANG_META_MIRROR = "https://bmclapi2.bangbang93.com/";
        private static final String LAUNCHER_META = "https://launchermeta.mojang.com/";
        private static final String LAUNCHER_META_MIRROR = "https://bmclapi2.bangbang93.com/";
        private static final String RESOURCES = "https://resources.download.minecraft.net/";
        private static final String RESOURCES_MIRROR = "https://bmclapi2.bangbang93.com/assets/";
        private static final String LIBRARIES = "https://libraries.minecraft.net/";
        private static final String LIBRARIES_MIRROR = "https://bmclapi2.bangbang93.com/maven/";
        
        @Override
        public String getName() {
            return "BMCLAPI";
        }
        
        @Override
        public String getDescription() {
            return "BMCLAPI mirror (China, fast)";
        }
        
        @Override
        public String transformUrl(String originalUrl) {
            if (originalUrl == null) return null;
            
            if (originalUrl.startsWith(MOJANG_META)) {
                return originalUrl.replace(MOJANG_META, MOJANG_META_MIRROR);
            }
            if (originalUrl.startsWith(LAUNCHER_META)) {
                return originalUrl.replace(LAUNCHER_META, LAUNCHER_META_MIRROR);
            }
            if (originalUrl.startsWith(RESOURCES)) {
                return originalUrl.replace(RESOURCES, RESOURCES_MIRROR);
            }
            if (originalUrl.startsWith(LIBRARIES)) {
                return originalUrl.replace(LIBRARIES, LIBRARIES_MIRROR);
            }
            if (originalUrl.contains("piston-data.mojang.com")) {
                return originalUrl.replace("piston-data.mojang.com", "bmclapi2.bangbang93.com");
            }
            
            return originalUrl;
        }
    }
    
    public static class MCBBSMirror implements MirrorProvider {
        private static final String MOJANG_META = "https://piston-meta.mojang.com/";
        private static final String MOJANG_META_MIRROR = "https://download.mcbbs.net/";
        private static final String LAUNCHER_META = "https://launchermeta.mojang.com/";
        private static final String LAUNCHER_META_MIRROR = "https://download.mcbbs.net/";
        private static final String RESOURCES = "https://resources.download.minecraft.net/";
        private static final String RESOURCES_MIRROR = "https://download.mcbbs.net/assets/";
        private static final String LIBRARIES = "https://libraries.minecraft.net/";
        private static final String LIBRARIES_MIRROR = "https://download.mcbbs.net/maven/";
        
        @Override
        public String getName() {
            return "MCBBS";
        }
        
        @Override
        public String getDescription() {
            return "MCBBS mirror (China, stable)";
        }
        
        @Override
        public String transformUrl(String originalUrl) {
            if (originalUrl == null) return null;
            
            if (originalUrl.startsWith(MOJANG_META)) {
                return originalUrl.replace(MOJANG_META, MOJANG_META_MIRROR);
            }
            if (originalUrl.startsWith(LAUNCHER_META)) {
                return originalUrl.replace(LAUNCHER_META, LAUNCHER_META_MIRROR);
            }
            if (originalUrl.startsWith(RESOURCES)) {
                return originalUrl.replace(RESOURCES, RESOURCES_MIRROR);
            }
            if (originalUrl.startsWith(LIBRARIES)) {
                return originalUrl.replace(LIBRARIES, LIBRARIES_MIRROR);
            }
            if (originalUrl.contains("piston-data.mojang.com")) {
                return originalUrl.replace("piston-data.mojang.com", "download.mcbbs.net");
            }
            
            return originalUrl;
        }
    }
}