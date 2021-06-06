package com.ruinscraft.voicechat.server.packet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;

public class VCKeepAlivePacket extends VCPacket<VCKeepAlivePacket> {

    @Override
    public VCKeepAlivePacket fromBytes(PacketDataSerializerReimpl buf) {
        return new VCKeepAlivePacket();
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
    }

}
