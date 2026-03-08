package com.howlstudio.discordbridge.webhook;

import com.google.gson.JsonObject;
import com.howlstudio.discordbridge.config.BridgeConfig;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sends messages to a Discord webhook URL asynchronously.
 * Uses a single-thread executor so messages are sent in order without blocking the game thread.
 */
public class DiscordWebhook {

    private final BridgeConfig config;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "discord-bridge-sender");
        t.setDaemon(true);
        return t;
    });

    public DiscordWebhook(BridgeConfig config) {
        this.config = config;
    }

    /**
     * Send a chat message from a player to Discord.
     */
    public void sendChatMessage(String username, String message) {
        String formatted = String.format("**%s**: %s", escape(username), escape(message));
        send(config.getWebhookUrl(), formatted, config.getChatUsername(), config.getChatAvatarUrl());
    }

    /**
     * Send a player join message.
     */
    public void sendJoinMessage(String username) {
        if (!config.isAnnounceJoins()) return;
        String formatted = String.format(":green_circle: **%s** joined the server.", escape(username));
        send(config.getWebhookUrl(), formatted, config.getSystemUsername(), config.getSystemAvatarUrl());
    }

    /**
     * Send a player leave message.
     */
    public void sendLeaveMessage(String username) {
        if (!config.isAnnounceLeaves()) return;
        String formatted = String.format(":red_circle: **%s** left the server.", escape(username));
        send(config.getWebhookUrl(), formatted, config.getSystemUsername(), config.getSystemAvatarUrl());
    }

    /**
     * Send a system / server status message.
     */
    public void sendSystemMessage(String message) {
        if (!config.isAnnounceServerStatus()) return;
        send(config.getWebhookUrl(), message, config.getSystemUsername(), config.getSystemAvatarUrl());
    }

    /**
     * Send a dungeon event message (boss killed, run completed, etc.).
     */
    public void sendDungeonEvent(String message) {
        if (!config.isAnnounceDungeonEvents()) return;
        send(config.getWebhookUrl(), ":crossed_swords: " + escape(message),
             config.getSystemUsername(), config.getSystemAvatarUrl());
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void send(String webhookUrl, String content, String username, String avatarUrl) {
        executor.submit(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("content", content);
                if (username != null && !username.isBlank()) {
                    payload.addProperty("username", username);
                }
                if (avatarUrl != null && !avatarUrl.isBlank()) {
                    payload.addProperty("avatar_url", avatarUrl);
                }

                byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
                HttpURLConnection conn = (HttpURLConnection) URI.create(webhookUrl).toURL().openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("User-Agent", "HytaleDiscordBridge/1.0");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body);
                }

                int code = conn.getResponseCode();
                if (code != 204 && code != 200) {
                    System.err.println("[DiscordBridge] Webhook returned HTTP " + code);
                }
                conn.disconnect();
            } catch (Exception e) {
                System.err.println("[DiscordBridge] Failed to send webhook: " + e.getMessage());
            }
        });
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("@everyone", "@\u200beveryone")
                .replace("@here", "@\u200bhere")
                .replace("`", "'");
    }

    public void shutdown() {
        executor.shutdown();
    }
}
