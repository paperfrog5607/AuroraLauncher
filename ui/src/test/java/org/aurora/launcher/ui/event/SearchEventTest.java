package org.aurora.launcher.ui.event;

import org.aurora.launcher.mod.search.ModSearchResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchEventTest {
    
    @Test
    void testDownloadEvent() {
        ModSearchResult result = new ModSearchResult();
        result.setId("test-mod");
        result.setName("Test Mod");
        
        SearchEvent event = new SearchEvent(SearchEvent.DOWNLOAD, result);
        
        assertEquals(SearchEvent.DOWNLOAD, event.getEventType());
        assertSame(result, event.getResult());
    }
    
    @Test
    void testDetailEvent() {
        ModSearchResult result = new ModSearchResult();
        result.setId("test-mod");
        result.setName("Test Mod");
        
        SearchEvent event = new SearchEvent(SearchEvent.DETAIL, result);
        
        assertEquals(SearchEvent.DETAIL, event.getEventType());
        assertSame(result, event.getResult());
    }
}