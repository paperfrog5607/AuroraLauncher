package org.aurora.launcher.core.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class EventBusTest {

    @BeforeEach
    void setUp() {
        EventBus.clear();
    }

    @Test
    void register_handlerAdded() {
        AtomicInteger counter = new AtomicInteger(0);
        EventHandler<TestEvent> handler = e -> counter.incrementAndGet();
        
        EventBus.register(TestEvent.class, handler);
        EventBus.post(new TestEvent("test"));
        
        assertEquals(1, counter.get());
    }

    @Test
    void unregister_handlerRemoved() {
        AtomicInteger counter = new AtomicInteger(0);
        EventHandler<TestEvent> handler = e -> counter.incrementAndGet();
        
        EventBus.register(TestEvent.class, handler);
        EventBus.unregister(TestEvent.class, handler);
        EventBus.post(new TestEvent("test"));
        
        assertEquals(0, counter.get());
    }

    @Test
    void post_multipleHandlers_allCalled() {
        AtomicInteger counter = new AtomicInteger(0);
        EventHandler<TestEvent> handler1 = e -> counter.incrementAndGet();
        EventHandler<TestEvent> handler2 = e -> counter.incrementAndGet();
        
        EventBus.register(TestEvent.class, handler1);
        EventBus.register(TestEvent.class, handler2);
        EventBus.post(new TestEvent("test"));
        
        assertEquals(2, counter.get());
    }

    @Test
    void post_differentEventTypes_correctHandlersCalled() {
        AtomicInteger testCounter = new AtomicInteger(0);
        AtomicInteger otherCounter = new AtomicInteger(0);
        
        EventBus.register(TestEvent.class, e -> testCounter.incrementAndGet());
        EventBus.register(OtherEvent.class, e -> otherCounter.incrementAndGet());
        
        EventBus.post(new TestEvent("test"));
        
        assertEquals(1, testCounter.get());
        assertEquals(0, otherCounter.get());
    }

    @Test
    void postAsync_callsHandler() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        EventHandler<TestEvent> handler = e -> counter.incrementAndGet();
        
        EventBus.register(TestEvent.class, handler);
        EventBus.postAsync(new TestEvent("test"));
        
        Thread.sleep(100);
        assertEquals(1, counter.get());
    }

    static class TestEvent {
        final String message;
        TestEvent(String message) { this.message = message; }
    }

    static class OtherEvent {
        final int value;
        OtherEvent(int value) { this.value = value; }
    }
}