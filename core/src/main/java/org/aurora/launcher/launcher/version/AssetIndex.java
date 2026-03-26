package org.aurora.launcher.launcher.version;

import java.util.Map;

public class AssetIndex {
    private String id;
    private String sha1;
    private long size;
    private long totalSize;
    private String url;
    private Map<String, AssetObject> objects;

    public AssetIndex() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, AssetObject> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, AssetObject> objects) {
        this.objects = objects;
    }
}