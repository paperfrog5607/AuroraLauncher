package org.aurora.launcher.ui.event;

import org.aurora.launcher.modpack.instance.Instance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstanceEventTest {
    
    @Test
    void testLaunchEvent() {
        Instance instance = new Instance();
        instance.setId("test-instance");
        
        InstanceEvent event = new InstanceEvent(InstanceEvent.LAUNCH, instance);
        
        assertEquals(InstanceEvent.LAUNCH, event.getEventType());
        assertSame(instance, event.getInstance());
    }
    
    @Test
    void testSettingsEvent() {
        Instance instance = new Instance();
        instance.setId("test-instance");
        
        InstanceEvent event = new InstanceEvent(InstanceEvent.SETTINGS, instance);
        
        assertEquals(InstanceEvent.SETTINGS, event.getEventType());
        assertSame(instance, event.getInstance());
    }
    
    @Test
    void testDeleteEvent() {
        Instance instance = new Instance();
        instance.setId("test-instance");
        
        InstanceEvent event = new InstanceEvent(InstanceEvent.DELETE, instance);
        
        assertEquals(InstanceEvent.DELETE, event.getEventType());
        assertSame(instance, event.getInstance());
    }
    
    @Test
    void testEventHierarchy() {
        assertEquals(InstanceEvent.ANY, InstanceEvent.LAUNCH.getSuperType());
        assertEquals(InstanceEvent.ANY, InstanceEvent.SETTINGS.getSuperType());
        assertEquals(InstanceEvent.ANY, InstanceEvent.DELETE.getSuperType());
    }
}