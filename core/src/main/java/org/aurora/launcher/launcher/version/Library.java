package org.aurora.launcher.launcher.version;

import java.util.List;

public class Library {
    private String name;
    private LibraryDownloads downloads;
    private List<Rule> rules;
    private NativeLibraries natives;
    private LibraryExtract extract;

    public Library() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LibraryDownloads getDownloads() {
        return downloads;
    }

    public void setDownloads(LibraryDownloads downloads) {
        this.downloads = downloads;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public NativeLibraries getNatives() {
        return natives;
    }

    public void setNatives(NativeLibraries natives) {
        this.natives = natives;
    }

    public LibraryExtract getExtract() {
        return extract;
    }

    public void setExtract(LibraryExtract extract) {
        this.extract = extract;
    }

    public boolean isAllowedOnCurrentPlatform() {
        if (rules == null || rules.isEmpty()) {
            return true;
        }
        for (Rule rule : rules) {
            if (rule.matchesCurrentPlatform()) {
                return rule.isAllowed();
            }
        }
        return false;
    }

    public String getArtifactPath() {
        if (name == null) return null;
        String[] parts = name.split(":");
        if (parts.length < 3) return null;
        String group = parts[0].replace(".", "/");
        String artifact = parts[1];
        String version = parts[2];
        return group + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
    }
}