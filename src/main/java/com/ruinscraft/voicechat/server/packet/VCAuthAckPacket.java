package com.ruinscraft.voicechat.server.packet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;

public class VCAuthAckPacket extends VCPacket<VCAuthAckPacket> {

    @Override
    public VCAuthAckPacket fromBytes(PacketDataSerializerReimpl buf) {
        return new VCAuthAckPacket();
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
    }

}
