package org.aurora.launcher.ui.event;

import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UiEventBus {
    private static final Logger logger = LoggerFactory.getLogger(UiEventBus.class);
    private static final UiEventBus INSTANCE = new UiEventBus();
    
    private final EventBus eventBus;

    private UiEventBus() {
        this.eventBus = new EventBus((exception, context) -> {
            logger.error("Event bus error in {} for event {}", 
                context.getSubscriberMethod(), context.getEvent(), exception);
        });
    }

    public static UiEventBus getInstance() {
        return INSTANCE;
    }

    public void register(Object listener) {
        eventBus.register(listener);
        logger.debug("Registered listener: {}", listener.getClass().getSimpleName());
    }

    public void unregister(Object listener) {
        try {
            eventBus.unregister(listener);
            logger.debug("Unregistered listener: {}", listener.getClass().getSimpleName());
        } catch (IllegalArgumentException e) {
            logger.warn("Listener was not registered: {}", listener.getClass().getSimpleName());
        }
    }

    public void post(Object event) {
        if (Platform.isFxApplicationThread()) {
            eventBus.post(event);
        } else {
            Platform.runLater(() -> eventBus.post(event));
        }
        logger.trace("Posted event: {}", event.getClass().getSimpleName());
    }

    public void postSync(Object event) {
        eventBus.post(event);
    }
}