package org.aurora.launcher.launcher.launch;

import org.aurora.launcher.account.model.Account;
import org.aurora.launcher.launcher.java.JavaVersion;
import org.aurora.launcher.launcher.memory.MemoryConfig;
import org.aurora.launcher.launcher.profile.GameProfile;
import org.aurora.launcher.launcher.version.VersionInfo;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

public class LaunchProfile {
    private String instanceId;
    private VersionInfo version;
    private GameProfile gameConfig;
    private Account account;
    private JavaVersion javaVersion;
    private MemoryConfig memoryConfig;
    private Path gameDir;
    private List<String> customJvmArgs;
    private List<String> customGameArgs;
    private Instant creationTime;

    public LaunchProfile() {
        this.creationTime = Instant.now();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public VersionInfo getVersion() {
        return version;
    }

    public void setVersion(VersionInfo version) {
        this.version = version;
    }

    public GameProfile getGameConfig() {
        return gameConfig;
    }

    public void setGameConfig(GameProfile gameConfig) {
        this.gameConfig = gameConfig;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(JavaVersion javaVersion) {
        this.javaVersion = javaVersion;
    }

    public MemoryConfig getMemoryConfig() {
        return memoryConfig;
    }

    public void setMemoryConfig(MemoryConfig memoryConfig) {
        this.memoryConfig = memoryConfig;
    }

    public Path getGameDir() {
        return gameDir;
    }

    public void setGameDir(Path gameDir) {
        this.gameDir = gameDir;
    }

    public List<String> getCustomJvmArgs() {
        return customJvmArgs;
    }

    public void setCustomJvmArgs(List<String> customJvmArgs) {
        this.customJvmArgs = customJvmArgs;
    }

    public List<String> getCustomGameArgs() {
        return customGameArgs;
    }

    public void setCustomGameArgs(List<String> customGameArgs) {
        this.customGameArgs = customGameArgs;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }
}