package org.aurora.launcher.account.auth;

import org.aurora.launcher.account.model.OfflineAccount;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

public class OfflineAuthProvider implements AuthProvider {

    @Override
    public String getName() {
        return "Offline";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public AuthResult authenticate(AuthRequest request) throws AuthException {
        String username = request.getUsername();
        String uuid = generateOfflineUUID(username);
        
        OfflineAccount account = new OfflineAccount();
        account.setUsername(username);
        account.setDisplayName(username);
        account.setUuid(uuid);
        account.setCreatedAt(Instant.now());
        
        return AuthResult.success(account);
    }

    @Override
    public void cancel() {
    }

    private String generateOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8))
                   .toString();
    }
}