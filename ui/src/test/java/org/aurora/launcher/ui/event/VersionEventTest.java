package org.aurora.launcher.ui.event;

import org.aurora.launcher.launcher.version.VersionInfo;
import org.aurora.launcher.launcher.version.VersionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionEventTest {
    
    @Test
    void testDownloadEvent() {
        VersionInfo version = new VersionInfo();
        version.setId("1.20.4");
        
        VersionEvent event = new VersionEvent(VersionEvent.DOWNLOAD, version);
        
        assertEquals(VersionEvent.DOWNLOAD, event.getEventType());
        assertSame(version, event.getVersion());
    }
    
    @Test
    void testInstallFabricEvent() {
        VersionInfo version = new VersionInfo();
        version.setId("1.20.4");
        
        VersionEvent event = new VersionEvent(VersionEvent.INSTALL_FABRIC, version);
        
        assertEquals(VersionEvent.INSTALL_FABRIC, event.getEventType());
        assertSame(version, event.getVersion());
    }
    
    @Test
    void testInstallForgeEvent() {
        VersionInfo version = new VersionInfo();
        version.setId("1.20.4");
        
        VersionEvent event = new VersionEvent(VersionEvent.INSTALL_FORGE, version);
        
        assertEquals(VersionEvent.INSTALL_FORGE, event.getEventType());
        assertSame(version, event.getVersion());
    }
}