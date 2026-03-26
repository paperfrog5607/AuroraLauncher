package org.aurora.launcher.dev.kubejs.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventHandlerTest {

    @Test
    void generateEventScript() {
        EventHandler handler = new EventHandler();
        EventTemplate template = new EventTemplate();
        template.setName("Block Break");
        template.setEventType(EventType.BLOCK_BREAK);
        
        EventConfig config = new EventConfig();
        config.setLogic("  event.player.tell('Block broken!');");
        
        String script = handler.generateEventScript(template, config);
        
        assertTrue(script.contains("Block Break"));
        assertTrue(script.contains("blockBroken"));
        assertTrue(script.contains("event.player.tell"));
    }

    @Test
    void generateWithFilter() {
        EventHandler handler = new EventHandler();
        EventTemplate template = new EventTemplate();
        template.setName("Item Crafted");
        template.setEventType(EventType.ITEM_CRAFTED);
        
        EventConfig config = new EventConfig();
        config.setFilter("minecraft:diamond");
        config.setLogic("  console.info('Diamond crafted!');");
        
        String script = handler.generateEventScript(template, config);
        
        assertTrue(script.contains("minecraft:diamond"));
    }
}