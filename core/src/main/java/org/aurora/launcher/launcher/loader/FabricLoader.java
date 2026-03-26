package org.aurora.launcher.launcher.loader;

import org.aurora.launcher.core.net.HttpClient;
import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FabricLoader implements ModLoader {
    private static final String FABRIC_API_URL = "https://meta.fabricmc.net/v2/versions/loader/";
    
    private final HttpClient httpClient;
    private String version;
    private List<Library> libraries;

    public FabricLoader() {
        this.httpClient = new HttpClient();
        this.libraries = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Fabric";
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public CompletableFuture<Void> install(VersionInfo mcVersion, Path target) {
        return CompletableFuture.runAsync(() -> {
            String url = FABRIC_API_URL + mcVersion.getId() + "/" + (version != null ? version : "latest") + "/profile";
            
            try {
                Path profilePath = target.resolve("fabric-profile.json");
                httpClient.download(url, profilePath, new org.aurora.launcher.core.net.DownloadOptions());
            } catch (Exception e) {
                throw new RuntimeException("Failed to install Fabric", e);
            }
        });
    }

    @Override
    public boolean isInstalled(Path instanceDir) {
        Path fabricPath = instanceDir.resolve("mods").resolve("fabric-loader");
        return Files.exists(fabricPath);
    }

    @Override
    public List<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries != null ? libraries : new ArrayList<>();
    }

    @Override
    public String getMainClass() {
        return "net.fabricmc.loader.impl.launch.knot.KnotClient";
    }

    @Override
    public List<String> getArguments() {
        List<String> args = new ArrayList<>();
        args.add("--fabric");
        return args;
    }
}