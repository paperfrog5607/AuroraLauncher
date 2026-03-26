package org.aurora.launcher.resource.resourcepack;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackParser {
    
    public ResourcePack parse(Path packPath) throws IOException {
        ResourcePack pack = new ResourcePack();
        pack.setFilePath(packPath);
        
        String fileName = packPath.getFileName().toString();
        pack.setId(fileName.replaceAll("\\.[^.]+$", ""));
        pack.setName(fileName);
        
        if (Files.isDirectory(packPath)) {
            pack.setType(ResourcePack.ResourcePackType.FOLDER);
            parseFolder(packPath, pack);
        } else if (fileName.endsWith(".zip")) {
            pack.setType(ResourcePack.ResourcePackType.ZIP);
            parseZip(packPath, pack);
        }
        
        pack.setFileSize(Files.size(packPath));
        
        return pack;
    }
    
    private void parseFolder(Path folderPath, ResourcePack pack) throws IOException {
        Path mcmetaPath = folderPath.resolve("pack.mcmeta");
        if (Files.exists(mcmetaPath)) {
            byte[] data = Files.readAllBytes(mcmetaPath);
            PackMeta meta = PackMeta.parse(data);
            pack.setPackFormat(meta.getPackFormat());
            pack.setDescription(meta.getDescription());
        }
        
        Path iconPath = folderPath.resolve("pack.png");
        if (Files.exists(iconPath)) {
            try {
                pack.setIcon(javax.imageio.ImageIO.read(iconPath.toFile()));
            } catch (Exception e) {
                // Ignore icon loading errors
            }
        }
    }
    
    private void parseZip(Path zipPath, ResourcePack pack) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            ZipEntry mcmetaEntry = zipFile.getEntry("pack.mcmeta");
            if (mcmetaEntry != null) {
                try (InputStream is = zipFile.getInputStream(mcmetaEntry)) {
                    byte[] data = readAllBytes(is);
                    PackMeta meta = PackMeta.parse(data);
                    pack.setPackFormat(meta.getPackFormat());
                    pack.setDescription(meta.getDescription());
                }
            }
            
            ZipEntry iconEntry = zipFile.getEntry("pack.png");
            if (iconEntry != null) {
                try (InputStream is = zipFile.getInputStream(iconEntry)) {
                    pack.setIcon(javax.imageio.ImageIO.read(is));
                } catch (Exception e) {
                    // Ignore icon loading errors
                }
            }
        }
    }
    
    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}