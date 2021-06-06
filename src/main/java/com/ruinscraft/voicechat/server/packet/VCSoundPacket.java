package com.ruinscraft.voicechat.server.packet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;

import java.util.UUID;

public class VCSoundPacket extends VCPacket<VCSoundPacket> {

    private UUID sender;
    private byte[] data;
    private long sequenceNumber;

    public VCSoundPacket(UUID sender, byte[] data, long sequenceNumber) {
        this.sender = sender;
        this.data = data;
        this.sequenceNumber = sequenceNumber;
    }

    public VCSoundPacket() {

    }

    public byte[] getData() {
        return data;
    }

    public UUID getSender() {
        return sender;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public VCSoundPacket fromBytes(PacketDataSerializerReimpl buf) {
        VCSoundPacket soundPacket = new VCSoundPacket();
        soundPacket.sender = buf.readUUID();
        soundPacket.data = buf.readByteArray();
        soundPacket.sequenceNumber = buf.readLong();
        return soundPacket;
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        buf.writeUUID(sender);
        buf.writeByteArray(data);
        buf.writeLong(sequenceNumber);
    }

}
