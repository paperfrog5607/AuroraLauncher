package org.aurora.launcher.launcher.memory;

import java.util.ArrayList;
import java.util.List;

public class JvmOptimizer {

    public List<String> buildG1GCArguments(long heapSize) {
        List<String> args = new ArrayList<>();
        
        args.add("-XX:+UseG1GC");
        args.add("-XX:+ParallelRefProcEnabled");
        args.add("-XX:MaxGCPauseMillis=200");
        
        args.add("-XX:InitiatingHeapOccupancyPercent=10");
        args.add("-XX:G1NewSizePercent=5");
        args.add("-XX:G1MaxNewSizePercent=30");
        
        args.add("-XX:+UnlockExperimentalVMOptions");
        args.add("-XX:G1NewSizePercent=20");
        args.add("-XX:G1ReservePercent=20");
        
        args.add("-XX:+UseStringDeduplication");
        
        return args;
    }

    public List<String> buildZGCArguments(long heapSize) {
        List<String> args = new ArrayList<>();
        
        args.add("-XX:+UseZGC");
        args.add("-XX:+ZGenerational");
        args.add("-XX:ConcGCThreads=2");
        
        return args;
    }

    public List<String> buildOptimizedArguments(long heapSizeMB) {
        List<String> args = new ArrayList<>();
        
        args.add("-XX:+UnlockExperimentalVMOptions");
        args.add("-XX:+UseG1GC");
        args.add("-XX:G1NewSizePercent=20");
        args.add("-XX:G1ReservePercent=20");
        args.add("-XX:MaxGCPauseMillis=50");
        args.add("-XX:G1HeapRegionSize=32M");
        
        return args;
    }

    public List<String> buildLowMemoryArguments(long heapSizeMB) {
        List<String> args = new ArrayList<>();
        
        args.add("-XX:+UseG1GC");
        args.add("-XX:MaxGCPauseMillis=130");
        args.add("-XX:G1NewSizePercent=5");
        args.add("-XX:G1MaxNewSizePercent=15");
        
        return args;
    }
}