package org.aurora.launcher.launcher.version;

import java.util.Map;

public class NativeLibraries {
    private Map<String, String> classifiers;

    public NativeLibraries() {
    }

    public Map<String, String> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(Map<String, String> classifiers) {
        this.classifiers = classifiers;
    }

    public String getClassifierForCurrentOs() {
        if (classifiers == null) return null;
        
        String osKey = getOsKey();
        return classifiers.get(osKey);
    }

    private String getOsKey() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return "windows";
        } else if (osName.contains("mac")) {
            return "osx";
        } else {
            return "linux";
        }
    }
}