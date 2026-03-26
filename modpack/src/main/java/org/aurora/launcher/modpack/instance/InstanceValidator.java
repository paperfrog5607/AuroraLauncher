package org.aurora.launcher.modpack.instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InstanceValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(InstanceValidator.class);
    
    public ValidationResult validate(Instance instance) {
        ValidationResult result = new ValidationResult();
        
        if (instance == null) {
            result.addError("Instance is null");
            return result;
        }
        
        validateBasicInfo(instance, result);
        validateDirectory(instance, result);
        validateConfig(instance, result);
        
        return result;
    }
    
    private void validateBasicInfo(Instance instance, ValidationResult result) {
        if (instance.getId() == null || instance.getId().trim().isEmpty()) {
            result.addError("Instance ID is required");
        }
        
        if (instance.getName() == null || instance.getName().trim().isEmpty()) {
            result.addError("Instance name is required");
        }
        
        if (instance.getConfig() == null) {
            result.addError("Instance config is required");
        }
    }
    
    private void validateDirectory(Instance instance, ValidationResult result) {
        Path instanceDir = instance.getInstanceDir();
        if (instanceDir == null) {
            result.addError("Instance directory is not set");
            return;
        }
        
        if (!Files.exists(instanceDir)) {
            result.addWarning("Instance directory does not exist: " + instanceDir);
            return;
        }
        
        Path minecraftDir = instance.getMinecraftDir();
        if (minecraftDir != null && !Files.exists(minecraftDir)) {
            result.addWarning(".minecraft directory does not exist");
        }
        
        Path configFile = instanceDir.resolve("instance.json");
        if (!Files.exists(configFile)) {
            result.addWarning("instance.json does not exist");
        }
    }
    
    private void validateConfig(Instance instance, ValidationResult result) {
        InstanceConfig config = instance.getConfig();
        if (config == null) return;
        
        if (config.getMinecraftVersion() == null || config.getMinecraftVersion().trim().isEmpty()) {
            result.addError("Minecraft version is required");
        }
        
        if (config.getLoaderType() != null && !config.getLoaderType().equalsIgnoreCase("vanilla")) {
            if (config.getLoaderVersion() == null || config.getLoaderVersion().trim().isEmpty()) {
                result.addError("Loader version is required for modded instances");
            }
        }
        
        validateMemoryConfig(config, result);
    }
    
    private void validateMemoryConfig(InstanceConfig config, ValidationResult result) {
        InstanceConfig.MemoryConfig memory = config.getMemory();
        if (memory != null) {
            if (memory.getMinMB() <= 0) {
                result.addError("Minimum memory must be positive");
            }
            if (memory.getMaxMB() <= 0) {
                result.addError("Maximum memory must be positive");
            }
            if (memory.getMinMB() > memory.getMaxMB()) {
                result.addError("Minimum memory cannot exceed maximum memory");
            }
        }
    }
    
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!errors.isEmpty()) {
                sb.append("Errors: ").append(errors).append("\n");
            }
            if (!warnings.isEmpty()) {
                sb.append("Warnings: ").append(warnings);
            }
            return sb.toString();
        }
    }
}