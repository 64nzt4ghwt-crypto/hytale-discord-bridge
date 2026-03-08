package com.howlstudio.discordbridge.listeners;

import com.howlstudio.discordbridge.config.BridgeConfig;
import com.howlstudio.discordbridge.webhook.DiscordWebhook;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class ChatListener implements PlayerChatEvent.Formatter {

    private final DiscordWebhook webhook;
    private final BridgeConfig config;

    public ChatListener(DiscordWebhook webhook, BridgeConfig config) {
        this.webhook = webhook;
        this.config = config;
    }

    public void register() {
        HytaleServer.get().getEventBus().registerGlobal(
                PlayerChatEvent.class,
                event -> event.setFormatter(this)
        );
    }

    @Override
    @Nonnull
    public Message format(@Nonnull PlayerRef playerRef, @Nonnull String message) {
        String name = playerRef.getUsername() != null ? playerRef.getUsername() : "Unknown";
        String truncated = message.length() > config.getMaxMessageLength()
                ? message.substring(0, config.getMaxMessageLength()) + "..."
                : message;

        webhook.sendChatMessage(name, truncated);

        return Message.join(
                Message.raw("<" + name + "> "),
                Message.raw(message)
        );
    }
}
