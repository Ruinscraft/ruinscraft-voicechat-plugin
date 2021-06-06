package com.ruinscraft.voicechat.listener;

import com.ruinscraft.voicechat.VCPlugin;
import com.ruinscraft.voicechat.pluginnet.PluginMessageUtil;
import com.ruinscraft.voicechat.pluginnet.message.SecretMessage;
import com.ruinscraft.voicechat.server.VCServer;
import com.ruinscraft.voicechat.server.VCServerConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private VCPlugin vcPlugin;

    public JoinListener(VCPlugin vcPlugin) {
        this.vcPlugin = vcPlugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        vcPlugin.getServer().getScheduler().runTaskLater(vcPlugin, () -> {
            VCServer server = vcPlugin.getVoiceChatServer();

            if (player.isOnline() && server.running()) {
                UUID secret = server.getSecret(player.getUniqueId());
                VCServerConfig config = server.getConfig();
                SecretMessage secretMessage = new SecretMessage(secret, config);
                PluginMessageUtil.sendToClient(vcPlugin, player, secretMessage);
            }
        }, 2 * 20L);
    }

}
