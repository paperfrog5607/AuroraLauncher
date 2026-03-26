package org.aurora.launcher.core.net;

import java.util.HashMap;
import java.util.Map;

public class DownloadOptions {
    private int timeout = 30000;
    private int retryCount = 3;
    private boolean overwrite = true;
    private Map<String, String> headers = new HashMap<>();

    public DownloadOptions() {
    }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public boolean isOverwrite() { return overwrite; }
    public void setOverwrite(boolean overwrite) { this.overwrite = overwrite; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public void addHeader(String key, String value) { this.headers.put(key, value); }
}