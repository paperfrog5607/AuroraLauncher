package org.aurora.launcher.mod.parser;

import org.aurora.launcher.mod.scanner.ModInfo;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface ModParser {
    
    boolean canParse(Path modFile);
    
    CompletableFuture<ModInfo> parse(Path modFile);
}