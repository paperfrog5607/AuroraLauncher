package org.aurora.launcher.download.chunk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChunkInfoTest {

    @Test
    void createChunk() {
        ChunkInfo chunk = new ChunkInfo(0, 0, 999);
        
        assertEquals(0, chunk.getIndex());
        assertEquals(0, chunk.getStartByte());
        assertEquals(999, chunk.getEndByte());
        assertEquals(0, chunk.getDownloadedBytes());
        assertEquals(ChunkStatus.PENDING, chunk.getStatus());
    }

    @Test
    void setDownloadedBytes() {
        ChunkInfo chunk = new ChunkInfo(1, 1000, 1999);
        
        chunk.setDownloadedBytes(500);
        
        assertEquals(500, chunk.getDownloadedBytes());
    }

    @Test
    void statusTransitions() {
        ChunkInfo chunk = new ChunkInfo(0, 0, 999);
        
        assertEquals(ChunkStatus.PENDING, chunk.getStatus());
        
        chunk.setStatus(ChunkStatus.DOWNLOADING);
        assertEquals(ChunkStatus.DOWNLOADING, chunk.getStatus());
        
        chunk.setStatus(ChunkStatus.COMPLETED);
        assertEquals(ChunkStatus.COMPLETED, chunk.getStatus());
    }

    @Test
    void chunkStatusEnum() {
        assertEquals(4, ChunkStatus.values().length);
        assertEquals(ChunkStatus.PENDING, ChunkStatus.valueOf("PENDING"));
        assertEquals(ChunkStatus.DOWNLOADING, ChunkStatus.valueOf("DOWNLOADING"));
        assertEquals(ChunkStatus.COMPLETED, ChunkStatus.valueOf("COMPLETED"));
        assertEquals(ChunkStatus.FAILED, ChunkStatus.valueOf("FAILED"));
    }

    @Test
    void getSize() {
        ChunkInfo chunk = new ChunkInfo(0, 0, 999);
        assertEquals(1000, chunk.getSize());
        
        ChunkInfo chunk2 = new ChunkInfo(1, 1000, 1999);
        assertEquals(1000, chunk2.getSize());
    }
}