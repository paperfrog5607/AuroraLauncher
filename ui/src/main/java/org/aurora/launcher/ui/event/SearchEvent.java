package org.aurora.launcher.ui.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.aurora.launcher.mod.search.ModSearchResult;

public class SearchEvent extends Event {
    
    public static final EventType<SearchEvent> ANY = 
        new EventType<>(Event.ANY, "SEARCH");
    public static final EventType<SearchEvent> DOWNLOAD = 
        new EventType<>(ANY, "DOWNLOAD");
    public static final EventType<SearchEvent> DETAIL = 
        new EventType<>(ANY, "DETAIL");
    
    private final ModSearchResult result;
    
    public SearchEvent(EventType<? extends Event> eventType, ModSearchResult result) {
        super(eventType);
        this.result = result;
    }
    
    public ModSearchResult getResult() {
        return result;
    }
}