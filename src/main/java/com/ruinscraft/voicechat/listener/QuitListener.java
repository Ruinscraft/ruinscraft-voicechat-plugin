package com.ruinscraft.voicechat.listener;

import com.ruinscraft.voicechat.VCPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private VCPlugin vcPlugin;

    public QuitListener(VCPlugin vcPlugin) {
        this.vcPlugin = vcPlugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        vcPlugin.getPlayerStateManager().removeState(player.getUniqueId());
        vcPlugin.getVoiceChatServer().disconnectClient(player.getUniqueId());
    }

}
