package org.aurora.launcher.account.skin;

import org.aurora.launcher.account.model.Account;
import org.aurora.launcher.account.model.SkinProfile;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class SkinManager {
    private SkinService skinService;
    private Path skinCacheDir;

    public SkinManager() {
        this.skinService = new SkinService();
    }

    public SkinProfile getSkin(Account account) throws SkinException {
        return getSkin(account.getUuid());
    }

    public SkinProfile getSkin(String uuid) throws SkinException {
        return skinService.fetchFromMojang(uuid);
    }

    public CompletableFuture<SkinProfile> getSkinAsync(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getSkin(uuid);
            } catch (SkinException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void uploadSkin(Account account, Path skinFile, String model) throws SkinException {
        throw new SkinException("Upload not implemented");
    }

    public void resetSkin(Account account) throws SkinException {
        throw new SkinException("Reset not implemented");
    }

    public void setCape(Account account, String capeId) throws SkinException {
        throw new SkinException("Cape not implemented");
    }
}