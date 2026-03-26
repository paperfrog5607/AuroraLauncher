package org.aurora.launcher.account.session;

import org.aurora.launcher.account.auth.AuthException;
import org.aurora.launcher.account.auth.AuthProvider;
import org.aurora.launcher.account.auth.AuthRequest;
import org.aurora.launcher.account.auth.AuthResult;
import org.aurora.launcher.account.model.Account;
import org.aurora.launcher.account.storage.AccountStorage;
import org.aurora.launcher.account.storage.StorageException;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private AccountStorage accountStorage;
    private Account currentSession;
    private List<SessionListener> listeners;

    public SessionManager(AccountStorage accountStorage) {
        this.accountStorage = accountStorage;
        this.listeners = new ArrayList<>();
    }

    public Account login(String accountId) throws AuthException {
        Account account = accountStorage.getAccount(accountId);
        if (account == null) {
            throw new AuthException("Account not found: " + accountId);
        }
        
        Account oldSession = currentSession;
        currentSession = account;
        account.setLastUsed(java.time.Instant.now());
        
        notifySessionChanged(oldSession, currentSession);
        notifyLoginSuccess(account);
        
        return account;
    }

    public Account loginNew(AuthProvider provider, AuthRequest request) throws AuthException {
        AuthResult result = provider.authenticate(request);
        if (!result.isSuccess()) {
            AuthException ex = new AuthException(result.getErrorMessage());
            notifyLoginFailed(ex);
            throw ex;
        }
        
        Account account = result.getAccount();
        accountStorage.addAccount(account);
        
        Account oldSession = currentSession;
        currentSession = account;
        account.setSelected(true);
        
        try {
            accountStorage.save();
        } catch (StorageException e) {
            throw new AuthException("Failed to save account");
        }
        
        notifySessionChanged(oldSession, currentSession);
        notifyLoginSuccess(account);
        
        return account;
    }

    public void logout(String accountId) {
        Account account = accountStorage.getAccount(accountId);
        if (account != null) {
            account.logout();
            if (currentSession == account) {
                Account oldSession = currentSession;
                currentSession = null;
                notifySessionChanged(oldSession, null);
            }
            notifyLogout(account);
        }
    }

    public void logoutCurrent() {
        if (currentSession != null) {
            logout(currentSession.getId());
        }
    }

    public Account getCurrentSession() {
        return currentSession;
    }

    public void validateSession() throws AuthException {
        if (currentSession == null) {
            throw new AuthException("No active session");
        }
        if (!currentSession.isValid()) {
            notifySessionExpired(currentSession);
            throw new AuthException("Session expired");
        }
    }

    public void refreshSession() throws AuthException {
        if (currentSession != null) {
            currentSession.refresh();
            notifySessionRefreshed(currentSession);
        }
    }

    public List<Account> getStoredAccounts() {
        return accountStorage.getAllAccounts();
    }

    public void switchAccount(String accountId) throws AuthException {
        login(accountId);
    }

    public void addSessionListener(SessionListener listener) {
        listeners.add(listener);
    }

    public void removeSessionListener(SessionListener listener) {
        listeners.remove(listener);
    }

    private void notifySessionChanged(Account oldSession, Account newSession) {
        for (SessionListener listener : listeners) {
            listener.onSessionChanged(oldSession, newSession);
        }
    }

    private void notifySessionRefreshed(Account session) {
        for (SessionListener listener : listeners) {
            listener.onSessionRefreshed(session);
        }
    }

    private void notifySessionExpired(Account session) {
        for (SessionListener listener : listeners) {
            listener.onSessionExpired(session);
        }
    }

    private void notifyLoginSuccess(Account account) {
        for (SessionListener listener : listeners) {
            listener.onLoginSuccess(account);
        }
    }

    private void notifyLoginFailed(AuthException error) {
        for (SessionListener listener : listeners) {
            listener.onLoginFailed(error);
        }
    }

    private void notifyLogout(Account account) {
        for (SessionListener listener : listeners) {
            listener.onLogout(account);
        }
    }
}