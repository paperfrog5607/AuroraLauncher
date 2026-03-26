package org.aurora.launcher.launcher.version;

public class AssetObject {
    private String hash;
    private long size;

    public AssetObject() {
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPrefixedPath() {
        if (hash == null || hash.length() < 2) {
            return null;
        }
        return hash.substring(0, 2) + "/" + hash;
    }
}