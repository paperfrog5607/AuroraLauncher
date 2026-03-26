package org.aurora.launcher.core.event;

public class DownloadFailedEvent {
    private final String fileName;
    private final Exception error;

    public DownloadFailedEvent(String fileName, Exception error) {
        this.fileName = fileName;
        this.error = error;
    }

    public String getFileName() { return fileName; }
    public Exception getError() { return error; }
}