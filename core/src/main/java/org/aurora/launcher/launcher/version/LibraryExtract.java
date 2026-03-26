package org.aurora.launcher.launcher.version;

import java.util.List;

public class LibraryExtract {
    private List<String> exclude;

    public LibraryExtract() {
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    public boolean shouldExclude(String path) {
        if (exclude == null) return false;
        for (String pattern : exclude) {
            if (path.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }
}