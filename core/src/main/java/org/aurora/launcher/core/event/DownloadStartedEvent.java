package org.aurora.launcher.core.event;

import java.nio.file.Path;

public class DownloadStartedEvent {
    private final String url;
    private final String fileName;

    public DownloadStartedEvent(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public String getUrl() { return url; }
    public String getFileName() { return fileName; }
}