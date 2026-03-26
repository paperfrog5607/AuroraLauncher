package org.aurora.launcher.dev.kubejs.event;

import org.aurora.launcher.dev.template.KubeJsCategory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventTemplateTest {

    @Test
    void createEventTemplate() {
        EventTemplate template = new EventTemplate();
        template.setId("block-break");
        template.setName("Block Break Event");
        template.setEventType(EventType.BLOCK_BREAK);
        template.setEventClass("BlockEvents");
        
        assertEquals("block-break", template.getId());
        assertEquals("Block Break Event", template.getName());
        assertEquals(EventType.BLOCK_BREAK, template.getEventType());
        assertEquals("BlockEvents", template.getEventClass());
        assertEquals(KubeJsCategory.EVENT, template.getCategory());
    }
}