package org.aurora.launcher.account.auth;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthExceptionTest {

    @Test
    void constructor_withMessage() {
        AuthException ex = new AuthException("Test error");
        assertEquals("Test error", ex.getMessage());
    }

    @Test
    void constructor_withErrorCode() {
        AuthException ex = new AuthException(AuthErrorCode.NETWORK_ERROR);
        assertEquals(AuthErrorCode.NETWORK_ERROR, ex.getErrorCode());
    }

    @Test
    void constructor_withErrorCodeAndMessage() {
        AuthException ex = new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "Invalid password");
        assertEquals(AuthErrorCode.INVALID_CREDENTIALS, ex.getErrorCode());
        assertEquals("Invalid password", ex.getMessage());
    }
}