package org.aurora.launcher.ui.component.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTypeTest {
    
    @Test
    void testAllTypesExist() {
        NotificationType[] types = NotificationType.values();
        assertEquals(4, types.length);
        
        assertTrue(containsType(types, NotificationType.INFO));
        assertTrue(containsType(types, NotificationType.SUCCESS));
        assertTrue(containsType(types, NotificationType.WARNING));
        assertTrue(containsType(types, NotificationType.ERROR));
    }
    
    @Test
    void testValueOf() {
        assertEquals(NotificationType.INFO, NotificationType.valueOf("INFO"));
        assertEquals(NotificationType.SUCCESS, NotificationType.valueOf("SUCCESS"));
        assertEquals(NotificationType.WARNING, NotificationType.valueOf("WARNING"));
        assertEquals(NotificationType.ERROR, NotificationType.valueOf("ERROR"));
    }
    
    private boolean containsType(NotificationType[] types, NotificationType type) {
        for (NotificationType t : types) {
            if (t == type) return true;
        }
        return false;
    }
}