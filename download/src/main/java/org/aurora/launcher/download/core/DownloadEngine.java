package org.aurora.launcher.download.core;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aurora.launcher.download.config.DownloadConfig;
import org.aurora.launcher.download.progress.ProgressEvent;
import org.aurora.launcher.download.progress.ProgressTracker;
import org.aurora.launcher.download.queue.DownloadQueue;
import org.aurora.launcher.download.retry.RetryHandler;
import org.aurora.launcher.download.retry.RetryPolicy;
import org.aurora.launcher.download.validation.ChecksumValidator;
import org.aurora.launcher.download.validation.SizeValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadEngine {
    private final DownloadConfig config;
    private final DownloadQueue queue;
    private final OkHttpClient httpClient;
    private final ExecutorService executor;
    private final RetryHandler retryHandler;

    public DownloadEngine(DownloadConfig config) {
        this.config = config;
        this.queue = new DownloadQueue(config.getMaxConcurrent());
        this.httpClient = createHttpClient();
        this.executor = Executors.newCachedThreadPool();
        this.retryHandler = new RetryHandler(createRetryPolicy());
    }

    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    private RetryPolicy createRetryPolicy() {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxRetries(config.getMaxRetries());
        policy.setInitialDelay(Duration.ofSeconds(1));
        return policy;
    }

    public CompletableFuture<DownloadResult> download(DownloadRequest request) {
        CompletableFuture<DownloadResult> result = new CompletableFuture<>();
        DownloadTask task = new DownloadTask(request);
        
        executor.submit(() -> {
            try {
                executeDownload(task, result);
            } catch (Exception e) {
                result.completeExceptionally(e);
            }
        });
        
        return result;
    }

    private void executeDownload(DownloadTask task, CompletableFuture<DownloadResult> result) {
        DownloadRequest request = task.getRequest();
        task.setStatus(DownloadStatus.DOWNLOADING);
        task.setStartTime(Instant.now());
        
        try {
            Files.createDirectories(request.getTargetPath().getParent());
            
            Path tempFile = request.getTargetPath().resolveSibling(
                request.getTargetPath().getFileName() + ".tmp");
            
            long contentLength = getContentLength(request.getUrl());
            task.setTotalBytes(contentLength);
            
            ProgressTracker tracker = new ProgressTracker(contentLength);
            
            downloadToFile(request, tempFile, tracker, task);
            
            if (!validateFile(tempFile, request)) {
                Files.deleteIfExists(tempFile);
                DownloadResult errorResult = createResult(task, DownloadStatus.FAILED);
                errorResult.setError("File validation failed");
                result.complete(errorResult);
                return;
            }
            
            Files.move(tempFile, request.getTargetPath());
            
            task.setEndTime(Instant.now());
            task.setStatus(DownloadStatus.COMPLETED);
            
            result.complete(createResult(task, DownloadStatus.COMPLETED));
            
        } catch (Exception e) {
            task.setStatus(DownloadStatus.FAILED);
            DownloadResult errorResult = createResult(task, DownloadStatus.FAILED);
            errorResult.setError(e.getMessage());
            result.complete(errorResult);
        }
    }

    private long getContentLength(String url) throws IOException {
        Request request = new Request.Builder().url(url).head().build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return -1;
            }
            return response.body() != null ? response.body().contentLength() : -1;
        }
    }

    private void downloadToFile(DownloadRequest request, Path target, 
            ProgressTracker tracker, DownloadTask task) throws IOException {
        
        Request.Builder requestBuilder = new Request.Builder().url(request.getUrl());
        for (java.util.Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            requestBuilder.addHeader(header.getKey(), header.getValue());
        }
        
        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Download failed with code: " + response.code());
            }
            
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            
            try (InputStream is = body.byteStream();
                 OutputStream os = Files.newOutputStream(target)) {
                byte[] buffer = new byte[8192];
                int read;
                
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                    tracker.update(read);
                    task.setDownloadedBytes(tracker.getDownloadedBytes());
                    
                    if (task.getStatus() == DownloadStatus.CANCELLED) {
                        throw new IOException("Download cancelled");
                    }
                }
            }
        }
    }

    private boolean validateFile(Path file, DownloadRequest request) {
        SizeValidator sizeValidator = new SizeValidator();
        ChecksumValidator checksumValidator = new ChecksumValidator();
        
        List<Boolean> results = Arrays.asList(
            sizeValidator.validate(file, request),
            checksumValidator.validate(file, request)
        );
        
        for (Boolean valid : results) {
            if (!valid) return false;
        }
        return true;
    }

    private DownloadResult createResult(DownloadTask task, DownloadStatus status) {
        DownloadResult result = new DownloadResult();
        result.setId(task.getId());
        result.setStatus(status);
        result.setFilePath(task.getRequest().getTargetPath());
        result.setBytesDownloaded(task.getDownloadedBytes());
        result.setTotalBytes(task.getTotalBytes());
        
        if (task.getStartTime() != null && task.getEndTime() != null) {
            long duration = Duration.between(task.getStartTime(), task.getEndTime()).toMillis();
            result.setDuration(duration);
            if (duration > 0) {
                result.setAverageSpeed((task.getDownloadedBytes() * 1000) / duration);
            }
        }
        
        return result;
    }

    public CompletableFuture<List<DownloadResult>> downloadBatch(List<DownloadRequest> requests) {
        CompletableFuture<DownloadResult>[] futures = new CompletableFuture[requests.size()];
        for (int i = 0; i < requests.size(); i++) {
            futures[i] = download(requests.get(i));
        }
        
        return CompletableFuture.allOf(futures).thenApply(v -> {
            List<DownloadResult> results = new java.util.ArrayList<>();
            for (CompletableFuture<DownloadResult> future : futures) {
                results.add(future.join());
            }
            return results;
        });
    }

    public void cancel(String taskId) {
        queue.cancel(taskId);
    }

    public void cancelAll() {
        queue.shutdown();
    }

    public void pause(String taskId) {
        queue.pause(taskId);
    }

    public void resume(String taskId) {
        queue.resume(taskId);
    }

    public void setMaxConcurrent(int max) {
        queue.setMaxConcurrent(max);
    }

    public int getMaxConcurrent() {
        return queue.getMaxConcurrent();
    }

    public int getActiveCount() {
        return queue.getActiveCount();
    }

    public int getQueuedCount() {
        return queue.getQueueSize();
    }

    public DownloadConfig getConfig() {
        return config;
    }

    public DownloadQueue getQueue() {
        return queue;
    }

    public void shutdown() {
        executor.shutdown();
        queue.shutdown();
    }
}