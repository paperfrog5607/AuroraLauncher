package org.aurora.launcher.launcher.version;

import org.aurora.launcher.core.platform.Platform;

public class OsRule {
    private String name;

    public OsRule() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean matchesCurrent() {
        if (name == null) {
            return true;
        }
        Platform.OS currentOS = Platform.getOS();
        
        if (name.equals("windows")) {
            return currentOS == Platform.OS.WINDOWS;
        } else if (name.equals("osx")) {
            return currentOS == Platform.OS.MACOS;
        } else if (name.equals("linux")) {
            return currentOS == Platform.OS.LINUX;
        }
        return false;
    }
}