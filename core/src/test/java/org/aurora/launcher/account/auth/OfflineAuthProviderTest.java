package org.aurora.launcher.account.auth;

import org.aurora.launcher.account.model.OfflineAccount;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OfflineAuthProviderTest {

    @Test
    void getName_returnsOffline() {
        OfflineAuthProvider provider = new OfflineAuthProvider();
        assertEquals("Offline", provider.getName());
    }

    @Test
    void isAvailable_returnsTrue() {
        OfflineAuthProvider provider = new OfflineAuthProvider();
        assertTrue(provider.isAvailable());
    }

    @Test
    void authenticate_withUsername_returnsSuccess() throws AuthException {
        OfflineAuthProvider provider = new OfflineAuthProvider();
        AuthRequest request = new AuthRequest();
        request.setUsername("TestPlayer");
        
        AuthResult result = provider.authenticate(request);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getAccount());
        assertEquals("TestPlayer", result.getAccount().getUsername());
        assertNotNull(result.getAccount().getUuid());
    }

    @Test
    void authenticate_generatesConsistentUuid() throws AuthException {
        OfflineAuthProvider provider = new OfflineAuthProvider();
        
        AuthRequest request1 = new AuthRequest();
        request1.setUsername("TestPlayer");
        AuthResult result1 = provider.authenticate(request1);
        
        AuthRequest request2 = new AuthRequest();
        request2.setUsername("TestPlayer");
        AuthResult result2 = provider.authenticate(request2);
        
        assertEquals(result1.getAccount().getUuid(), result2.getAccount().getUuid());
    }

    @Test
    void authenticate_differentUsernames_differentUuids() throws AuthException {
        OfflineAuthProvider provider = new OfflineAuthProvider();
        
        AuthRequest request1 = new AuthRequest();
        request1.setUsername("Player1");
        AuthResult result1 = provider.authenticate(request1);
        
        AuthRequest request2 = new AuthRequest();
        request2.setUsername("Player2");
        AuthResult result2 = provider.authenticate(request2);
        
        assertNotEquals(result1.getAccount().getUuid(), result2.getAccount().getUuid());
    }

    @Test
    void cancel_doesNothing() {
        OfflineAuthProvider provider = new OfflineAuthProvider();
        assertDoesNotThrow(() -> provider.cancel());
    }
}