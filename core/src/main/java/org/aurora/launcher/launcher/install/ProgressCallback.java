package org.aurora.launcher.launcher.install;

public interface ProgressCallback {
    void onProgress(String stage, double progress, long current, long total);
    void onMessage(String message);
    void onError(Exception error);
    void onComplete();
}