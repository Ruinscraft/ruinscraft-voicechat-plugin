package com.ruinscraft.voicechat.server.packet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;

public class VCMicPacket extends VCPacket<VCMicPacket> {

    private byte[] data;
    private long sequenceNumber;

    public VCMicPacket(byte[] data, long sequenceNumber) {
        this.data = data;
        this.sequenceNumber = sequenceNumber;
    }

    public VCMicPacket() {
    }

    public byte[] getData() {
        return data;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public VCMicPacket fromBytes(PacketDataSerializerReimpl buf) {
        VCMicPacket micPacket = new VCMicPacket();
        micPacket.data = buf.readByteArray();
        micPacket.sequenceNumber = buf.readLong();
        return micPacket;
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        buf.writeByteArray(data);
        buf.writeLong(sequenceNumber);
    }

}
