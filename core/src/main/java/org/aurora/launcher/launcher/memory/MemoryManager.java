package org.aurora.launcher.launcher.memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private static final long MIN_MEMORY = 512L * 1024 * 1024;
    
    private final MemoryCalculator calculator;
    private final JvmOptimizer optimizer;
    private MemoryPreset currentPreset;

    public MemoryManager() {
        this.calculator = new MemoryCalculator();
        this.optimizer = new JvmOptimizer();
        this.currentPreset = MemoryPreset.AUTO;
    }

    public MemoryConfig calculateOptimal(boolean hasShaders, boolean hasResourcePacks, int modCount) {
        long systemMemory = calculator.getSystemMemory();
        long recommendedMemory = calculator.calculateRecommended(hasShaders, hasResourcePacks, modCount);
        
        long finalMemory = currentPreset.apply(recommendedMemory, systemMemory);
        
        long initialHeap = (long) (finalMemory * 0.6);
        long maxHeap = finalMemory;
        
        initialHeap = Math.max(MIN_MEMORY, initialHeap);
        maxHeap = Math.max(MIN_MEMORY, maxHeap);
        
        List<String> jvmArgs = buildJvmArguments(maxHeap);
        
        return new MemoryConfig(initialHeap, maxHeap, jvmArgs);
    }

    public MemoryConfig calculateCustom(long minMemoryMB, long maxMemoryMB) {
        long systemMemory = calculator.getSystemMemory();
        
        long minBytes = Math.min(minMemoryMB * 1024 * 1024, systemMemory * 80 / 100);
        long maxBytes = Math.min(maxMemoryMB * 1024 * 1024, systemMemory * 80 / 100);
        
        minBytes = Math.max(MIN_MEMORY, minBytes);
        maxBytes = Math.max(MIN_MEMORY, maxBytes);
        
        List<String> jvmArgs = buildJvmArguments(maxBytes);
        
        return new MemoryConfig(minBytes, maxBytes, jvmArgs);
    }

    private List<String> buildJvmArguments(long heapSize) {
        List<String> args = new ArrayList<>();
        
        long heapMB = heapSize / (1024 * 1024);
        
        if (heapMB <= 2048) {
            args.addAll(optimizer.buildLowMemoryArguments(heapMB));
        } else if (heapMB >= 8192) {
            args.addAll(optimizer.buildOptimizedArguments(heapMB));
        } else {
            args.addAll(optimizer.buildG1GCArguments(heapSize));
        }
        
        return args;
    }

    public MemoryPreset getCurrentPreset() {
        return currentPreset;
    }

    public void setCurrentPreset(MemoryPreset preset) {
        this.currentPreset = preset != null ? preset : MemoryPreset.AUTO;
    }

    public void setCurrentPreset(String presetId) {
        this.currentPreset = MemoryPreset.fromId(presetId);
    }

    public long getSystemMemory() {
        return calculator.getSystemMemory();
    }

    public long getAvailableMemory() {
        return calculator.getAvailableMemory();
    }

    public MemoryCalculator getCalculator() {
        return calculator;
    }

    public JvmOptimizer getOptimizer() {
        return optimizer;
    }
}