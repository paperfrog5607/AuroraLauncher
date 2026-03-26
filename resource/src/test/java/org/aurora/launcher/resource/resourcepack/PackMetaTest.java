package org.aurora.launcher.resource.resourcepack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PackMetaTest {
    
    @Test
    void constructor_defaultValues() {
        PackMeta meta = new PackMeta();
        
        assertEquals(0, meta.getPackFormat());
        assertNull(meta.getDescription());
    }
    
    @Test
    void constructor_withValues() {
        PackMeta meta = new PackMeta(34, "Test pack");
        
        assertEquals(34, meta.getPackFormat());
        assertEquals("Test pack", meta.getDescription());
    }
    
    @Test
    void parse_validMcmeta() {
        String mcmeta = "{\"pack\":{\"pack_format\":34,\"description\":\"Test description\"}}";
        
        PackMeta meta = PackMeta.parse(mcmeta.getBytes());
        
        assertEquals(34, meta.getPackFormat());
        assertEquals("Test description", meta.getDescription());
    }
    
    @Test
    void parse_emptyData() {
        PackMeta meta = PackMeta.parse(new byte[0]);
        
        assertEquals(0, meta.getPackFormat());
        assertNull(meta.getDescription());
    }
    
    @Test
    void parse_nullData() {
        PackMeta meta = PackMeta.parse(null);
        
        assertEquals(0, meta.getPackFormat());
        assertNull(meta.getDescription());
    }
    
    @Test
    void parse_invalidJson() {
        PackMeta meta = PackMeta.parse("invalid json".getBytes());
        
        assertEquals(0, meta.getPackFormat());
        assertNull(meta.getDescription());
    }
    
    @Test
    void setters_updateValues() {
        PackMeta meta = new PackMeta();
        meta.setPackFormat(22);
        meta.setDescription("Updated");
        
        assertEquals(22, meta.getPackFormat());
        assertEquals("Updated", meta.getDescription());
    }
}