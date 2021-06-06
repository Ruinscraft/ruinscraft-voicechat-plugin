package com.ruinscraft.voicechat.player;

import com.ruinscraft.voicechat.buffer.PacketDataSerializable;
import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;

import java.util.UUID;

public class VCPlayerState implements PacketDataSerializable<VCPlayerState> {

    private UUID playerId;
    private boolean disabled;
    private boolean disconnected;

    public VCPlayerState(UUID playerId, boolean disabled, boolean disconnected) {
        this.playerId = playerId;
        this.disabled = disabled;
        this.disconnected = disconnected;
    }

    public VCPlayerState() {

    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    @Override
    public VCPlayerState fromBytes(PacketDataSerializerReimpl buf) {
        playerId = buf.readUUID();
        disabled = buf.readBoolean();
        disconnected = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        buf.writeUUID(playerId);
        buf.writeBoolean(disabled);
        buf.writeBoolean(disconnected);
        buf.writeBoolean(false); // no groups
    }

}
