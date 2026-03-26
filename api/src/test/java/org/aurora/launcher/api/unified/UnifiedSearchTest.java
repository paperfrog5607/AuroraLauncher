package org.aurora.launcher.api.unified;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnifiedSearchTest {

    @Test
    void shouldCreateSearch() {
        UnifiedSearch search = new UnifiedSearch();
        
        assertNotNull(search);
    }

    @Test
    void shouldCreateWithOptions() {
        SearchOptions options = SearchOptions.builder()
                .query("test")
                .gameVersion("1.20.1")
                .loader("forge")
                .limit(10)
                .offset(0)
                .build();
        
        assertEquals("test", options.getQuery());
        assertEquals("1.20.1", options.getGameVersion());
        assertEquals("forge", options.getLoader());
        assertEquals(10, options.getLimit());
        assertEquals(0, options.getOffset());
    }
}