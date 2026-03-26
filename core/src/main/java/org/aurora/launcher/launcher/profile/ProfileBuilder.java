package org.aurora.launcher.launcher.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.aurora.launcher.launcher.profile.GameProfile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProfileBuilder {
    private final GameProfile profile;

    public ProfileBuilder() {
        this.profile = new GameProfile();
        this.profile.setId(UUID.randomUUID().toString());
    }

    public ProfileBuilder name(String name) {
        profile.setName(name);
        return this;
    }

    public ProfileBuilder version(String versionId) {
        profile.setVersionId(versionId);
        return this;
    }

    public ProfileBuilder gameDir(Path gameDir) {
        profile.setGameDir(gameDir);
        return this;
    }

    public ProfileBuilder javaPath(String javaPath) {
        profile.setJavaPath(javaPath);
        return this;
    }

    public ProfileBuilder memoryPreset(String preset) {
        profile.setMemoryPreset(preset);
        profile.setAutoMemory(true);
        return this;
    }

    public ProfileBuilder customMemory(long minMB, long maxMB) {
        profile.setMinMemory(minMB);
        profile.setMaxMemory(maxMB);
        profile.setAutoMemory(false);
        return this;
    }

    public ProfileBuilder jvmArg(String arg) {
        profile.addJvmArg(arg);
        return this;
    }

    public ProfileBuilder gameArg(String arg) {
        profile.addGameArg(arg);
        return this;
    }

    public ProfileBuilder icon(String icon) {
        profile.setIcon(icon);
        return this;
    }

    public GameProfile build() {
        if (profile.getName() == null || profile.getName().isEmpty()) {
            profile.setName("Minecraft");
        }
        if (profile.getVersionId() == null || profile.getVersionId().isEmpty()) {
            throw new IllegalStateException("Version ID is required");
        }
        profile.setLastPlayed(Instant.now().toString());
        return profile;
    }
}