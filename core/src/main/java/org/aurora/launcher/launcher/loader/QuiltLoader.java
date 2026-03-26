package org.aurora.launcher.launcher.loader;

import org.aurora.launcher.core.net.HttpClient;
import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuiltLoader implements ModLoader {
    private static final String QUILT_API_URL = "https://meta.quiltmc.org/v3/versions/loader/";
    
    private final HttpClient httpClient;
    private String version;
    private List<Library> libraries;

    public QuiltLoader() {
        this.httpClient = new HttpClient();
        this.libraries = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Quilt";
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
            String url = QUILT_API_URL + mcVersion.getId() + "/" + (version != null ? version : "latest") + "/profile";
            
try {
                Path profilePath = target.resolve("quilt-profile.json");
                httpClient.download(url, profilePath, new org.aurora.launcher.core.net.DownloadOptions());
            } catch (Exception e) {
                throw new RuntimeException("Failed to install Quilt", e);
            }
        });
    }

    @Override
    public boolean isInstalled(Path instanceDir) {
        Path quiltPath = instanceDir.resolve("mods").resolve("quilt-loader");
        return Files.exists(quiltPath);
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
        return "org.quiltmc.loader.impl.launch.knot.KnotClient";
    }

    @Override
    public List<String> getArguments() {
        List<String> args = new ArrayList<>();
        args.add("--quilt");
        return args;
    }
}