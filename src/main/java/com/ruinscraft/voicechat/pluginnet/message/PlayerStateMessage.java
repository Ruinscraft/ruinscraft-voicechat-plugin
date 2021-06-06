package com.ruinscraft.voicechat.pluginnet.message;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;
import com.ruinscraft.voicechat.player.VCPlayerState;

public class PlayerStateMessage extends PluginMessage<PlayerStateMessage> {

    private VCPlayerState playerState;

    public PlayerStateMessage(VCPlayerState playerState) {
        this();
        this.playerState = playerState;
    }

    public PlayerStateMessage() {
        super("player_state");
    }

    public VCPlayerState getPlayerState() {
        return playerState;
    }

    @Override
    public PlayerStateMessage fromBytes(PacketDataSerializerReimpl buf) {
        playerState = new VCPlayerState().fromBytes(buf);
        return this;
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        playerState.toBytes(buf);
    }

}
