package org.aurora.launcher.download.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ChunkManager {

    public List<ChunkInfo> planChunks(long fileSize, int chunkCount) {
        List<ChunkInfo> chunks = new ArrayList<>();
        long chunkSize = fileSize / chunkCount;
        
        for (int i = 0; i < chunkCount; i++) {
            long start = i * chunkSize;
            long end;
            if (i == chunkCount - 1) {
                end = fileSize - 1;
            } else {
                end = start + chunkSize - 1;
            }
            chunks.add(new ChunkInfo(i, start, end));
        }
        
        return chunks;
    }

    public boolean allCompleted(List<ChunkInfo> chunks) {
        for (ChunkInfo chunk : chunks) {
            if (chunk.getStatus() != ChunkStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }

    public void mergeChunks(List<ChunkInfo> chunks, List<Path> chunkFiles, Path output) throws IOException {
        Files.createDirectories(output.getParent());
        
        try (OutputStream os = Files.newOutputStream(output)) {
            for (Path chunkFile : chunkFiles) {
                if (Files.exists(chunkFile)) {
                    try (InputStream is = Files.newInputStream(chunkFile)) {
                        byte[] buffer = new byte[8192];
                        int read;
                        while ((read = is.read(buffer)) != -1) {
                            os.write(buffer, 0, read);
                        }
                    }
                }
            }
        }
    }

    public void cleanupChunks(List<Path> chunkFiles) {
        for (Path chunkFile : chunkFiles) {
            try {
                Files.deleteIfExists(chunkFile);
            } catch (IOException ignored) {
            }
        }
    }
}