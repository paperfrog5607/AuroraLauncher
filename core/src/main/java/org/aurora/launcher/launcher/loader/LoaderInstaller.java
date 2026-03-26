package org.aurora.launcher.launcher.loader;

import org.aurora.launcher.launcher.version.VersionInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoaderInstaller {
    private final List<ModLoader> availableLoaders = new ArrayList<>();

    public LoaderInstaller() {
        availableLoaders.add(new FabricLoader());
        availableLoaders.add(new ForgeLoader());
        availableLoaders.add(new QuiltLoader());
    }

    public CompletableFuture<Void> install(ModLoader loader, VersionInfo mcVersion, Path target) {
        if (loader == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("Loader is null"));
            return failed;
        }
        return loader.install(mcVersion, target);
    }

    public CompletableFuture<Void> installFabric(VersionInfo mcVersion, Path target) {
        return install(getLoader("Fabric"), mcVersion, target);
    }

    public CompletableFuture<Void> installForge(VersionInfo mcVersion, Path target) {
        return install(getLoader("Forge"), mcVersion, target);
    }

    public CompletableFuture<Void> installQuilt(VersionInfo mcVersion, Path target) {
        return install(getLoader("Quilt"), mcVersion, target);
    }

    public ModLoader getLoader(String name) {
        for (ModLoader loader : availableLoaders) {
            if (loader.getName().equalsIgnoreCase(name)) {
                return loader;
            }
        }
        return null;
    }

    public List<ModLoader> getAvailableLoaders() {
        return new ArrayList<>(availableLoaders);
    }

    public List<String> getLoaderNames() {
        List<String> names = new ArrayList<>();
        for (ModLoader loader : availableLoaders) {
            names.add(loader.getName());
        }
        return names;
    }

    public boolean isLoaderInstalled(String name, Path instanceDir) {
        ModLoader loader = getLoader(name);
        if (loader == null) return false;
        return loader.isInstalled(instanceDir);
    }
}