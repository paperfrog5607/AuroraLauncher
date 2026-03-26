package org.aurora.launcher.account.session;

import org.aurora.launcher.account.auth.AuthException;
import org.aurora.launcher.account.model.Account;

public interface SessionListener {
    void onSessionChanged(Account oldSession, Account newSession);
    void onSessionRefreshed(Account session);
    void onSessionExpired(Account session);
    void onLoginSuccess(Account account);
    void onLoginFailed(AuthException error);
    void onLogout(Account account);
}