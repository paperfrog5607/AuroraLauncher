package org.aurora.launcher.core.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventBus {
    private static final Map<Class<?>, List<EventHandler<?>>> handlers = new ConcurrentHashMap<>();

    private EventBus() {
    }

    public static <T> void register(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public static <T> void unregister(Class<T> eventType, EventHandler<T> handler) {
        List<EventHandler<?>> eventHandlers = handlers.get(eventType);
        if (eventHandlers != null) {
            eventHandlers.remove(handler);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void post(T event) {
        if (event == null) return;
        
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler<?> handler : eventHandlers) {
                try {
                    ((EventHandler<T>) handler).handle(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void postAsync(T event) {
        if (event == null) return;
        
        CompletableFuture.runAsync(() -> {
            List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
            if (eventHandlers != null) {
                for (EventHandler<?> handler : eventHandlers) {
                    try {
                        ((EventHandler<T>) handler).handle(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void clear() {
        handlers.clear();
    }
}