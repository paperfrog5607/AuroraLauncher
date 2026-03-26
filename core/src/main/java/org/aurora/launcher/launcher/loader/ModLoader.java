package org.aurora.launcher.launcher.loader;

import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ModLoader {
    String getName();
    String getVersion();
    CompletableFuture<Void> install(VersionInfo mcVersion, Path target);
    boolean isInstalled(Path instanceDir);
    List<Library> getLibraries();
    String getMainClass();
    List<String> getArguments();
}