package org.aurora.launcher.core.net;

import okhttp3.*;
import org.aurora.launcher.core.event.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private final OkHttpClient client;

    public HttpClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public String get(String url) throws IOException {
        return get(url, null);
    }

    public String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try (Response response = client.newCall(builder.build()).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = "";
                ResponseBody body = response.body();
                if (body != null) {
                    errorBody = body.string();
                }
                throw new IOException("Request failed: " + response.code() + ", body: " + errorBody);
            }
            ResponseBody body = response.body();
            return body != null ? body.string() : "";
        }
    }

    public byte[] getBytes(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request failed: " + response.code());
            }
            ResponseBody body = response.body();
            return body != null ? body.bytes() : new byte[0];
        }
    }

    public void download(String url, Path target, ProgressCallback callback) throws IOException {
        download(url, target, new DownloadOptions(), callback);
    }

    public void download(String url, Path target, DownloadOptions options) throws IOException {
        download(url, target, options, null);
    }

    public void download(String url, Path target, DownloadOptions options, ProgressCallback callback) throws IOException {
        String fileName = target.getFileName().toString();
        EventBus.post(new DownloadStartedEvent(url, fileName));

        OkHttpClient downloadClient = client.newBuilder()
                .connectTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .build();

        Request.Builder builder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : options.getHeaders().entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        int retries = 0;
        Exception lastException = null;
        
        while (retries <= options.getRetryCount()) {
            try {
                Files.createDirectories(target.getParent());
                
                try (Response response = downloadClient.newCall(builder.build()).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Download failed: " + response.code());
                    }
                    
                    ResponseBody body = response.body();
                    if (body == null) {
                        throw new IOException("Empty response body");
                    }
                    
                    long contentLength = body.contentLength();
                    try (java.io.InputStream is = body.byteStream();
                         java.io.OutputStream os = Files.newOutputStream(target)) {
                        byte[] buffer = new byte[8192];
                        int read;
                        long totalRead = 0;
                        
                        while ((read = is.read(buffer)) != -1) {
                            os.write(buffer, 0, read);
                            totalRead += read;
                            if (callback != null) {
                                callback.onProgress(totalRead, contentLength);
                            }
                            EventBus.post(new DownloadProgressEvent(fileName, totalRead, contentLength));
                        }
                    }
                    
                    EventBus.post(new DownloadCompletedEvent(fileName, target));
                    return;
                }
            } catch (Exception e) {
                lastException = e;
                retries++;
                if (retries <= options.getRetryCount()) {
                    try {
                        Thread.sleep(1000 * retries);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        EventBus.post(new DownloadFailedEvent(fileName, lastException));
        throw new IOException("Download failed after " + retries + " retries", lastException);
    }
    
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}