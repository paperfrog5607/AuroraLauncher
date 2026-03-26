package org.aurora.launcher.launcher.version;

import java.util.Map;

public class LibraryDownloads {
    private LibraryArtifact artifact;
    private Map<String, LibraryArtifact> classifiers;

    public LibraryDownloads() {
    }

    public LibraryArtifact getArtifact() {
        return artifact;
    }

    public void setArtifact(LibraryArtifact artifact) {
        this.artifact = artifact;
    }

    public Map<String, LibraryArtifact> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(Map<String, LibraryArtifact> classifiers) {
        this.classifiers = classifiers;
    }
}