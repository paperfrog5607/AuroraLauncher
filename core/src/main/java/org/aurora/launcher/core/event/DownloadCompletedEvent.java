package org.aurora.launcher.core.event;

import java.nio.file.Path;

public class DownloadCompletedEvent {
    private final String fileName;
    private final Path path;

    public DownloadCompletedEvent(String fileName, Path path) {
        this.fileName = fileName;
        this.path = path;
    }

    public String getFileName() { return fileName; }
    public Path getPath() { return path; }
}