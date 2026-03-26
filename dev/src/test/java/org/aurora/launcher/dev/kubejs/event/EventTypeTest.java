package org.aurora.launcher.dev.kubejs.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventTypeTest {

    @Test
    void eventTypeEnum() {
        assertEquals(12, EventType.values().length);
        assertEquals(EventType.BLOCK_BREAK, EventType.valueOf("BLOCK_BREAK"));
        assertEquals(EventType.BLOCK_PLACE, EventType.valueOf("BLOCK_PLACE"));
        assertEquals(EventType.ITEM_CRAFTED, EventType.valueOf("ITEM_CRAFTED"));
        assertEquals(EventType.ITEM_SMELTED, EventType.valueOf("ITEM_SMELTED"));
        assertEquals(EventType.ENTITY_DEATH, EventType.valueOf("ENTITY_DEATH"));
        assertEquals(EventType.PLAYER_JOIN, EventType.valueOf("PLAYER_JOIN"));
        assertEquals(EventType.PLAYER_QUIT, EventType.valueOf("PLAYER_QUIT"));
        assertEquals(EventType.PLAYER_CHAT, EventType.valueOf("PLAYER_CHAT"));
        assertEquals(EventType.SERVER_LOAD, EventType.valueOf("SERVER_LOAD"));
        assertEquals(EventType.SERVER_TICK, EventType.valueOf("SERVER_TICK"));
        assertEquals(EventType.WORLD_LOAD, EventType.valueOf("WORLD_LOAD"));
        assertEquals(EventType.WORLD_TICK, EventType.valueOf("WORLD_TICK"));
    }
}