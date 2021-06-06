package com.ruinscraft.voicechat.server.packet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;

import java.util.UUID;

public class VCAuthPacket extends VCPacket<VCAuthPacket> {

    private UUID playerId;
    private UUID secret;

    public VCAuthPacket(UUID playerUUID, UUID secret) {
        this.playerId = playerUUID;
        this.secret = secret;
    }

    public VCAuthPacket() {

    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getSecret() {
        return secret;
    }

    @Override
    public VCAuthPacket fromBytes(PacketDataSerializerReimpl buf) {
        VCAuthPacket packet = new VCAuthPacket();
        packet.playerId = buf.readUUID();
        packet.secret = buf.readUUID();
        return packet;
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        buf.writeUUID(playerId);
        buf.writeUUID(secret);
    }

}
