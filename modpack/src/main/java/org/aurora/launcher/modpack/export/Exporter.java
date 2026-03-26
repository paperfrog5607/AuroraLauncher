package org.aurora.launcher.modpack.export;

import org.aurora.launcher.modpack.instance.Instance;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface Exporter {
    
    String getFormat();
    
    CompletableFuture<Path> export(Instance instance, ExportOptions options);
}