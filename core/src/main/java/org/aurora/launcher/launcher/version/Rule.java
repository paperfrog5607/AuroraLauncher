package org.aurora.launcher.launcher.version;

import org.aurora.launcher.core.platform.Platform;

public class Rule {
    private String action;
    private OsRule os;

    public Rule() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public OsRule getOs() {
        return os;
    }

    public void setOs(OsRule os) {
        this.os = os;
    }

    public boolean isAllowed() {
        return "allow".equals(action);
    }

    public boolean matchesCurrentPlatform() {
        if (os == null) {
            return true;
        }
        return os.matchesCurrent();
    }
}