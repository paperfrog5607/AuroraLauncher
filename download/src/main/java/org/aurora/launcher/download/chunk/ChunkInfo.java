package org.aurora.launcher.download.chunk;

public class ChunkInfo {
    private final int index;
    private final long startByte;
    private final long endByte;
    private long downloadedBytes;
    private ChunkStatus status;

    public ChunkInfo(int index, long startByte, long endByte) {
        this.index = index;
        this.startByte = startByte;
        this.endByte = endByte;
        this.downloadedBytes = 0;
        this.status = ChunkStatus.PENDING;
    }

    public int getIndex() {
        return index;
    }

    public long getStartByte() {
        return startByte;
    }

    public long getEndByte() {
        return endByte;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public ChunkStatus getStatus() {
        return status;
    }

    public void setStatus(ChunkStatus status) {
        this.status = status;
    }

    public long getSize() {
        return endByte - startByte + 1;
    }
}