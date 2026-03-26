package org.aurora.launcher.config.batch;

import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class BatchResultTest {
    
    @Test
    void isAllSuccess_noErrors_returnsTrue() {
        BatchResult result = new BatchResult();
        result.addProcessedFile(Paths.get("test.txt"));
        
        assertTrue(result.isAllSuccess());
        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailCount());
    }
    
    @Test
    void isAllSuccess_hasErrors_returnsFalse() {
        BatchResult result = new BatchResult();
        result.addError(new BatchError(Paths.get("test.txt"), "op", "error"));
        
        assertFalse(result.isAllSuccess());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailCount());
    }
    
    @Test
    void getErrors_returnsErrorsList() {
        BatchResult result = new BatchResult();
        BatchError error = new BatchError(Paths.get("test.txt"), "op", "message");
        result.addError(error);
        
        assertEquals(1, result.getErrors().size());
        assertEquals(error, result.getErrors().get(0));
    }
}