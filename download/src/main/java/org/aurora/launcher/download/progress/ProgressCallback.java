package org.aurora.launcher.download.progress;

import org.aurora.launcher.download.core.DownloadResult;

public interface ProgressCallback {
    void onProgress(ProgressEvent event);
    void onComplete(DownloadResult result);
    void onError(Exception error);
}