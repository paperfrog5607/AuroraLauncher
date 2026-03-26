package org.aurora.launcher.core.platform;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public final class Platform {

    public enum OS {
        WINDOWS,
        MACOS,
        LINUX,
        UNKNOWN
    }

    public enum Arch {
        X86,
        X64,
        ARM64,
        UNKNOWN
    }

    private static final OS currentOS = detectOS();
    private static final Arch currentArch = detectArch();

    private Platform() {
    }

    public static OS getOS() {
        return currentOS;
    }

    public static Arch getArch() {
        return currentArch;
    }

    public static String getOSVersion() {
        return System.getProperty("os.version", "unknown");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version", "unknown");
    }

    public static String getJavaHome() {
        return System.getProperty("java.home", "");
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getAvailableMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
    }

    public static String getWorkingDirectory() {
        return System.getProperty("user.dir", ".");
    }

    public static Path getAppDataDirectory() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH);
        String userHome = System.getProperty("user.home", "");

        if (osName.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isEmpty()) {
                return Paths.get(appData);
            }
            return Paths.get(userHome, "AppData", "Roaming");
        } else if (osName.contains("mac")) {
            return Paths.get(userHome, "Library", "Application Support");
        } else {
            String xdgDataHome = System.getenv("XDG_DATA_HOME");
            if (xdgDataHome != null && !xdgDataHome.isEmpty()) {
                return Paths.get(xdgDataHome);
            }
            return Paths.get(userHome, ".local", "share");
        }
    }

    private static OS detectOS() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH);
        
        if (osName.contains("win")) {
            return OS.WINDOWS;
        } else if (osName.contains("mac")) {
            return OS.MACOS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OS.LINUX;
        }
        return OS.UNKNOWN;
    }

    private static Arch detectArch() {
        String osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ENGLISH);
        
        if (osArch.contains("aarch64") || osArch.contains("arm64")) {
            return Arch.ARM64;
        } else if (osArch.contains("64")) {
            return Arch.X64;
        } else if (osArch.contains("86") || osArch.contains("32")) {
            return Arch.X86;
        }
        return Arch.UNKNOWN;
    }
}