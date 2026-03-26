package org.aurora.launcher.download.chunk;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChunkManagerTest {

    @Test
    void planChunksEvenDivision() {
        ChunkManager manager = new ChunkManager();
        
        List<ChunkInfo> chunks = manager.planChunks(1000, 4);
        
        assertEquals(4, chunks.size());
        
        assertEquals(0, chunks.get(0).getStartByte());
        assertEquals(249, chunks.get(0).getEndByte());
        
        assertEquals(250, chunks.get(1).getStartByte());
        assertEquals(499, chunks.get(1).getEndByte());
        
        assertEquals(500, chunks.get(2).getStartByte());
        assertEquals(749, chunks.get(2).getEndByte());
        
        assertEquals(750, chunks.get(3).getStartByte());
        assertEquals(999, chunks.get(3).getEndByte());
    }

    @Test
    void planChunksOddDivision() {
        ChunkManager manager = new ChunkManager();
        
        List<ChunkInfo> chunks = manager.planChunks(1000, 3);
        
        assertEquals(3, chunks.size());
        
        assertEquals(0, chunks.get(0).getStartByte());
        assertEquals(332, chunks.get(0).getEndByte());
        
        assertEquals(333, chunks.get(1).getStartByte());
        assertEquals(665, chunks.get(1).getEndByte());
        
        assertEquals(666, chunks.get(2).getStartByte());
        assertEquals(999, chunks.get(2).getEndByte());
    }

    @Test
    void allCompleted() {
        ChunkManager manager = new ChunkManager();
        List<ChunkInfo> chunks = manager.planChunks(1000, 4);
        
        assertFalse(manager.allCompleted(chunks));
        
        for (ChunkInfo chunk : chunks) {
            chunk.setStatus(ChunkStatus.COMPLETED);
        }
        
        assertTrue(manager.allCompleted(chunks));
    }

    @Test
    void allCompletedWithSomeFailed() {
        ChunkManager manager = new ChunkManager();
        List<ChunkInfo> chunks = manager.planChunks(1000, 4);
        
        chunks.get(0).setStatus(ChunkStatus.COMPLETED);
        chunks.get(1).setStatus(ChunkStatus.COMPLETED);
        chunks.get(2).setStatus(ChunkStatus.FAILED);
        chunks.get(3).setStatus(ChunkStatus.COMPLETED);
        
        assertFalse(manager.allCompleted(chunks));
    }

    @Test
    void singleChunk() {
        ChunkManager manager = new ChunkManager();
        
        List<ChunkInfo> chunks = manager.planChunks(500, 1);
        
        assertEquals(1, chunks.size());
        assertEquals(0, chunks.get(0).getStartByte());
        assertEquals(499, chunks.get(0).getEndByte());
    }
}