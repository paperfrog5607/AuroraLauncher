package org.aurora.launcher.account.auth;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    @Test
    void constructor_defaultValues() {
        AuthRequest request = new AuthRequest();
        
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void setUsername_setsUsername() {
        AuthRequest request = new AuthRequest();
        request.setUsername("TestPlayer");
        
        assertEquals("TestPlayer", request.getUsername());
    }

    @Test
    void setPassword_setsPassword() {
        AuthRequest request = new AuthRequest();
        request.setPassword("password123");
        
        assertEquals("password123", request.getPassword());
    }
}