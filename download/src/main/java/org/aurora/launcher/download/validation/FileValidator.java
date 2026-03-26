package org.aurora.launcher.download.validation;

import org.aurora.launcher.download.core.DownloadRequest;

import java.nio.file.Path;

public interface FileValidator {
    boolean validate(Path file, DownloadRequest request);
    String getValidationError();
}