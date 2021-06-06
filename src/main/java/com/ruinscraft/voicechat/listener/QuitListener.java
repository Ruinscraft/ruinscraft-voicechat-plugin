package com.ruinscraft.voicechat.listener;

import com.ruinscraft.voicechat.player.VCPlayerStateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private VCPlayerStateManager playerStateManager;

    public QuitListener(VCPlayerStateManager playerStateManager) {
        this.playerStateManager = playerStateManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerStateManager.removeState(player.getUniqueId());
    }

}
