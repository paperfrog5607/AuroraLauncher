package org.aurora.launcher.api.mojang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MojangClientTest {

    @Test
    void shouldCreateClient() {
        MojangClient client = new MojangClient();
        
        assertNotNull(client);
        assertNotNull(client.getCache());
    }

    @Test
    void shouldCreateVersionManifest() {
        VersionManifest manifest = new VersionManifest();
        VersionManifest.LatestVersion latest = new VersionManifest.LatestVersion();
        latest.setRelease("1.20.4");
        latest.setSnapshot("24w04a");
        manifest.setLatest(latest);
        
        assertNotNull(manifest);
        assertEquals("1.20.4", manifest.getLatest().getRelease());
        assertEquals("24w04a", manifest.getLatest().getSnapshot());
    }
}