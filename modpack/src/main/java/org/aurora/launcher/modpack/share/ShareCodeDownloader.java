package org.aurora.launcher.modpack.share;

import org.aurora.launcher.modpack.instance.Instance;
import org.aurora.launcher.modpack.instance.InstanceManager;
import org.aurora.launcher.modpack.import_.ImportTask;
import org.aurora.launcher.modpack.import_.Importer;
import org.aurora.launcher.modpack.import_.ModrinthImporter;
import org.aurora.launcher.modpack.import_.CurseForgeImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShareCodeDownloader {
    
    private static final Logger logger = LoggerFactory.getLogger(ShareCodeDownloader.class);
    
    private final ShareCodeParser parser;
    private final InstanceManager instanceManager;
    private final ExecutorService executor;
    private final Map<String, DownloadProgress> activeDownloads;
    
    public ShareCodeDownloader(InstanceManager instanceManager) {
        this.parser = new ShareCodeParser();
        this.instanceManager = instanceManager;
        this.executor = Executors.newCachedThreadPool();
        this.activeDownloads = new ConcurrentHashMap<>();
    }
    
    public CompletableFuture<Instance> download(String shareCode) {
        return download(shareCode, instanceManager.getInstancesDir());
    }
    
    public CompletableFuture<Instance> download(String shareCode, Path targetDir) {
        return CompletableFuture.supplyAsync(() -> {
            ShareCode code = parser.parse(shareCode);
            
            if (code.isExpired()) {
                throw new RuntimeException("Share code has expired: " + shareCode);
            }
            
            String downloadUrl = code.getDownloadUrl();
            if (downloadUrl == null || downloadUrl.isEmpty()) {
                throw new RuntimeException("No download URL available for share code: " + shareCode);
            }
            
            String downloadId = code.getCode();
            DownloadProgress progress = new DownloadProgress(downloadId);
            activeDownloads.put(downloadId, progress);
            
            try {
                progress.setStatus(DownloadStatus.DOWNLOADING);
                progress.setMessage("Downloading modpack...");
                
                Path tempFile = downloadFile(downloadUrl, progress);
                
                progress.setStatus(DownloadStatus.IMPORTING);
                progress.setMessage("Importing modpack...");
                
                Importer importer = createImporter(tempFile);
                
                Instance instance = importer.import_(tempFile, targetDir).join();
                
                Files.deleteIfExists(tempFile);
                
                activeDownloads.remove(downloadId);
                
                logger.info("Successfully downloaded and imported instance from share code: {}", shareCode);
                return instance;
            } catch (Exception e) {
                progress.setStatus(DownloadStatus.FAILED);
                progress.setMessage(e.getMessage());
                activeDownloads.remove(downloadId);
                throw new RuntimeException("Failed to download from share code: " + e.getMessage(), e);
            }
        }, executor);
    }
    
    private Path downloadFile(String url, DownloadProgress progress) throws IOException {
        Path tempFile = Files.createTempFile("modpack-", ".download");
        
        try (InputStream is = new java.net.URL(url).openStream();
             OutputStream os = Files.newOutputStream(tempFile)) {
            
            byte[] buffer = new byte[8192];
            int read;
            long totalRead = 0;
            
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
                totalRead += read;
                progress.setBytesDownloaded(totalRead);
            }
        }
        
        return tempFile;
    }
    
    private Importer createImporter(Path file) {
        ModrinthImporter mrImporter = new ModrinthImporter();
        if (mrImporter.canImport(file)) {
            return mrImporter;
        }
        
        CurseForgeImporter cfImporter = new CurseForgeImporter();
        if (cfImporter.canImport(file)) {
            return cfImporter;
        }
        
        throw new IllegalArgumentException("Unknown modpack format");
    }
    
    public DownloadProgress getDownloadProgress(String downloadId) {
        return activeDownloads.get(downloadId);
    }
    
    public void cancelDownload(String downloadId) {
        DownloadProgress progress = activeDownloads.get(downloadId);
        if (progress != null) {
            progress.setStatus(DownloadStatus.CANCELLED);
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    public enum DownloadStatus {
        PENDING, DOWNLOADING, IMPORTING, COMPLETED, FAILED, CANCELLED
    }
    
    public static class DownloadProgress {
        private final String downloadId;
        private volatile DownloadStatus status;
        private volatile String message;
        private volatile long bytesDownloaded;
        private volatile long totalBytes;
        
        public DownloadProgress(String downloadId) {
            this.downloadId = downloadId;
            this.status = DownloadStatus.PENDING;
        }
        
        public String getDownloadId() { return downloadId; }
        public DownloadStatus getStatus() { return status; }
        public void setStatus(DownloadStatus status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getBytesDownloaded() { return bytesDownloaded; }
        public void setBytesDownloaded(long bytesDownloaded) { this.bytesDownloaded = bytesDownloaded; }
        public long getTotalBytes() { return totalBytes; }
        public void setTotalBytes(long totalBytes) { this.totalBytes = totalBytes; }
        
        public double getProgress() {
            if (totalBytes <= 0) return 0;
            return (double) bytesDownloaded / totalBytes;
        }
    }
}