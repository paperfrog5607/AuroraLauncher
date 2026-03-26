package org.aurora.launcher.account.auth;

import org.aurora.launcher.account.model.Account;

public class AuthResult {
    private Account account;
    private boolean success;
    private String errorMessage;
    private String errorCode;

    private AuthResult() {
    }

    public static AuthResult success(Account account) {
        AuthResult result = new AuthResult();
        result.account = account;
        result.success = true;
        return result;
    }

    public static AuthResult failure(String errorCode, String message) {
        AuthResult result = new AuthResult();
        result.success = false;
        result.errorCode = errorCode;
        result.errorMessage = message;
        return result;
    }

    public Account getAccount() {
        return account;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}