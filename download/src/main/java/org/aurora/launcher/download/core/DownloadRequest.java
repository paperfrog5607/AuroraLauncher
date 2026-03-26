package org.aurora.launcher.download.core;

import org.aurora.launcher.download.config.ProxyConfig;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DownloadRequest {
    private String id;
    private String url;
    private Path targetPath;
    private String expectedSha1;
    private long expectedSize;
    private int maxRetries = 3;
    private int chunkCount = 4;
    private boolean supportResume = true;
    private ProxyConfig proxy;
    private Map<String, String> headers;
    private int timeout = 30000;
    private int priority = 0;

    public DownloadRequest() {
        this.headers = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    public String getExpectedSha1() {
        return expectedSha1;
    }

    public void setExpectedSha1(String expectedSha1) {
        this.expectedSha1 = expectedSha1;
    }

    public long getExpectedSize() {
        return expectedSize;
    }

    public void setExpectedSize(long expectedSize) {
        this.expectedSize = expectedSize;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public boolean isSupportResume() {
        return supportResume;
    }

    public void setSupportResume(boolean supportResume) {
        this.supportResume = supportResume;
    }

    public ProxyConfig getProxy() {
        return proxy;
    }

    public void setProxy(ProxyConfig proxy) {
        this.proxy = proxy;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}