package org.aurora.launcher.launcher.java;

import org.aurora.launcher.core.platform.Platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JavaDetector {
    private final List<JavaVersion> detectedVersions = new ArrayList<>();

    public CompletableFuture<List<JavaVersion>> detectInstalled() {
        return CompletableFuture.supplyAsync(() -> {
            List<JavaVersion> versions = new ArrayList<>();
            
            detectFromJavaHome(versions);
            detectFromCommonPaths(versions);
            
            return versions;
        });
    }

    private void detectFromJavaHome(List<JavaVersion> versions) {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            JavaVersion version = detectFromPath(Paths.get(javaHome));
            if (version != null) {
                versions.add(version);
            }
        }
    }

    private void detectFromCommonPaths(List<JavaVersion> versions) {
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("windows")) {
            detectWindowsJava(versions);
        } else if (osName.contains("mac")) {
            detectMacJava(versions);
        } else {
            detectLinuxJava(versions);
        }
    }

    private void detectWindowsJava(List<JavaVersion> versions) {
        String[] programFiles = {
            System.getenv("ProgramFiles"),
            System.getenv("ProgramFiles(x86)")
        };
        
        for (String pf : programFiles) {
            if (pf == null) continue;
            Path javaDir = Paths.get(pf).resolve("Java");
            if (Files.exists(javaDir)) {
                try {
                    Files.list(javaDir)
                        .filter(Files::isDirectory)
                        .filter(p -> p.getFileName().toString().toLowerCase().contains("jdk") ||
                                    p.getFileName().toString().toLowerCase().contains("jre"))
                        .forEach(p -> {
                            JavaVersion version = detectFromPath(p);
                            if (version != null && !containsPath(versions, p.toString())) {
                                versions.add(version);
                            }
                        });
                } catch (Exception ignored) {
                }
            }
        }
        
        String eclipseAdoptium = System.getenv("ProgramFiles");
        if (eclipseAdoptium != null) {
            Path adoptiumDir = Paths.get(eclipseAdoptium).resolve("Eclipse Adoptium");
            if (Files.exists(adoptiumDir)) {
                try {
                    Files.list(adoptiumDir)
                        .filter(Files::isDirectory)
                        .forEach(p -> {
                            JavaVersion version = detectFromPath(p);
                            if (version != null && !containsPath(versions, p.toString())) {
                                versions.add(version);
                            }
                        });
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void detectMacJava(List<JavaVersion> versions) {
        Path javaVms = Paths.get("/Library/Java/JavaVirtualMachines");
        if (Files.exists(javaVms)) {
            try {
                Files.list(javaVms)
                    .filter(Files::isDirectory)
                    .forEach(vmDir -> {
                        Path contentsHome = vmDir.resolve("Contents").resolve("Home");
                        if (Files.exists(contentsHome)) {
                            JavaVersion version = detectFromPath(contentsHome);
                            if (version != null && !containsPath(versions, contentsHome.toString())) {
                                versions.add(version);
                            }
                        }
                    });
            } catch (Exception ignored) {
            }
        }
    }

    private void detectLinuxJava(List<JavaVersion> versions) {
        Path jvmDir = Paths.get("/usr/lib/jvm");
        if (Files.exists(jvmDir)) {
            try {
                Files.list(jvmDir)
                    .filter(Files::isDirectory)
                    .forEach(p -> {
                        JavaVersion version = detectFromPath(p);
                        if (version != null && !containsPath(versions, p.toString())) {
                            versions.add(version);
                        }
                    });
            } catch (Exception ignored) {
            }
        }
    }

    public JavaVersion detectFromPath(Path javaHome) {
        if (javaHome == null || !Files.exists(javaHome)) {
            return null;
        }
        
        Path javaExe = getJavaExecutable(javaHome);
        if (javaExe == null || !Files.exists(javaExe)) {
            return null;
        }
        
        try {
            ProcessBuilder pb = new ProcessBuilder(javaExe.toString(), "-version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            
            if (line != null && line.contains("version")) {
                JavaVersion version = new JavaVersion();
                version.setPath(javaHome.toString());
                
                int majorVersion = parseMajorVersion(line);
                version.setMajorVersion(majorVersion);
                version.setVersion(line);
                
                if (line.toLowerCase().contains("openjdk")) {
                    version.setVendor("OpenJDK");
                } else if (line.toLowerCase().contains("oracle")) {
                    version.setVendor("Oracle");
                } else if (line.toLowerCase().contains("adoptium") || line.toLowerCase().contains("temurin")) {
                    version.setVendor("Eclipse Adoptium");
                }
                
                String osArch = System.getProperty("os.arch");
                version.setArchitecture(osArch.contains("64") ? "64-bit" : "32-bit");
                
                return version;
            }
        } catch (Exception ignored) {
        }
        
        return null;
    }

    private Path getJavaExecutable(Path javaHome) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return javaHome.resolve("bin").resolve("java.exe");
        } else {
            return javaHome.resolve("bin").resolve("java");
        }
    }

    private int parseMajorVersion(String versionLine) {
        int start = versionLine.indexOf("\"");
        int end = versionLine.lastIndexOf("\"");
        if (start >= 0 && end > start) {
            String version = versionLine.substring(start + 1, end);
            
            if (version.startsWith("1.")) {
                version = version.substring(2);
            }
            
            int dotIndex = version.indexOf(".");
            if (dotIndex > 0) {
                version = version.substring(0, dotIndex);
            }
            
            try {
                return Integer.parseInt(version);
            } catch (NumberFormatException ignored) {
            }
        }
        return 8;
    }

    private boolean containsPath(List<JavaVersion> versions, String path) {
        return versions.stream().anyMatch(v -> path.equals(v.getPath()));
    }
}