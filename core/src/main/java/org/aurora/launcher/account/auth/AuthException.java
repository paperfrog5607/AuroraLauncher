package org.aurora.launcher.account.auth;

public class AuthException extends Exception {
    private AuthErrorCode errorCode;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public AuthException(AuthErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthErrorCode getErrorCode() {
        return errorCode;
    }
}