package org.aurora.launcher.account.auth;

import org.aurora.launcher.account.model.Account;
import org.aurora.launcher.account.model.OfflineAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResultTest {

    @Test
    void success_withAccount() {
        OfflineAccount account = new OfflineAccount();
        AuthResult result = AuthResult.success(account);
        
        assertTrue(result.isSuccess());
        assertEquals(account, result.getAccount());
        assertNull(result.getErrorMessage());
        assertNull(result.getErrorCode());
    }

    @Test
    void failure_withError() {
        AuthResult result = AuthResult.failure("AUTH_FAILED", "Authentication failed");
        
        assertFalse(result.isSuccess());
        assertNull(result.getAccount());
        assertEquals("AUTH_FAILED", result.getErrorCode());
        assertEquals("Authentication failed", result.getErrorMessage());
    }
}