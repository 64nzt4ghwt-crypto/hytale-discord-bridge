package com.howlstudio.discordbridge.listeners;

import com.howlstudio.discordbridge.config.BridgeConfig;
import com.howlstudio.discordbridge.webhook.DiscordWebhook;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class PlayerJoinLeaveListener {

    private final DiscordWebhook webhook;
    private final BridgeConfig config;

    public PlayerJoinLeaveListener(DiscordWebhook webhook, BridgeConfig config) {
        this.webhook = webhook;
        this.config = config;
    }

    public void register() {
        var eventBus = HytaleServer.get().getEventBus();
        eventBus.registerGlobal(PlayerReadyEvent.class, this::onJoin);
        eventBus.registerGlobal(PlayerDisconnectEvent.class, this::onLeave);
    }

    private void onJoin(PlayerReadyEvent event) {
        var player = event.getPlayer();
        if (player == null) return;
        PlayerRef ref = player.getPlayerRef();
        if (ref == null) return;
        webhook.sendJoinMessage(ref.getUsername());
    }

    private void onLeave(PlayerDisconnectEvent event) {
        PlayerRef ref = event.getPlayerRef();
        if (ref == null) return;
        webhook.sendLeaveMessage(ref.getUsername());
    }
}
