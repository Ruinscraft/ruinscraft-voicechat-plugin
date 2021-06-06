package com.ruinscraft.voicechat.pluginnet.message;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;
import com.ruinscraft.voicechat.player.VCPlayerState;
import org.apache.commons.lang.NotImplementedException;

import java.util.Map;
import java.util.UUID;

public class PlayerStatesMessage extends PluginMessage<PlayerStatesMessage> {

    private Map<UUID, VCPlayerState> playerStates;

    public PlayerStatesMessage(Map<UUID, VCPlayerState> playerStates) {
        this();
        this.playerStates = playerStates;
    }

    public PlayerStatesMessage() {
        super("player_states");
    }

    @Override
    public PlayerStatesMessage fromBytes(PacketDataSerializerReimpl buf) {
        throw new NotImplementedException("No server implementation");
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        buf.writeInt(playerStates.size());
        for (Map.Entry<UUID, VCPlayerState> entry : playerStates.entrySet()) {
            entry.getValue().toBytes(buf);
        }
    }

}
