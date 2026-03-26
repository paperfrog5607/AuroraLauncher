package org.aurora.launcher.config.batch;

import org.aurora.launcher.config.editor.ConfigEditor;
import org.aurora.launcher.config.parser.JsonParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BatchEditor {
    
    private List<BatchOperation> operations;
    
    public BatchEditor() {
        this.operations = new ArrayList<>();
    }
    
    public void addOperation(BatchOperation operation) {
        operations.add(operation);
    }
    
    public void addSetOperation(String key, Object value) {
        operations.add(new SetOperation(key, value));
    }
    
    public void addRemoveOperation(String key) {
        operations.add(new RemoveOperation(key));
    }
    
    public void addRenameOperation(String oldKey, String newKey) {
        operations.add(new RenameOperation(oldKey, newKey));
    }
    
    public void addCommentOperation(String key, String comment) {
        operations.add(new SetOperation(key, null, comment));
    }
    
    public BatchResult execute(Path configPath) {
        BatchResult result = new BatchResult();
        
        try {
            ConfigEditor editor = ConfigEditor.load(configPath);
            
            for (BatchOperation op : operations) {
                try {
                    op.apply(editor);
                } catch (Exception e) {
                    result.addError(new BatchError(configPath, op.getDescription(), e.getMessage()));
                }
            }
            
            editor.save();
            result.addProcessedFile(configPath);
            
        } catch (Exception e) {
            result.addError(new BatchError(configPath, "load/save", e.getMessage()));
        }
        
        return result;
    }
    
    public BatchResult execute(List<Path> configPaths) {
        BatchResult result = new BatchResult();
        
        for (Path path : configPaths) {
            BatchResult singleResult = execute(path);
            
            result.setSuccessCount(result.getSuccessCount() + singleResult.getSuccessCount());
            result.setFailCount(result.getFailCount() + singleResult.getFailCount());
            result.getErrors().addAll(singleResult.getErrors());
            result.getProcessedFiles().addAll(singleResult.getProcessedFiles());
        }
        
        return result;
    }
    
    public BatchResult execute(ConfigEditor editor) {
        BatchResult result = new BatchResult();
        
        for (BatchOperation op : operations) {
            try {
                op.apply(editor);
            } catch (Exception e) {
                result.addError(new BatchError(null, op.getDescription(), e.getMessage()));
            }
        }
        
        if (result.getErrors().isEmpty()) {
            result.setSuccessCount(operations.size());
        }
        
        return result;
    }
    
    public void preview(Path configPath) {
        System.out.println("Preview of batch operations for: " + configPath);
        System.out.println("===========================================");
        
        for (int i = 0; i < operations.size(); i++) {
            System.out.println((i + 1) + ". " + operations.get(i).getDescription());
        }
    }
    
    public void saveScript(Path output) throws IOException {
        JsonParser parser = new JsonParser();
        Map<String, Object> script = new LinkedHashMap<>();
        script.put("name", "Batch Script");
        script.put("description", "Generated batch script");
        
        List<Map<String, Object>> opsList = new ArrayList<>();
        for (BatchOperation op : operations) {
            Map<String, Object> opMap = new LinkedHashMap<>();
            opMap.put("key", op.getKey());
            
            if (op instanceof SetOperation) {
                SetOperation setOp = (SetOperation) op;
                opMap.put("type", "set");
                opMap.put("value", setOp.getValue());
                if (setOp.getComment() != null) {
                    opMap.put("comment", setOp.getComment());
                }
            } else if (op instanceof RemoveOperation) {
                opMap.put("type", "remove");
            } else if (op instanceof RenameOperation) {
                RenameOperation renameOp = (RenameOperation) op;
                opMap.put("type", "rename");
                opMap.put("newKey", renameOp.getNewKey());
            }
            
            opsList.add(opMap);
        }
        script.put("operations", opsList);
        
        Files.createDirectories(output.getParent());
        try (OutputStream out = Files.newOutputStream(output)) {
            parser.write(out, script);
        } catch (Exception e) {
            throw new IOException("Failed to save script", e);
        }
    }
    
    public void loadScript(Path input) throws IOException {
        JsonParser parser = new JsonParser();
        try (InputStream in = Files.newInputStream(input)) {
            Map<String, Object> script = parser.parse(in);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> opsList = (List<Map<String, Object>>) script.get("operations");
            if (opsList != null) {
                operations.clear();
                
                for (Map<String, Object> opData : opsList) {
                    String type = (String) opData.get("type");
                    String key = (String) opData.get("key");
                    
                    if ("set".equals(type)) {
                        Object value = opData.get("value");
                        String comment = (String) opData.get("comment");
                        operations.add(new SetOperation(key, value, comment));
                    } else if ("remove".equals(type)) {
                        operations.add(new RemoveOperation(key));
                    } else if ("rename".equals(type)) {
                        String newKey = (String) opData.get("newKey");
                        operations.add(new RenameOperation(key, newKey));
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to load script", e);
        }
    }
    
    public List<BatchOperation> getOperations() {
        return operations;
    }
}