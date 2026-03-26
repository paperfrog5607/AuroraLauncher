package org.aurora.launcher.resource.datapack;

import org.aurora.launcher.resource.resourcepack.PackMeta;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DataPackParser {
    
    public DataPack parse(Path packPath) throws IOException {
        DataPack pack = new DataPack();
        pack.setFilePath(packPath);
        
        String fileName = packPath.getFileName().toString();
        pack.setId(fileName.replaceAll("\\.[^.]+$", ""));
        pack.setName(fileName);
        
        if (Files.isDirectory(packPath)) {
            pack.setType(DataPack.DataPackType.FOLDER);
            parseFolder(packPath, pack);
        } else if (fileName.endsWith(".zip")) {
            pack.setType(DataPack.DataPackType.ZIP);
            parseZip(packPath, pack);
        }
        
        pack.setFileSize(Files.size(packPath));
        
        return pack;
    }
    
    private void parseFolder(Path folderPath, DataPack pack) throws IOException {
        Path mcmetaPath = folderPath.resolve("pack.mcmeta");
        if (Files.exists(mcmetaPath)) {
            byte[] data = Files.readAllBytes(mcmetaPath);
            PackMeta meta = PackMeta.parse(data);
            pack.setPackFormat(meta.getPackFormat());
            pack.setDescription(meta.getDescription());
        }
        
        Path dataPath = folderPath.resolve("data");
        if (Files.exists(dataPath)) {
            pack.setNamespaces(scanNamespacesFolder(dataPath));
        }
    }
    
    private void parseZip(Path zipPath, DataPack pack) throws IOException {
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
            
            pack.setNamespaces(scanNamespacesZip(zipFile));
        }
    }
    
    private List<String> scanNamespacesFolder(Path dataPath) throws IOException {
        List<String> namespaces = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dataPath)) {
            stream.filter(Files::isDirectory)
                  .map(p -> p.getFileName().toString())
                  .forEach(namespaces::add);
        }
        return namespaces;
    }
    
    private List<String> scanNamespacesZip(ZipFile zipFile) {
        Set<String> namespaces = new HashSet<>();
        
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            
            if (name.startsWith("data/") && name.length() > 5) {
                int slashIndex = name.indexOf('/', 5);
                if (slashIndex > 5) {
                    namespaces.add(name.substring(5, slashIndex));
                }
            }
        }
        
        return new ArrayList<>(namespaces);
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