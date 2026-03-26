package org.aurora.launcher.launcher.memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryPreset {
    public static final MemoryPreset LOW_END = new MemoryPreset(
        "low", "低配模式",
        2L * 1024 * 1024 * 1024,
        0.3
    );

    public static final MemoryPreset STANDARD = new MemoryPreset(
        "standard", "标准模式",
        4L * 1024 * 1024 * 1024,
        0.4
    );

    public static final MemoryPreset HIGH_END = new MemoryPreset(
        "high", "高配模式",
        8L * 1024 * 1024 * 1024,
        0.5
    );

    public static final MemoryPreset EXTREME = new MemoryPreset(
        "extreme", "极致模式",
        16L * 1024 * 1024 * 1024,
        0.6
    );

    public static final MemoryPreset AUTO = new MemoryPreset(
        "auto", "自动",
        Long.MAX_VALUE,
        0.4
    );

    private final String id;
    private final String name;
    private final long maxHeapSize;
    private final double systemMemoryRatio;

    public MemoryPreset(String id, String name, long maxHeapSize, double systemMemoryRatio) {
        this.id = id;
        this.name = name;
        this.maxHeapSize = maxHeapSize;
        this.systemMemoryRatio = systemMemoryRatio;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getMaxHeapSize() {
        return maxHeapSize;
    }

    public double getSystemMemoryRatio() {
        return systemMemoryRatio;
    }

    public long apply(long recommendedMemory, long systemMemory) {
        long maxAllowed = (long) (systemMemory * systemMemoryRatio);
        long result = Math.min(recommendedMemory, maxAllowed);
        result = Math.min(result, maxHeapSize);
        return result;
    }

    public static MemoryPreset fromId(String id) {
        if (id == null) return AUTO;
        switch (id.toLowerCase()) {
            case "low":
                return LOW_END;
            case "standard":
                return STANDARD;
            case "high":
                return HIGH_END;
            case "extreme":
                return EXTREME;
            default:
                return AUTO;
        }
    }

    public static List<MemoryPreset> getAllPresets() {
        List<MemoryPreset> presets = new ArrayList<>();
        presets.add(AUTO);
        presets.add(LOW_END);
        presets.add(STANDARD);
        presets.add(HIGH_END);
        presets.add(EXTREME);
        return presets;
    }
}