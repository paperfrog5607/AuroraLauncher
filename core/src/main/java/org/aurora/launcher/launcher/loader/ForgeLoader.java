package org.aurora.launcher.launcher.loader;

import org.aurora.launcher.core.net.HttpClient;
import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ForgeLoader implements ModLoader {
    private static final String FORGE_MAVEN_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/";
    
    private final HttpClient httpClient;
    private String version;
    private List<Library> libraries;

    public ForgeLoader() {
        this.httpClient = new HttpClient();
        this.libraries = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Forge";
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
            throw new UnsupportedOperationException("Forge installation requires installer jar execution");
        });
    }

    @Override
    public boolean isInstalled(Path instanceDir) {
        Path forgePath = instanceDir.resolve("mods").resolve("forge");
        return Files.exists(forgePath);
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
        return "cpw.mods.modlauncher.Launcher";
    }

    @Override
    public List<String> getArguments() {
        List<String> args = new ArrayList<>();
        args.add("--forge");
        return args;
    }
}