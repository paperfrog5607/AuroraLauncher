package org.aurora.launcher.account.auth;

public interface AuthProvider {
    String getName();
    boolean isAvailable();
    AuthResult authenticate(AuthRequest request) throws AuthException;
    void cancel();
}