package org.aurora.launcher.modpack.import_;

import org.aurora.launcher.modpack.instance.Instance;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface Importer {
    
    String getFormat();
    
    boolean canImport(Path file);
    
    CompletableFuture<ImportTask> createImportTask(Path file, Path targetDir);
    
    CompletableFuture<Instance> import_(Path file, Path targetDir);
}