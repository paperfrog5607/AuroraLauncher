package org.aurora.launcher.download.resume;

import org.aurora.launcher.download.chunk.ChunkInfo;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class ResumeRecordTest {

    @Test
    void createRecord() {
        Instant now = Instant.now();
        ResumeRecord record = new ResumeRecord();
        record.setId("test-id");
        record.setUrl("https://example.com/file.zip");
        record.setTargetPath(Paths.get("/tmp/file.zip"));
        record.setTotalSize(1000);
        record.setDownloadedSize(500);
        record.setCreatedTime(now);
        record.setLastModified(now);
        record.setTempFilePath(Paths.get("/tmp/file.zip.tmp"));
        
        assertEquals("test-id", record.getId());
        assertEquals("https://example.com/file.zip", record.getUrl());
        assertEquals(Paths.get("/tmp/file.zip"), record.getTargetPath());
        assertEquals(1000, record.getTotalSize());
        assertEquals(500, record.getDownloadedSize());
        assertEquals(now, record.getCreatedTime());
        assertEquals(now, record.getLastModified());
        assertEquals(Paths.get("/tmp/file.zip.tmp"), record.getTempFilePath());
    }

    @Test
    void chunksInRecord() {
        ResumeRecord record = new ResumeRecord();
        ChunkInfo chunk1 = new ChunkInfo(0, 0, 499);
        ChunkInfo chunk2 = new ChunkInfo(1, 500, 999);
        
        record.setChunks(Arrays.asList(chunk1, chunk2));
        
        assertEquals(2, record.getChunks().size());
        assertEquals(0, record.getChunks().get(0).getIndex());
        assertEquals(1, record.getChunks().get(1).getIndex());
    }
}