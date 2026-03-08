package com.howlstudio.discordbridge;

import com.howlstudio.discordbridge.config.BridgeConfig;
import com.howlstudio.discordbridge.listeners.ChatListener;
import com.howlstudio.discordbridge.listeners.PlayerJoinLeaveListener;
import com.howlstudio.discordbridge.webhook.DiscordWebhook;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public final class DiscordBridgePlugin extends JavaPlugin {

    private BridgeConfig config;
    private DiscordWebhook webhook;

    public DiscordBridgePlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.config = new BridgeConfig(getDataDirectory());

        if (!config.isEnabled()) {
            log("[DiscordBridge] Disabled via config.");
            return;
        }

        if (config.getWebhookUrl() == null || config.getWebhookUrl().isBlank()
                || config.getWebhookUrl().equals("PASTE_YOUR_DISCORD_WEBHOOK_URL_HERE")) {
            log("[DiscordBridge] Set webhook_url in bridge-config.json to enable.");
            return;
        }

        this.webhook = new DiscordWebhook(config);

        new ChatListener(webhook, config).register();
        new PlayerJoinLeaveListener(webhook, config).register();

        log("[DiscordBridge] Ready — forwarding to Discord.");
        webhook.sendSystemMessage(":white_check_mark: **Server started.** Discord bridge active.");
    }

    @Override
    protected void shutdown() {
        if (webhook != null && config != null && config.isEnabled()) {
            webhook.sendSystemMessage(":octagonal_sign: **Server stopped.**");
            webhook.shutdown();
        }
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
