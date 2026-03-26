package org.aurora.launcher.launcher.java;

import org.aurora.launcher.launcher.version.JavaVersionInfo;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class JavaManager {
    private final JavaDetector detector;
    private final List<JavaVersion> installedVersions = new ArrayList<>();

    public JavaManager() {
        this.detector = new JavaDetector();
    }

    public CompletableFuture<List<JavaVersion>> detectInstalled() {
        return detector.detectInstalled().thenApply(versions -> {
            installedVersions.clear();
            installedVersions.addAll(versions);
            return new ArrayList<>(installedVersions);
        });
    }

    public JavaVersion findBest(int minVersion) {
        return installedVersions.stream()
            .filter(v -> v.meets(minVersion))
            .max(Comparator.comparingInt(JavaVersion::getMajorVersion))
            .orElse(null);
    }

    public JavaVersion findBest(JavaVersionInfo requirement) {
        if (requirement == null) {
            return findBest(8);
        }
        return findBest(requirement.getMajorVersion());
    }

    public boolean isCompatible(JavaVersion java, VersionInfo mcVersion) {
        if (java == null || mcVersion == null) {
            return false;
        }
        
        JavaVersionInfo required = mcVersion.getJavaVersion();
        if (required == null) {
            return java.getMajorVersion() >= 8;
        }
        
        return java.meets(required.getMajorVersion());
    }

    public Optional<JavaVersion> getByPath(String path) {
        return installedVersions.stream()
            .filter(v -> path.equals(v.getPath()))
            .findFirst();
    }

    public List<JavaVersion> getInstalledVersions() {
        return new ArrayList<>(installedVersions);
    }

    public void addCustomJava(JavaVersion version) {
        if (version != null && version.getPath() != null) {
            installedVersions.removeIf(v -> version.getPath().equals(v.getPath()));
            installedVersions.add(version);
        }
    }

    public void removeJava(String path) {
        installedVersions.removeIf(v -> path.equals(v.getPath()));
    }
}