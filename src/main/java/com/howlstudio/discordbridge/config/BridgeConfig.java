package com.howlstudio.discordbridge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BridgeConfig {

    private boolean enabled = true;
    private String webhookUrl = "";
    private String chatUsername = "Hytale";
    private String chatAvatarUrl = "";
    private String systemUsername = "Hytale Server";
    private String systemAvatarUrl = "";
    private boolean announceJoins = true;
    private boolean announceLeaves = true;
    private boolean announceServerStatus = true;
    private boolean announceDungeonEvents = true;
    private int maxMessageLength = 500;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public BridgeConfig(Path configDir) {
        Path configFile = configDir.resolve("bridge-config.json");
        if (!Files.exists(configFile)) {
            writeDefaults(configFile);
            return;
        }
        try {
            String json = Files.readString(configFile, StandardCharsets.UTF_8);
            JsonObject obj = GSON.fromJson(json, JsonObject.class);
            if (obj.has("enabled")) enabled = obj.get("enabled").getAsBoolean();
            if (obj.has("webhook_url")) webhookUrl = obj.get("webhook_url").getAsString();
            if (obj.has("chat_username")) chatUsername = obj.get("chat_username").getAsString();
            if (obj.has("chat_avatar_url")) chatAvatarUrl = obj.get("chat_avatar_url").getAsString();
            if (obj.has("system_username")) systemUsername = obj.get("system_username").getAsString();
            if (obj.has("system_avatar_url")) systemAvatarUrl = obj.get("system_avatar_url").getAsString();
            if (obj.has("announce_joins")) announceJoins = obj.get("announce_joins").getAsBoolean();
            if (obj.has("announce_leaves")) announceLeaves = obj.get("announce_leaves").getAsBoolean();
            if (obj.has("announce_server_status")) announceServerStatus = obj.get("announce_server_status").getAsBoolean();
            if (obj.has("announce_dungeon_events")) announceDungeonEvents = obj.get("announce_dungeon_events").getAsBoolean();
            if (obj.has("max_message_length")) maxMessageLength = obj.get("max_message_length").getAsInt();
        } catch (Exception e) {
            System.err.println("[DiscordBridge] Failed to read config: " + e.getMessage());
        }
    }

    private void writeDefaults(Path configFile) {
        try {
            Files.createDirectories(configFile.getParent());
            JsonObject obj = new JsonObject();
            obj.addProperty("enabled", true);
            obj.addProperty("webhook_url", "PASTE_YOUR_DISCORD_WEBHOOK_URL_HERE");
            obj.addProperty("chat_username", "Hytale");
            obj.addProperty("chat_avatar_url", "");
            obj.addProperty("system_username", "Hytale Server");
            obj.addProperty("system_avatar_url", "");
            obj.addProperty("announce_joins", true);
            obj.addProperty("announce_leaves", true);
            obj.addProperty("announce_server_status", true);
            obj.addProperty("announce_dungeon_events", true);
            obj.addProperty("max_message_length", 500);
            Files.writeString(configFile, GSON.toJson(obj), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[DiscordBridge] Failed to write default config: " + e.getMessage());
        }
    }

    public boolean isEnabled() { return enabled; }
    public String getWebhookUrl() { return webhookUrl; }
    public String getChatUsername() { return chatUsername; }
    public String getChatAvatarUrl() { return chatAvatarUrl; }
    public String getSystemUsername() { return systemUsername; }
    public String getSystemAvatarUrl() { return systemAvatarUrl; }
    public boolean isAnnounceJoins() { return announceJoins; }
    public boolean isAnnounceLeaves() { return announceLeaves; }
    public boolean isAnnounceServerStatus() { return announceServerStatus; }
    public boolean isAnnounceDungeonEvents() { return announceDungeonEvents; }
    public int getMaxMessageLength() { return maxMessageLength; }
}
