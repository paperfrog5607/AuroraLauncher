package org.aurora.launcher.steam.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.aurora.launcher.steam.model.SteamGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Steam API 客户端
 * 用于获取Steam游戏库信息和游戏详情
 */
public class SteamApiClient {

    private static final Logger logger = LoggerFactory.getLogger(SteamApiClient.class);

    private static final String STEAM_API_KEY = "YOUR_STEAM_API_KEY";
    private static final String STEAM_API_BASE = "https://steam.googleapis.com/steam/api/";
    private static final String STEAM COMMUNITY_BASE = "https://steamcommunity.com/";

    private final OkHttpClient httpClient;
    private final Gson gson;

    public SteamApiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * 获取玩家的Steam游戏列表
     */
    public List<SteamGame> getOwnedGames(String steamId) throws IOException {
        String url = STEAM_COMMUNITY_BASE + "profiles/" + steamId + "/games?xml=1";
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String xml = response.body().string();
            return parseGamesFromXml(xml);
        }
    }

    /**
     * 获取游戏详情
     */
    public SteamGame getGameDetails(int appId) throws IOException {
        String url = STEAM_COMMUNITY_BASE + "appdetails?appids=" + appId;
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String json = response.body().string();
            return parseGameDetailsFromJson(json);
        }
    }

    /**
     * 获取游戏图标URL
     */
    public String getGameIconUrl(int appId) {
        return "https://steamcdn-a.akamaihd.net/steam/apps/" + appId + "/capsule_184x69.jpg";
    }

    /**
     * 获取游戏封面URL
     */
    public String getGameHeaderUrl(int appId) {
        return "https://steamcdn-a.akamaihd.net/steam/apps/" + appId + "/header.jpg";
    }

    /**
     * 获取游戏背景URL
     */
    public String getGameBackgroundUrl(int appId) {
        return "https://steamcdn-a.akamaihd.net/steam/apps/" + appId + "/page_bg_generated_v6b.jpg";
    }

    private List<SteamGame> parseGamesFromXml(String xml) {
        List<SteamGame> games = new ArrayList<>();
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            java.io.StringReader reader = new java.io.StringReader(xml);
            org.w3c.dom.Document doc = builder.parse(new org.xml.sax.InputSource(reader));
            
            org.w3c.dom.NodeList gameList = doc.getElementsByTagName("game");
            
            for (int i = 0; i < gameList.getLength(); i++) {
                org.w3c.dom.Element game = (org.w3c.dom.Element) gameList.item(i);
                
                SteamGame steamGame = new SteamGame();
                steamGame.setAppId(getElementValue(game, "appID"));
                steamGame.setName(getElementValue(game, "name"));
                steamGame.setLogoUrl(getElementValue(game, "logo"));
                steamGame.setStoreUrl(getElementValue(game, "storeURL"));
                
                games.add(steamGame);
            }
        } catch (Exception e) {
            logger.error("Failed to parse Steam games XML", e);
        }
        
        return games;
    }

    private String getElementValue(org.w3c.dom.Element parent, String tagName) {
        org.w3c.dom.NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            org.w3c.dom.Element element = (org.w3c.dom.Element) list.item(0);
            return element.getTextContent();
        }
        return "";
    }

    private SteamGame parseGameDetailsFromJson(String json) {
        try {
            com.google.gson.JsonObject root = gson.fromJson(json, com.google.gson.JsonObject.class);
            for (String key : root.keySet()) {
                com.google.gson.JsonObject gameData = root.getAsJsonObject(key);
                if (gameData.has("success") && gameData.get("success").getAsBoolean()) {
                    com.google.gson.JsonObject data = gameData.getAsJsonObject("data");
                    
                    SteamGame game = new SteamGame();
                    game.setAppId(String.valueOf(data.get("steam_appid").getAsInt()));
                    game.setName(data.get("name").getAsString());
                    game.setType(data.get("type").getAsString());
                    
                    if (data.has("short_description")) {
                        game.setDescription(data.get("short_description").getAsString());
                    }
                    
                    if (data.has("header_image")) {
                        game.setHeaderImage(data.get("header_image").getAsString());
                    }
                    
                    return game;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse Steam game details JSON", e);
        }
        return null;
    }

    /**
     * 解析Steam ID
     */
    public String resolveVanityUrl(String vanityUrl) throws IOException {
        String url = STEAM_COMMUNITY_BASE + "api/profiles/?action=xml&steamid=" + vanityUrl;
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String xml = response.body().string();
            
            try {
                javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
                org.w3c.dom.Document doc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
                
                org.w3c.dom.NodeList steamId64 = doc.getElementsByTagName("steamID64");
                if (steamId64.getLength() > 0) {
                    return steamId64.item(0).getTextContent();
                }
            } catch (Exception e) {
                logger.error("Failed to parse vanity URL response", e);
            }
        }
        
        return null;
    }
}
