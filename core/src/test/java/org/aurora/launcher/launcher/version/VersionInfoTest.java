package org.aurora.launcher.launcher.version;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VersionInfoTest {

    @Test
    void constructor_setsDefaultValues() {
        VersionInfo info = new VersionInfo();
        
        assertNull(info.getId());
        assertNull(info.getType());
        assertNull(info.getUrl());
        assertNull(info.getReleaseTime());
    }

    @Test
    void setId_setsId() {
        VersionInfo info = new VersionInfo();
        info.setId("1.20.4");
        
        assertEquals("1.20.4", info.getId());
    }

    @Test
    void setType_setsType() {
        VersionInfo info = new VersionInfo();
        info.setType(VersionType.RELEASE);
        
        assertEquals(VersionType.RELEASE, info.getType());
    }

    @Test
    void setUrl_setsUrl() {
        VersionInfo info = new VersionInfo();
        info.setUrl("https://example.com/version.json");
        
        assertEquals("https://example.com/version.json", info.getUrl());
    }
}