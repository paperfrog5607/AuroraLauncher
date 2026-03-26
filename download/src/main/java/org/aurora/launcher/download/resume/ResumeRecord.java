package org.aurora.launcher.download.resume;

import org.aurora.launcher.download.chunk.ChunkInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ResumeRecord {
    private String id;
    private String url;
    private String targetPath;
    private long totalSize;
    private long downloadedSize;
    private List<ChunkData> chunks;
    private long createdTime;
    private long lastModified;
    private String tempFilePath;

    public ResumeRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Path getTargetPath() {
        return targetPath != null ? Paths.get(targetPath) : null;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath != null ? targetPath.toString() : null;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public List<ChunkInfo> getChunks() {
        if (chunks == null) return null;
        List<ChunkInfo> result = new ArrayList<>();
        for (ChunkData data : chunks) {
            result.add(data.toChunkInfo());
        }
        return result;
    }

    public void setChunks(List<ChunkInfo> chunks) {
        if (chunks == null) {
            this.chunks = null;
            return;
        }
        this.chunks = new ArrayList<>();
        for (ChunkInfo info : chunks) {
            this.chunks.add(new ChunkData(info));
        }
    }

    public Instant getCreatedTime() {
        return createdTime > 0 ? Instant.ofEpochMilli(createdTime) : null;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime != null ? createdTime.toEpochMilli() : 0;
    }

    public Instant getLastModified() {
        return lastModified > 0 ? Instant.ofEpochMilli(lastModified) : null;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified != null ? lastModified.toEpochMilli() : 0;
    }

    public Path getTempFilePath() {
        return tempFilePath != null ? Paths.get(tempFilePath) : null;
    }

    public void setTempFilePath(Path tempFilePath) {
        this.tempFilePath = tempFilePath != null ? tempFilePath.toString() : null;
    }

    public static class ChunkData {
        private int index;
        private long startByte;
        private long endByte;
        private long downloadedBytes;
        private String status;

        public ChunkData() {
        }

        public ChunkData(ChunkInfo info) {
            this.index = info.getIndex();
            this.startByte = info.getStartByte();
            this.endByte = info.getEndByte();
            this.downloadedBytes = info.getDownloadedBytes();
            this.status = info.getStatus().name();
        }

        public ChunkInfo toChunkInfo() {
            ChunkInfo info = new ChunkInfo(index, startByte, endByte);
            info.setDownloadedBytes(downloadedBytes);
            info.setStatus(org.aurora.launcher.download.chunk.ChunkStatus.valueOf(status));
            return info;
        }
    }
}