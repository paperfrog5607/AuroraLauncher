package org.aurora.launcher.config.batch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RenameOperationTest {
    
    @Test
    void apply_renamesKey() throws Exception {
        RenameOperation op = new RenameOperation("oldKey", "newKey");
        
        assertEquals("oldKey", op.getKey());
        assertEquals("newKey", op.getNewKey());
        assertTrue(op.getDescription().contains("oldKey"));
        assertTrue(op.getDescription().contains("newKey"));
    }
}