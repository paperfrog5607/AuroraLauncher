package org.aurora.launcher.launcher.library;

import org.aurora.launcher.launcher.version.Library;
import org.aurora.launcher.launcher.version.LibraryArtifact;
import org.aurora.launcher.launcher.version.LibraryDownloads;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LibraryDownloader {
    private final Path librariesDir;
    private final org.aurora.launcher.core.net.HttpClient httpClient;

    public LibraryDownloader(Path librariesDir) {
        this.librariesDir = librariesDir;
        this.httpClient = new org.aurora.launcher.core.net.HttpClient();
    }

    public CompletableFuture<Void> download(Library library) {
        return CompletableFuture.runAsync(() -> {
            if (!library.isAllowedOnCurrentPlatform()) {
                return;
            }
            
            LibraryDownloads downloads = library.getDownloads();
            if (downloads == null) return;
            
            LibraryArtifact artifact = downloads.getArtifact();
            if (artifact != null && artifact.getUrl() != null) {
                downloadArtifact(artifact);
            }
        });
    }

    public CompletableFuture<Void> downloadAll(List<Library> libraries) {
        CompletableFuture<?>[] futures = libraries.stream()
            .map(this::download)
            .toArray(CompletableFuture[]::new);
        
        return CompletableFuture.allOf(futures);
    }

    private void downloadArtifact(LibraryArtifact artifact) {
        String url = artifact.getUrl();
        String path = artifact.getPath();
        if (path == null) return;
        
        Path targetPath = librariesDir.resolve(path);
        
        try {
            httpClient.download(url, targetPath, (org.aurora.launcher.core.net.ProgressCallback) null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download library: " + url, e);
        }
    }

    public Path getLibrariesDir() {
        return librariesDir;
    }
}