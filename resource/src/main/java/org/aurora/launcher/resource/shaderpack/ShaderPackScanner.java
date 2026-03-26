package org.aurora.launcher.resource.shaderpack;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ShaderPackScanner {
    
    public List<ShaderPack> scan(Path dir) throws IOException {
        List<ShaderPack> packs = new ArrayList<>();
        
        if (!Files.exists(dir)) {
            return packs;
        }
        
        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> files = stream.collect(Collectors.toList());
            
            for (Path file : files) {
                try {
                    String fileName = file.getFileName().toString();
                    if (fileName.endsWith(".zip")) {
                        ShaderPack pack = parseZip(file);
                        if (pack != null) {
                            packs.add(pack);
                        }
                    }
                } catch (Exception e) {
                    // Skip invalid packs
                }
            }
        }
        
        return packs;
    }
    
    private ShaderPack parseZip(Path zipPath) throws IOException {
        ShaderPack pack = new ShaderPack();
        pack.setFilePath(zipPath);
        
        String fileName = zipPath.getFileName().toString();
        pack.setId(fileName.replace(".zip", ""));
        pack.setName(fileName);
        pack.setFileSize(Files.size(zipPath));
        
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            // Detect shader type
            if (hasOptifineShaders(zipFile)) {
                pack.setType(ShaderPack.ShaderPackType.OPTIFINE);
            } else if (hasIrisShaders(zipFile)) {
                pack.setType(ShaderPack.ShaderPackType.IRIS);
            } else {
                pack.setType(ShaderPack.ShaderPackType.VANILLA);
            }
            
            // Try to read description from shaders settings
            ZipEntry shaderEntry = zipFile.getEntry("shaders/shaders.properties");
            if (shaderEntry != null) {
                try (InputStream is = zipFile.getInputStream(shaderEntry)) {
                    Properties props = new Properties();
                    props.load(is);
                    pack.setDescription(props.getProperty("screen.name", ""));
                }
            }
            
            // Try to load icon
            ZipEntry iconEntry = zipFile.getEntry("pack.png");
            if (iconEntry != null) {
                try (InputStream is = zipFile.getInputStream(iconEntry)) {
                    pack.setIcon(javax.imageio.ImageIO.read(is));
                } catch (Exception e) {
                    // Ignore icon loading errors
                }
            }
        }
        
        return pack;
    }
    
    private boolean hasOptifineShaders(ZipFile zipFile) {
        return zipFile.getEntry("shaders/shaders.properties") != null ||
               zipFile.getEntry("shaders/gbuffers_basic.vsh") != null;
    }
    
    private boolean hasIrisShaders(ZipFile zipFile) {
        return zipFile.getEntry("shaders/final.vsh") != null ||
               zipFile.getEntry("shaders/composite.vsh") != null;
    }
}