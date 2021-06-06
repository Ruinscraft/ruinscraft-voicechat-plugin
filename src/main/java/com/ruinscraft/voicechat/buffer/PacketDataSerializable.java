package com.ruinscraft.voicechat.buffer;

public interface PacketDataSerializable<T extends PacketDataSerializable> {

    T fromBytes(PacketDataSerializerReimpl buf);

    void toBytes(PacketDataSerializerReimpl buf);

}
