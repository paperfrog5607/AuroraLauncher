package org.aurora.launcher.account.storage;

public class StorageException extends Exception {
    private StorageErrorCode errorCode;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(StorageErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public StorageException(StorageErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public StorageException(StorageErrorCode errorCode, Throwable cause) {
        super(errorCode.name(), cause);
        this.errorCode = errorCode;
    }

    public StorageErrorCode getErrorCode() {
        return errorCode;
    }
}