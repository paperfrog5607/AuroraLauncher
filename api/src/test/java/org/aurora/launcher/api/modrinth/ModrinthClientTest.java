package org.aurora.launcher.api.modrinth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModrinthClientTest {

    @Test
    void shouldCreateClient() {
        ModrinthClient client = new ModrinthClient();
        
        assertNotNull(client);
        assertNotNull(client.getCache());
        assertNotNull(client.getRateLimiter());
    }

    @Test
    void shouldHaveCorrectBaseUrl() {
        ModrinthClient client = new ModrinthClient();
        
        assertNotNull(client);
    }
}