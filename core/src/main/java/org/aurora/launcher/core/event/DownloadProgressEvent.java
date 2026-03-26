package org.aurora.launcher.core.event;

public class DownloadProgressEvent {
    private final String fileName;
    private final long current;
    private final long total;

    public DownloadProgressEvent(String fileName, long current, long total) {
        this.fileName = fileName;
        this.current = current;
        this.total = total;
    }

    public String getFileName() { return fileName; }
    public long getCurrent() { return current; }
    public long getTotal() { return total; }
}