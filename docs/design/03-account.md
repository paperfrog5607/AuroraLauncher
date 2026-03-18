# 模块设计：account（账号系统模块）

## 概述

账号系统模块负责用户账号的管理，包括微软OAuth登录、离线模式、第三方认证服务支持，以及账号存储和皮肤系统。

## 子包结构

```
org.aurora.launcher.account/
├── auth/             # 认证服务
├── storage/          # 账号存储
├── skin/             # 皮肤系统
├── session/          # 会话管理
└── model/            # 数据模型
```

## 详细设计

### 1. model（数据模型）

**Account**
```java
public abstract class Account {
    protected String id;
    protected String username;
    protected String displayName;
    protected String uuid;
    protected AccountType type;
    protected Instant createdAt;
    protected Instant lastUsed;
    protected boolean selected;
    
    public abstract boolean isValid();
    public abstract void refresh() throws AuthException;
    public abstract String getAccessToken();
    public abstract void logout();
    
    public enum AccountType {
        MICROSOFT, OFFLINE, CUSTOM
    }
}

public class MicrosoftAccount extends Account {
    private String accessToken;
    private String refreshToken;
    private String xboxToken;
    private String minecraftToken;
    private Instant tokenExpiry;
    private SkinProfile skin;
    
    @Override
    public boolean isValid();
    
    @Override
    public void refresh() throws AuthException;
    
    @Override
    public String getAccessToken();
}

public class OfflineAccount extends Account {
    private String uuid;
    
    @Override
    public boolean isValid() { return true; }
    
    @Override
    public void refresh() {}
    
    @Override
    public String getAccessToken() { return ""; }
}

public class CustomAccount extends Account {
    private String authServerUrl;
    private String accessToken;
    private String refreshToken;
    private Instant tokenExpiry;
    
    @Override
    public boolean isValid();
    
    @Override
    public void refresh() throws AuthException;
    
    @Override
    public String getAccessToken();
}
```

**SkinProfile**
```java
public class SkinProfile {
    private String skinUrl;
    private String capeUrl;
    private SkinModel model;
    private boolean slim;
    
    public enum SkinModel {
        STEVE, ALEX
    }
    
    public Image getSkinImage();
    public Image getCapeImage();
}
```

### 2. auth（认证服务）

**AuthProvider接口**
```java
public interface AuthProvider {
    String getName();
    boolean isAvailable();
    AuthResult authenticate(AuthRequest request) throws AuthException;
    void cancel();
}
```

**MicrosoftAuthProvider**
```java
public class MicrosoftAuthProvider implements AuthProvider {
    private static final String CLIENT_ID = "...";
    private static final String REDIRECT_URI = "http://localhost:12789/callback";
    
    private HttpServer callbackServer;
    private CompletableFuture<AuthResult> authFuture;
    
    @Override
    public String getName() { return "Microsoft"; }
    
    @Override
    public AuthResult authenticate(AuthRequest request) throws AuthException {
        // 1. 启动本地回调服务器
        // 2. 打开浏览器进行OAuth授权
        // 3. 等待回调获取授权码
        // 4. 用授权码换取访问令牌
        // 5. 获取Xbox Live令牌
        // 6. 获取XSTS令牌
        // 7. 获取Minecraft访问令牌
        // 8. 获取玩家信息
        return result;
    }
    
    @Override
    public void cancel();
    
    public TokenResult refreshMicrosoftToken(String refreshToken) throws AuthException;
    public XboxTokenResult getXboxToken(String accessToken) throws AuthException;
    public XstsTokenResult getXstsToken(String xboxToken) throws AuthException;
    public MinecraftTokenResult getMinecraftToken(String xstsToken, String uhs) throws AuthException;
    public MinecraftProfile getMinecraftProfile(String minecraftToken) throws AuthException;
}
```

**OAuth流程**
```
1. 用户点击登录
   ↓
2. 启动本地HTTP服务器（端口12789）
   ↓
3. 生成OAuth URL并在浏览器打开
   https://login.live.com/oauth20_authorize.srf?
     client_id=...
     &response_type=code
     &redirect_uri=http://localhost:12789/callback
     &scope=XboxLive.signin offline_access
   ↓
4. 用户在浏览器完成登录授权
   ↓
5. 微软重定向到本地服务器，带上授权码
   http://localhost:12789/callback?code=...
   ↓
6. 用授权码换取Microsoft访问令牌
   POST https://login.live.com/oauth20_token.srf
   ↓
7. 用MS令牌获取Xbox Live令牌
   POST https://user.auth.xboxlive.com/user/authenticate
   ↓
8. 用XBL令牌获取XSTS令牌
   POST https://xsts.auth.xboxlive.com/xsts/authorize
   ↓
9. 用XSTS令牌获取Minecraft令牌
   POST https://api.minecraftservices.com/authentication/login_with_xbox
   ↓
10. 用MC令牌获取玩家信息
    GET https://api.minecraftservices.com/minecraft/profile
```

