package org.aurora.launcher.mod.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModEnabler {
    
    private static final Logger logger = LoggerFactory.getLogger(ModEnabler.class);
    
    public void enable(Path modFile) {
        String fileName = modFile.getFileName().toString();
        if (fileName.endsWith(".disabled")) {
            String newName = fileName.substring(0, fileName.length() - 9);
            Path newPath = modFile.getParent().resolve(newName);
            
            try {
                Files.move(modFile, newPath);
                logger.info("Enabled mod: {}", newName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to enable mod: " + fileName, e);
            }
        }
    }
    
    public void disable(Path modFile) {
        String fileName = modFile.getFileName().toString();
        if (!fileName.endsWith(".disabled")) {
            Path newPath = Paths.get(modFile.toString() + ".disabled");
            
            try {
                Files.move(modFile, newPath);
                logger.info("Disabled mod: {}", fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to disable mod: " + fileName, e);
            }
        }
    }
    
    public boolean isEnabled(Path modFile) {
        return !modFile.getFileName().toString().endsWith(".disabled");
    }
}