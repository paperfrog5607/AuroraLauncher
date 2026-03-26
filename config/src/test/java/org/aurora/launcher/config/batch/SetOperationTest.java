package org.aurora.launcher.config.batch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SetOperationTest {
    
    @Test
    void apply_setsValue() throws Exception {
        SetOperation op = new SetOperation("key", "value");
        
        assertEquals("key", op.getKey());
        assertEquals("value", op.getValue());
        assertTrue(op.getDescription().contains("key"));
        assertTrue(op.getDescription().contains("value"));
    }
    
    @Test
    void constructor_withComment_setsComment() {
        SetOperation op = new SetOperation("key", "value", "comment");
        
        assertEquals("comment", op.getComment());
    }
}