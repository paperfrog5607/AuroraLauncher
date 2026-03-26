package org.aurora.launcher.launcher.launch;

import org.aurora.launcher.account.model.Account;
import org.aurora.launcher.launcher.memory.MemoryConfig;
import org.aurora.launcher.launcher.profile.GameProfile;
import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaunchArgumentBuilder {
    private final VersionInfo version;
    private final GameProfile profile;
    private final Account account;
    private final Path gameDir;
    private final Path librariesDir;
    private final Path assetsDir;
    private final Path nativesDir;
    private final MemoryConfig memoryConfig;
    private final LaunchOptions options;

    public LaunchArgumentBuilder(VersionInfo version, GameProfile profile, Account account,
                                  Path gameDir, Path librariesDir, Path assetsDir, Path nativesDir,
                                  MemoryConfig memoryConfig, LaunchOptions options) {
        this.version = version;
        this.profile = profile;
        this.account = account;
        this.gameDir = gameDir;
        this.librariesDir = librariesDir;
        this.assetsDir = assetsDir;
        this.nativesDir = nativesDir;
        this.memoryConfig = memoryConfig;
        this.options = options != null ? options : new LaunchOptions();
    }

    public List<String> build() {
        List<String> args = new ArrayList<>();
        
        args.addAll(buildJvmArguments());
        args.add(version.getMainClass());
        args.addAll(buildGameArguments());
        
        return args;
    }

    public List<String> buildJvmArguments() {
        List<String> args = new ArrayList<>();
        
        args.add("-Djava.library.path=" + nativesDir.toString());
        args.add("-Dminecraft.launcher.brand=Aurora-Launcher");
        args.add("-Dminecraft.launcher.version=1.0.0");
        
        if (memoryConfig != null) {
            args.add(memoryConfig.getInitialHeapArgument());
            args.add(memoryConfig.getMaxHeapArgument());
            args.addAll(memoryConfig.getJvmArguments());
        }
        
        if (profile != null && profile.getCustomJvmArgs() != null) {
            args.addAll(profile.getCustomJvmArgs());
        }
        
        args.add("-cp");
        args.add(buildClasspath());
        
        return args;
    }

    public String buildClasspath() {
        List<String> paths = new ArrayList<>();
        
        Path clientJar = gameDir.resolve(version.getId() + ".jar");
        if (Files.exists(clientJar)) {
            paths.add(clientJar.toString());
        }
        
        List<Library> libraries = version.getLibraries();
        if (libraries != null) {
            for (Library library : libraries) {
                if (!library.isAllowedOnCurrentPlatform()) {
                    continue;
                }
                
                String artifactPath = library.getArtifactPath();
                if (artifactPath != null) {
                    Path libPath = librariesDir.resolve(artifactPath);
                    if (Files.exists(libPath)) {
                        paths.add(libPath.toString());
                    }
                }
            }
        }
        
        return String.join(File.pathSeparator, paths);
    }

    public List<String> buildGameArguments() {
        List<String> args = new ArrayList<>();
        
        Map<String, String> variables = buildVariables();
        
        List<String> gameArgs = getGameArguments();
        for (String arg : gameArgs) {
            String replaced = replaceVariables(arg, variables);
            args.add(replaced);
        }
        
        if (options.isFullscreen()) {
            args.add("--fullscreen");
        }
        
        if (options.getWidth() != 854) {
            args.add("--width");
            args.add(String.valueOf(options.getWidth()));
        }
        
        if (options.getHeight() != 480) {
            args.add("--height");
            args.add(String.valueOf(options.getHeight()));
        }
        
        if (options.getServerAddress() != null) {
            args.add("--server");
            args.add(options.getServerAddress());
            args.add("--port");
            args.add(String.valueOf(options.getServerPort()));
        }
        
        if (options.isDemo()) {
            args.add("--demo");
        }
        
        return args;
    }

    private Map<String, String> buildVariables() {
        Map<String, String> vars = new HashMap<>();
        
        vars.put("${auth_player_name}", account != null ? account.getUsername() : "Player");
        vars.put("${version_name}", version.getId());
        vars.put("${game_directory}", gameDir.toString());
        vars.put("${assets_root}", assetsDir.toString());
        vars.put("${assets_index_name}", version.getAssetIndex() != null ? version.getAssetIndex().getId() : "");
        vars.put("${auth_uuid}", account != null ? account.getUuid() : "00000000-0000-0000-0000-000000000000");
        vars.put("${auth_access_token}", account != null ? account.getAccessToken() : "");
        vars.put("${user_type}", "mojang");
        vars.put("${version_type}", version.getType() != null ? version.getType().name().toLowerCase() : "release");
        
        vars.put("${auth_xuid}", "");
        vars.put("${clientid}", "");
        vars.put("${launcher_name}", "Aurora-Launcher");
        vars.put("${launcher_version}", "1.0.0");
        vars.put("${classpath}", buildClasspath());
        vars.put("${classpath_separator}", File.pathSeparator);
        vars.put("${library_directory}", librariesDir.toString());
        
        return vars;
    }

    private String replaceVariables(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private List<String> getGameArguments() {
        List<String> args = new ArrayList<>();
        
        args.add("--username");
        args.add("${auth_player_name}");
        args.add("--version");
        args.add("${version_name}");
        args.add("--gameDir");
        args.add("${game_directory}");
        args.add("--assetsDir");
        args.add("${assets_root}");
        args.add("--assetIndex");
        args.add("${assets_index_name}");
        args.add("--uuid");
        args.add("${auth_uuid}");
        args.add("--accessToken");
        args.add("${auth_access_token}");
        args.add("--userType");
        args.add("${user_type}");
        args.add("--versionType");
        args.add("${version_type}");
        
        return args;
    }
}