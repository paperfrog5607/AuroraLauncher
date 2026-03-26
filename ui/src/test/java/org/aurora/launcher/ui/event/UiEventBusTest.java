package org.aurora.launcher.ui.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class UiEventBusTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testRegisterAndPost() throws InterruptedException {
        TestEventListener listener = new TestEventListener();
        UiEventBus.getInstance().register(listener);

        TestEvent event = new TestEvent("hello");
        UiEventBus.getInstance().postSync(event);

        assertEquals(1, listener.getCount());
        assertEquals("hello", listener.getLastMessage());
    }

    @Test
    void testUnregister() {
        TestEventListener listener = new TestEventListener();
        UiEventBus.getInstance().register(listener);
        UiEventBus.getInstance().unregister(listener);

        TestEvent event = new TestEvent("test");
        UiEventBus.getInstance().postSync(event);

        assertEquals(0, listener.getCount());
    }

    @Test
    void testMultipleListeners() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();

        UiEventBus.getInstance().register(listener1);
        UiEventBus.getInstance().register(listener2);

        TestEvent event = new TestEvent("multi");
        UiEventBus.getInstance().postSync(event);

        assertEquals(1, listener1.getCount());
        assertEquals(1, listener2.getCount());

        UiEventBus.getInstance().unregister(listener1);
        UiEventBus.getInstance().unregister(listener2);
    }

    static class TestEvent {
        private final String message;

        TestEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static class TestEventListener {
        private final AtomicInteger count = new AtomicInteger(0);
        private String lastMessage;

        @com.google.common.eventbus.Subscribe
        public void onTestEvent(TestEvent event) {
            count.incrementAndGet();
            lastMessage = event.getMessage();
        }

        public int getCount() {
            return count.get();
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }
}