**OfflineAuthProvider**
```java
public class OfflineAuthProvider implements AuthProvider {
    @Override
    public String getName() { return "Offline"; }
    
    @Override
    public boolean isAvailable() { return true; }
    
    @Override
    public AuthResult authenticate(AuthRequest request) throws AuthException {
        String username = request.getUsername();
        String uuid = generateOfflineUUID(username);
        
        OfflineAccount account = new OfflineAccount();
        account.setUsername(username);
        account.setUuid(uuid);
        account.setCreatedAt(Instant.now());
        
        return new AuthResult(account, true);
    }
    
    private String generateOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8))
                   .toString();
    }
}
```

**CustomAuthProvider**
```java
public class CustomAuthProvider implements AuthProvider {
    private String serverUrl;
    private HttpClient httpClient;
    
    @Override
    public String getName() { return serverUrl; }
    
    @Override
    public AuthResult authenticate(AuthRequest request) throws AuthException {
        // 根据自定义服务器协议进行认证
        // 支持常见的第三方认证协议如authlib-injector
        
        // 1. 发送认证请求到自定义服务器
        // 2. 解析响应获取令牌和玩家信息
        // 3. 创建CustomAccount
    }
    
    public boolean validateServer(String url) {
        // 验证服务器是否可用
    }
}
```

**AuthResult**
```java
public class AuthResult {
    private Account account;
    private boolean success;
    private String errorMessage;
    private String errorCode;
    
    public static AuthResult success(Account account);
    public static AuthResult failure(String errorCode, String message);
}

public class AuthException extends Exception {
    private AuthErrorCode errorCode;
    
    public enum AuthErrorCode {
        NETWORK_ERROR,
        INVALID_CREDENTIALS,
        USER_CANCELLED,
        TOKEN_EXPIRED,
        SERVER_ERROR,
        NO_MINECRAFT_PROFILE,
        RATE_LIMITED
    }
}
```

### 3. storage（账号存储）

**AccountStorage**
```java
public class AccountStorage {
    private Path storagePath;
    private Map<String, Account> accounts;
    private String selectedAccountId;
    private byte[] encryptionKey;
    
    public AccountStorage(Path storagePath) {
        this.storagePath = storagePath;
        this.accounts = new LinkedHashMap<>();
    }
    
    public void load() throws StorageException;
    public void save() throws StorageException;
    
    public void addAccount(Account account);
    public void removeAccount(String accountId);
    public Account getAccount(String accountId);
    public List<Account> getAllAccounts();
    
    public void selectAccount(String accountId);
    public Account getSelectedAccount();
    
    public void setEncryptionKey(byte[] key);
    public void changeEncryptionKey(byte[] newKey);
}

public class StorageException extends Exception {
    private StorageErrorCode errorCode;
    
    public enum StorageErrorCode {
        READ_ERROR,
        WRITE_ERROR,
        DECRYPTION_ERROR,
        INVALID_FORMAT
    }
}
```

**存储格式**
```json
{
  "version": 1,
  "accounts": [
    {
      "id": "uuid-1",
      "type": "microsoft",
      "username": "Player1",
      "displayName": "Player1",
      "uuid": "minecraft-uuid",
      "accessToken": "encrypted...",
      "refreshToken": "encrypted...",
      "tokenExpiry": "2024-01-01T00:00:00Z",
      "createdAt": "2023-01-01T00:00:00Z",
      "lastUsed": "2024-01-01T00:00:00Z",
      "selected": true
    },
    {
      "id": "uuid-2",
      "type": "offline",
      "username": "OfflinePlayer",
      "uuid": "offline-uuid",
      "createdAt": "2023-01-01T00:00:00Z",
      "selected": false
    }
  ]
}
```

### 4. skin（皮肤系统）

**SkinManager**
```java
public class SkinManager {
    private HttpClient httpClient;
    private Path skinCacheDir;
    
    public SkinProfile getSkin(Account account) throws SkinException;
    public SkinProfile getSkin(String uuid) throws SkinException;
    public void uploadSkin(Account account, Path skinFile, SkinModel model) throws SkinException;
    public void resetSkin(Account account) throws SkinException;
    public void setCape(Account account, String capeId) throws SkinException;
    
    public Image getSkinPreview(String uuid);
    public Image getSkinHead(String uuid, int size);
    
    public void cacheSkin(String uuid, Image skin);
    public void clearCache();
    
    public CompletableFuture<SkinProfile> getSkinAsync(String uuid);
}
```

