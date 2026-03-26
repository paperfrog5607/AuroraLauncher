package org.aurora.launcher.download.resume;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.aurora.launcher.download.chunk.ChunkInfo;
import org.aurora.launcher.download.core.DownloadTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ResumeManager {
    private final Path recordsDir;
    private final Gson gson;

    public ResumeManager(Path recordsDir) {
        this.recordsDir = recordsDir;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.createDirectories(recordsDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create records directory", e);
        }
    }

    public void saveRecord(DownloadTask task) {
        ResumeRecord record = new ResumeRecord();
        record.setId(task.getId());
        record.setUrl(task.getRequest().getUrl());
        record.setTargetPath(task.getRequest().getTargetPath());
        record.setTotalSize(task.getTotalBytes());
        record.setDownloadedSize(task.getDownloadedBytes());
        record.setCreatedTime(Instant.now());
        record.setLastModified(Instant.now());
        record.setTempFilePath(task.getRequest().getTargetPath().resolveSibling(
            task.getRequest().getTargetPath().getFileName() + ".tmp"));

        Path recordFile = recordsDir.resolve(task.getId() + ".json");
        try {
            String json = gson.toJson(record);
            Files.write(recordFile, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume record", e);
        }
    }

    public Optional<ResumeRecord> loadRecord(String taskId) {
        Path recordFile = recordsDir.resolve(taskId + ".json");
        if (!Files.exists(recordFile)) {
            return Optional.empty();
        }

        try {
            String json = new String(Files.readAllBytes(recordFile), StandardCharsets.UTF_8);
            ResumeRecord record = gson.fromJson(json, ResumeRecord.class);
            return Optional.of(record);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public void deleteRecord(String taskId) {
        Path recordFile = recordsDir.resolve(taskId + ".json");
        try {
            Files.deleteIfExists(recordFile);
        } catch (IOException ignored) {
        }
    }

    public void cleanupOldRecords(Duration maxAge) {
        Instant cutoff = Instant.now().minus(maxAge);
        try (Stream<Path> files = Files.list(recordsDir)) {
            files.filter(p -> p.toString().endsWith(".json"))
                 .forEach(p -> {
                     try {
                         if (Files.getLastModifiedTime(p).toInstant().isBefore(cutoff)) {
                             Files.delete(p);
                         }
                     } catch (IOException ignored) {
                     }
                 });
        } catch (IOException ignored) {
        }
    }

    public List<ResumeRecord> getAllRecords() {
        List<ResumeRecord> records = new ArrayList<>();
        try (Stream<Path> files = Files.list(recordsDir)) {
            files.filter(p -> p.toString().endsWith(".json"))
                 .forEach(p -> {
                     String taskId = p.getFileName().toString().replace(".json", "");
                     loadRecord(taskId).ifPresent(records::add);
                 });
        } catch (IOException ignored) {
        }
        return records;
    }
}