package org.aurora.launcher.api.unified;

import org.aurora.launcher.api.curseforge.CurseForgeClient;
import org.aurora.launcher.api.curseforge.CurseForgeFile;
import org.aurora.launcher.api.curseforge.CurseForgeMod;
import org.aurora.launcher.api.modrinth.ModrinthClient;
import org.aurora.launcher.api.modrinth.ModrinthProject;
import org.aurora.launcher.api.modrinth.ModrinthVersion;
import org.aurora.launcher.api.modrinth.ModrinthFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UnifiedSearch {
    
    private final ModrinthClient modrinthClient;
    private CurseForgeClient curseForgeClient;
    
    public UnifiedSearch() {
        this.modrinthClient = new ModrinthClient();
    }
    
    public UnifiedSearch(ModrinthClient modrinthClient) {
        this.modrinthClient = modrinthClient;
    }
    
    public void setCurseForgeClient(CurseForgeClient curseForgeClient) {
        this.curseForgeClient = curseForgeClient;
    }
    
    public CompletableFuture<List<UnifiedMod>> search(String query) {
        return search(query, SearchOptions.builder().build());
    }
    
    public CompletableFuture<List<UnifiedMod>> search(String query, SearchOptions options) {
        List<CompletableFuture<List<UnifiedMod>>> futures = new ArrayList<>();
        
        futures.add(searchModrinth(query, options));
        
        if (curseForgeClient != null) {
            futures.add(searchCurseForge(query, options));
        }
        
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        
        return allOf.thenApply(v -> {
            List<UnifiedMod> results = new ArrayList<>();
            for (CompletableFuture<List<UnifiedMod>> future : futures) {
                results.addAll(future.join());
            }
            
            results.sort(Comparator.comparingLong(UnifiedMod::getDownloads).reversed());
            
            if (results.size() > options.getLimit()) {
                results = results.subList(0, options.getLimit());
            }
            
            return results;
        });
    }
    
    private CompletableFuture<List<UnifiedMod>> searchModrinth(String query, SearchOptions options) {
        return modrinthClient.search(query, options.getGameVersion(), options.getOffset(), options.getLimit())
                .thenApply(result -> {
                    List<UnifiedMod> mods = new ArrayList<>();
                    if (result != null && result.getHits() != null) {
                        for (ModrinthProject project : result.getHits()) {
                            mods.add(toUnifiedMod(project));
                        }
                    }
                    return mods;
                })
                .exceptionally(e -> new ArrayList<>());
    }
    
    private CompletableFuture<List<UnifiedMod>> searchCurseForge(String query, SearchOptions options) {
        if (curseForgeClient == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        return curseForgeClient.searchMods(query, options.getGameVersion(), options.getOffset(), options.getLimit())
                .thenApply(result -> {
                    List<UnifiedMod> mods = new ArrayList<>();
                    if (result != null && result.getData() != null) {
                        for (CurseForgeMod mod : result.getData()) {
                            mods.add(toUnifiedMod(mod));
                        }
                    }
                    return mods;
                })
                .exceptionally(e -> new ArrayList<>());
    }
    
    public CompletableFuture<UnifiedMod> getMod(String id, String source) {
        if ("modrinth".equalsIgnoreCase(source)) {
            return modrinthClient.getProject(id).thenApply(this::toUnifiedMod);
        } else if ("curseforge".equalsIgnoreCase(source) && curseForgeClient != null) {
            return curseForgeClient.getMod(Integer.parseInt(id)).thenApply(this::toUnifiedMod);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<List<UnifiedVersion>> getVersions(String id, String source) {
        if ("modrinth".equalsIgnoreCase(source)) {
            return modrinthClient.getVersions(id).thenApply(this::toUnifiedVersions);
        } else if ("curseforge".equalsIgnoreCase(source) && curseForgeClient != null) {
            return curseForgeClient.getFiles(Integer.parseInt(id)).thenApply(this::toUnifiedVersionsFromFiles);
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
    
    private UnifiedMod toUnifiedMod(ModrinthProject project) {
        UnifiedMod mod = new UnifiedMod();
        mod.setId(project.getId());
        mod.setSource("modrinth");
        mod.setName(project.getName());
        mod.setSlug(project.getSlug());
        mod.setDescription(project.getDescription());
        mod.setIconUrl(project.getIconUrl());
        mod.setDownloads(project.getDownloads());
        mod.setCategories(project.getCategories());
        mod.setGameVersions(project.getGameVersions());
        mod.setLoaders(project.getLoaders());
        mod.setPageUrl("https://modrinth.com/mod/" + project.getSlug());
        return mod;
    }
    
    private UnifiedMod toUnifiedMod(CurseForgeMod cfMod) {
        UnifiedMod mod = new UnifiedMod();
        mod.setId(String.valueOf(cfMod.getId()));
        mod.setSource("curseforge");
        mod.setName(cfMod.getName());
        mod.setSlug(cfMod.getSlug());
        mod.setDescription(cfMod.getSummary());
        mod.setIconUrl(cfMod.getLogoUrl());
        mod.setDownloads(cfMod.getDownloadCount());
        mod.setPageUrl(cfMod.getWebsiteUrl());
        return mod;
    }
    
    private List<UnifiedVersion> toUnifiedVersions(List<ModrinthVersion> versions) {
        List<UnifiedVersion> result = new ArrayList<>();
        if (versions != null) {
            for (ModrinthVersion version : versions) {
                result.add(toUnifiedVersion(version));
            }
        }
        return result;
    }
    
    private UnifiedVersion toUnifiedVersion(ModrinthVersion version) {
        UnifiedVersion uv = new UnifiedVersion();
        uv.setId(version.getId());
        uv.setSource("modrinth");
        uv.setVersionNumber(version.getVersionNumber());
        uv.setName(version.getName());
        uv.setChangelog(version.getChangelog());
        uv.setType(version.getVersionType());
        uv.setGameVersions(version.getGameVersions());
        uv.setLoaders(version.getLoaders());
        uv.setDatePublished(version.getDatePublished());
        
        List<UnifiedVersion.DownloadFile> files = new ArrayList<>();
        if (version.getFiles() != null) {
            for (ModrinthFile file : version.getFiles()) {
                UnifiedVersion.DownloadFile df = new UnifiedVersion.DownloadFile();
                df.setUrl(file.getUrl());
                df.setFilename(file.getFilename());
                df.setSize(file.getSize());
                df.setSha1(file.getSha1());
                df.setPrimary(file.isPrimary());
                files.add(df);
            }
        }
        uv.setFiles(files);
        
        return uv;
    }
    
    private List<UnifiedVersion> toUnifiedVersionsFromFiles(List<CurseForgeFile> files) {
        List<UnifiedVersion> result = new ArrayList<>();
        if (files != null) {
            for (CurseForgeFile file : files) {
                UnifiedVersion uv = new UnifiedVersion();
                uv.setId(String.valueOf(file.getId()));
                uv.setSource("curseforge");
                uv.setName(file.getDisplayName());
                uv.setGameVersions(file.getGameVersions());
                uv.setDatePublished(file.getFileDate());
                
                UnifiedVersion.DownloadFile df = new UnifiedVersion.DownloadFile();
                df.setUrl(file.getDownloadUrl());
                df.setFilename(file.getFileName());
                df.setSize(file.getFileLength());
                df.setPrimary(true);
                uv.setFiles(java.util.Collections.singletonList(df));
                
                result.add(uv);
            }
        }
        return result;
    }
}