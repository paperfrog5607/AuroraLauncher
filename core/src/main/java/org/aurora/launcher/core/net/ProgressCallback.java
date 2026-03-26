package org.aurora.launcher.core.net;

public interface ProgressCallback {
    void onProgress(long current, long total);
}