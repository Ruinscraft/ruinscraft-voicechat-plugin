package com.ruinscraft.voicechat.player;

import com.ruinscraft.voicechat.VCPlugin;
import com.ruinscraft.voicechat.pluginnet.PluginMessageUtil;
import com.ruinscraft.voicechat.pluginnet.message.PlayerStateMessage;
import com.ruinscraft.voicechat.pluginnet.message.PlayerStatesMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VCPlayerStateManager {

    private VCPlugin vcPlugin;
    private Map<UUID, VCPlayerState> playerStates;

    public VCPlayerStateManager(VCPlugin vcPlugin) {
        this.vcPlugin = vcPlugin;
        playerStates = new HashMap<>();
    }

    public void broadcastState(VCPlayerState playerState) {
        PlayerStateMessage message = new PlayerStateMessage(playerState);
        vcPlugin.getServer().getOnlinePlayers().forEach(p -> PluginMessageUtil.sendToClient(vcPlugin, p, message));
    }

    public void acceptPlayer(Player player) {
        VCPlayerState playerState = new VCPlayerState(player.getUniqueId(), false, true);
        updateState(playerState);

        // Let the newly connected client know about all current player states
        PlayerStatesMessage message = new PlayerStatesMessage(playerStates);
        PluginMessageUtil.sendToClient(vcPlugin, player, message);
    }

    public void updateState(VCPlayerState playerState) {
        playerStates.put(playerState.getPlayerId(), playerState);
        broadcastState(playerState);
    }

    public void removeState(UUID playerId) {
        playerStates.remove(playerId);
    }

}