**SkinService**
```java
public class SkinService {
    
    public SkinProfile fetchFromMojang(String uuid) throws SkinException {
        // 调用Mojang API获取皮肤
        // GET https://sessionserver.mojang.com/session/minecraft/profile/{uuid}
    }
    
    public SkinProfile fetchFromCrafatar(String uuid) {
        // 使用Crafatar作为备选
        // https://crafatar.com/avatars/{uuid}
    }
    
    public SkinProfile fetchFromCustomServer(String serverUrl, String uuid) {
        // 从自定义服务器获取皮肤
    }
}
```

**SkinRenderer**
```java
public class SkinRenderer {
    
    public Image renderHead(String uuid, int size);
    public Image renderBody(String uuid, int size);
    public Image renderFull(String uuid, int width, int height);
    public Image renderWithPose(String uuid, SkinPose pose, int size);
    
    public enum SkinPose {
        STANDING, WALKING, RUNNING, SITTING
    }
    
    public void setSkinImage(Image skinImage);
    public void setOverlayEnabled(boolean enabled);
}
```

### 5. session（会话管理）

**SessionManager**
```java
public class SessionManager {
    private AccountStorage accountStorage;
    private Account currentSession;
    
    public SessionManager(AccountStorage accountStorage) {
        this.accountStorage = accountStorage;
    }
    
    public Account login(String accountId) throws AuthException;
    public Account loginNew(AuthProvider provider, AuthRequest request) throws AuthException;
    public void logout(String accountId);
    public void logoutCurrent();
    
    public Account getCurrentSession();
    public void validateSession() throws AuthException;
    public void refreshSession() throws AuthException;
    
    public List<Account> getStoredAccounts();
    public void switchAccount(String accountId) throws AuthException;
    
    public void addSessionListener(SessionListener listener);
    public void removeSessionListener(SessionListener listener);
}

public interface SessionListener {
    void onSessionChanged(Account oldSession, Account newSession);
    void onSessionRefreshed(Account session);
    void onSessionExpired(Account session);
    void onLoginSuccess(Account account);
    void onLoginFailed(AuthException error);
    void onLogout(Account account);
}
```

**Session状态机**
```
           ┌─────────────┐
           │   IDLE      │
           └──────┬──────┘
                  │ login()
                  ▼
           ┌─────────────┐
           │ AUTHENTICATING│
           └──────┬──────┘
                  │
        ┌─────────┴─────────┐
        │ success           │ failure
        ▼                   ▼
┌─────────────┐      ┌─────────────┐
│  ACTIVE     │      │   FAILED    │
└──────┬──────┘      └─────────────┘
       │
       │ token expired
       ▼
┌─────────────┐
│  EXPIRED    │
└──────┬──────┘
       │
       │ refresh()
       ▼
┌─────────────┐
│  ACTIVE     │
└─────────────┘
```

## 账号流程示例

### 微软登录流程
```java
// 初始化
AccountStorage storage = new AccountStorage(Paths.get("accounts.json"));
storage.load();
SessionManager session = new SessionManager(storage);

// 检查是否有已存储的账号
List<Account> accounts = storage.getAllAccounts();
if (!accounts.isEmpty()) {
    // 使用最近使用的账号
    Account account = storage.getSelectedAccount();
    if (account != null) {
        session.login(account.getId());
    }
}

// 新账号登录
MicrosoftAuthProvider provider = new MicrosoftAuthProvider();
AuthResult result = session.loginNew(provider, new AuthRequest());
if (result.isSuccess()) {
    System.out.println("Logged in as: " + result.getAccount().getDisplayName());
}

// 刷新令牌
session.refreshSession();

// 切换账号
session.switchAccount("other-account-id");

// 登出
session.logoutCurrent();
```

### 离线模式登录
```java
OfflineAuthProvider provider = new OfflineAuthProvider();
AuthRequest request = new AuthRequest();
request.setUsername("OfflinePlayer");

AuthResult result = session.loginNew(provider, request);
```

### 皮肤操作
```java
SkinManager skinManager = new SkinManager();

// 获取皮肤
SkinProfile skin = skinManager.getSkin(account);
Image skinImage = skin.getSkinImage();

// 上传皮肤
skinManager.uploadSkin(account, Paths.get("custom_skin.png"), SkinModel.ALEX);

// 重置皮肤
skinManager.resetSkin(account);

// 渲染皮肤头像
Image head = new SkinRenderer().renderHead(account.getUuid(), 64);
```

## 依赖关系

本模块依赖：
- core（网络工具、日志、事件总线、JSON处理）
- 第三方库：OkHttp

## 安全考虑

1. **令牌加密存储**：访问令牌和刷新令牌使用AES加密存储
2. **不存储敏感信息**：不在内存中长时间保存明文令牌
3. **安全传输**：所有API调用使用HTTPS
4. **令牌刷新**：自动检测令牌过期并刷新
5. **会话验证**：启动游戏前验证会话有效性