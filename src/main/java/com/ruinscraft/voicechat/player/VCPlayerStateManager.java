package com.ruinscraft.voicechat.player;

import com.ruinscraft.voicechat.VCPlugin;
import com.ruinscraft.voicechat.pluginnet.PluginMessageUtil;
import com.ruinscraft.voicechat.pluginnet.message.PlayerStateMessage;
import com.ruinscraft.voicechat.pluginnet.message.PlayerStatesMessage;
import org.bukkit.entity.Player;

import java.util.*;

public class VCPlayerStateManager {

    private VCPlugin vcPlugin;
    private Map<UUID, VCPlayerState> playerStates;

    public VCPlayerStateManager(VCPlugin vcPlugin) {
        this.vcPlugin = vcPlugin;
        playerStates = new HashMap<>();
    }

    private void broadcastState(VCPlayerState playerState) {
        PlayerStateMessage message = new PlayerStateMessage(playerState);
        vcPlugin.getServer().getOnlinePlayers().forEach(p -> PluginMessageUtil.sendToClient(vcPlugin, p, message));
    }

    public void notifyPlayer(Player player) {
        PlayerStatesMessage message = new PlayerStatesMessage(playerStates);
        PluginMessageUtil.sendToClient(vcPlugin, player, message);
        broadcastState(new VCPlayerState(player.getUniqueId(), false, true));
    }

    public void addState(VCPlayerState playerState) {
        playerStates.put(playerState.getPlayerId(), playerState);
    }

    public void removeState(UUID playerId) {
        playerStates.remove(playerId);
    }

    public VCPlayerState getState(UUID playerId) {
        return playerStates.get(playerId);
    }

    public List<VCPlayerState> getStates() {
        return new ArrayList<>(playerStates.values());
    }

}
