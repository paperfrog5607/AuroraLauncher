package org.aurora.launcher.config.batch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemoveOperationTest {
    
    @Test
    void apply_removesValue() throws Exception {
        RemoveOperation op = new RemoveOperation("key");
        
        assertEquals("key", op.getKey());
        assertTrue(op.getDescription().contains("Remove"));
        assertTrue(op.getDescription().contains("key"));
    }
}