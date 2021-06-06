package com.ruinscraft.voicechat.server.packet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;

import java.util.UUID;

public class VCPingPacket extends VCPacket<VCPingPacket> {

    private UUID id;
    private long timestamp;

    public VCPingPacket(UUID id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public VCPingPacket() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public VCPingPacket fromBytes(PacketDataSerializerReimpl buf) {
        VCPingPacket soundPacket = new VCPingPacket();
        soundPacket.id = buf.readUUID();
        soundPacket.timestamp = buf.readLong();
        return soundPacket;
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        buf.writeUUID(id);
        buf.writeLong(timestamp);
    }

}
