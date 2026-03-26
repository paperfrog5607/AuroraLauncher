package org.aurora.ui.service;

import org.aurora.launcher.ui.service.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceLocatorTest {

    @BeforeEach
    void setUp() {
        ServiceLocator.clear();
    }

    @Test
    void register_and_get_service() {
        TestService service = new TestServiceImpl();
        ServiceLocator.register(TestService.class, service);
        
        TestService retrieved = ServiceLocator.get(TestService.class);
        
        assertSame(service, retrieved);
    }

    @Test
    void get_throws_when_not_registered() {
        assertThrows(IllegalStateException.class, () -> {
            ServiceLocator.get(TestService.class);
        });
    }

    @Test
    void isRegistered_returns_correct_status() {
        assertFalse(ServiceLocator.isRegistered(TestService.class));
        
        ServiceLocator.register(TestService.class, new TestServiceImpl());
        
        assertTrue(ServiceLocator.isRegistered(TestService.class));
    }

    @Test
    void unregister_removes_service() {
        ServiceLocator.register(TestService.class, new TestServiceImpl());
        
        ServiceLocator.unregister(TestService.class);
        
        assertFalse(ServiceLocator.isRegistered(TestService.class));
    }

    @Test
    void register_replaces_existing_service() {
        TestService service1 = new TestServiceImpl();
        TestService service2 = new TestServiceImpl();
        
        ServiceLocator.register(TestService.class, service1);
        ServiceLocator.register(TestService.class, service2);
        
        TestService retrieved = ServiceLocator.get(TestService.class);
        assertSame(service2, retrieved);
    }

    @Test
    void clear_removes_all_services() {
        ServiceLocator.register(TestService.class, new TestServiceImpl());
        ServiceLocator.register(AnotherService.class, new AnotherServiceImpl());
        
        ServiceLocator.clear();
        
        assertFalse(ServiceLocator.isRegistered(TestService.class));
        assertFalse(ServiceLocator.isRegistered(AnotherService.class));
    }

    interface TestService {}
    
    static class TestServiceImpl implements TestService {}
    
    interface AnotherService {}
    
    static class AnotherServiceImpl implements AnotherService {}
}