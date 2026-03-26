package org.aurora.launcher.ui.service;

import org.aurora.launcher.account.session.SessionManager;
import org.aurora.launcher.account.storage.AccountStorage;
import org.aurora.launcher.core.mirror.MirrorManager;
import org.aurora.launcher.core.path.PathManager;
import org.aurora.launcher.launcher.launch.GameLauncher;
import org.aurora.launcher.launcher.version.VersionManager;
import org.aurora.launcher.modpack.instance.InstanceManager;
import org.aurora.launcher.ui.cache.ImageCache;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ServiceLocator {
    
    private static final Map<Class<?>, Object> services = new HashMap<>();
    private static boolean initialized = false;
    
    private ServiceLocator() {
    }
    
    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        
        MirrorManager.getInstance().setCurrentMirror("bmclapi");
        
        initializePathManager();
        initializeCoreServices();
        registerServices();
    }
    
    private static void initializePathManager() {
        Path basePath = getLauncherBaseDir();
        PathManager.initialize(basePath);
    }
    
    private static void initializeCoreServices() {
        PathManager pathManager = PathManager.getInstance();
        
        VersionManager versionManager = new VersionManager(pathManager.getVersionsDirectory());
        register(VersionManager.class, versionManager);
        
        GameLauncher gameLauncher = new GameLauncher();
        register(GameLauncher.class, gameLauncher);
        
        AccountStorage accountStorage = new AccountStorage(pathManager.getConfigDirectory());
        SessionManager sessionManager = new SessionManager(accountStorage);
        register(SessionManager.class, sessionManager);
    }
    
    private static void registerServices() {
        Path instancesDir = PathManager.getInstance().getInstancesDirectory();
        InstanceManager instanceManager = new InstanceManager(instancesDir);
        register(InstanceManager.class, instanceManager);
        
        VersionService versionService = new VersionService();
        register(VersionService.class, versionService);
        
        VersionDownloadService versionDownloadService = new VersionDownloadService();
        register(VersionDownloadService.class, versionDownloadService);
        
        UnifiedSearchService unifiedSearchService = new UnifiedSearchService();
        register(UnifiedSearchService.class, unifiedSearchService);
        register(ResourceSearchService.class, new ResourceSearchService());
    }
    
    private static Path getLauncherBaseDir() {
        String userHome = System.getProperty("user.home");
        String osName = System.getProperty("os.name").toLowerCase();
        
        Path baseDir;
        if (osName.contains("win")) {
            String appData = System.getenv("APPDATA");
            baseDir = (appData != null) ? Paths.get(appData) : Paths.get(userHome);
        } else if (osName.contains("mac")) {
            baseDir = Paths.get(userHome, "Library", "Application Support");
        } else {
            baseDir = Paths.get(userHome, ".local", "share");
        }
        
        return baseDir.resolve("AuroraLauncher");
    }
    
    public static <T> void register(Class<T> serviceClass, T service) {
        Objects.requireNonNull(serviceClass, "Service class cannot be null");
        Objects.requireNonNull(service, "Service cannot be null");
        services.put(serviceClass, service);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> serviceClass) {
        Objects.requireNonNull(serviceClass, "Service class cannot be null");
        T service = (T) services.get(serviceClass);
        if (service == null) {
            throw new IllegalStateException("Service not registered: " + serviceClass.getName());
        }
        return service;
    }
    
    public static <T> boolean isRegistered(Class<T> serviceClass) {
        return services.containsKey(serviceClass);
    }
    
    public static <T> void unregister(Class<T> serviceClass) {
        services.remove(serviceClass);
    }
    
    public static void clear() {
        services.clear();
    }
    
    public static void shutdown() {
        if (!initialized) return;
        
        if (isRegistered(VersionService.class)) {
            try {
                get(VersionService.class).shutdown();
            } catch (Exception e) {
                System.err.println("Failed to shutdown VersionService: " + e.getMessage());
            }
        }
        
        if (isRegistered(ResourceSearchService.class)) {
            try {
                get(ResourceSearchService.class).shutdown();
            } catch (Exception e) {
                System.err.println("Failed to shutdown ResourceSearchService: " + e.getMessage());
            }
        }

        if (isRegistered(UnifiedSearchService.class)) {
            try {
                get(UnifiedSearchService.class).shutdown();
            } catch (Exception e) {
                System.err.println("Failed to shutdown UnifiedSearchService: " + e.getMessage());
            }
        }
        
        try {
            ImageCache.getInstance().shutdown();
        } catch (Exception e) {
            System.err.println("Failed to shutdown ImageCache: " + e.getMessage());
        }
        
        clear();
        initialized = false;
        System.exit(0);
    }
}