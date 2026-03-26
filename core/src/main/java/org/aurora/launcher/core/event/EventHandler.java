package org.aurora.launcher.core.event;

@FunctionalInterface
public interface EventHandler<T> {
    void handle(T event);
}