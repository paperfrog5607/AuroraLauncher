package org.aurora.launcher.resource.texture;

import java.awt.Image;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TextureExtractor {
    
    public CompletableFuture<List<TexturePreview>> extractFromMod(Path modFile) {
        return extractFromArchive(modFile, "assets");
    }
    
    public CompletableFuture<List<TexturePreview>> extractFromResourcePack(Path packPath) {
        return extractFromArchive(packPath, "");
    }
    
    private CompletableFuture<List<TexturePreview>> extractFromArchive(Path archivePath, String basePath) {
        return CompletableFuture.supplyAsync(() -> {
            List<TexturePreview> textures = new ArrayList<>();
            
            if (!Files.exists(archivePath) || !archivePath.toString().endsWith(".zip")) {
                return textures;
            }
            
            try (ZipFile zip = new ZipFile(archivePath.toFile())) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    
                    if (isTextureFile(name)) {
                        try (InputStream is = zip.getInputStream(entry)) {
                            Image image = loadImage(is);
                            if (image != null) {
                                TexturePreview preview = new TexturePreview(image, name);
                                textures.add(preview);
                            }
                        } catch (Exception e) {
                            // Skip invalid textures
                        }
                    }
                }
            } catch (IOException e) {
                // Return what we have
            }
            
            return textures;
        });
    }
    
    public CompletableFuture<Image> extractSingle(Path filePath, String texturePath) {
        return CompletableFuture.supplyAsync(() -> {
            if (!Files.exists(filePath)) {
                return null;
            }
            
            if (Files.isDirectory(filePath)) {
                Path textureFile = filePath.resolve(texturePath);
                if (Files.exists(textureFile)) {
                    try (InputStream is = Files.newInputStream(textureFile)) {
                        return loadImage(is);
                    } catch (IOException e) {
                        return null;
                    }
                }
            } else if (filePath.toString().endsWith(".zip")) {
                try (ZipFile zip = new ZipFile(filePath.toFile())) {
                    ZipEntry entry = zip.getEntry(texturePath);
                    if (entry != null) {
                        try (InputStream is = zip.getInputStream(entry)) {
                            return loadImage(is);
                        }
                    }
                } catch (IOException e) {
                    return null;
                }
            }
            
            return null;
        });
    }
    
    public CompletableFuture<List<TexturePreview>> extractFromFolder(Path folderPath) {
        return CompletableFuture.supplyAsync(() -> {
            List<TexturePreview> textures = new ArrayList<>();
            
            if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                return textures;
            }
            
            try (Stream<Path> stream = Files.walk(folderPath)) {
                stream.filter(p -> isTextureFile(p.toString()))
                      .forEach(p -> {
                          try (InputStream is = Files.newInputStream(p)) {
                              Image image = loadImage(is);
                              if (image != null) {
                                  String relativePath = folderPath.relativize(p).toString().replace('\\', '/');
                                  TexturePreview preview = new TexturePreview(image, relativePath);
                                  textures.add(preview);
                              }
                          } catch (Exception e) {
                              // Skip invalid textures
                          }
                      });
            } catch (IOException e) {
                // Return what we have
            }
            
            return textures;
        });
    }
    
    private boolean isTextureFile(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".png") && !lower.contains(".mcmeta");
    }
    
    private Image loadImage(InputStream is) throws IOException {
        return javax.imageio.ImageIO.read(is);
    }
}