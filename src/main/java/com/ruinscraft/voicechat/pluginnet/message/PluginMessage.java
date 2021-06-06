package com.ruinscraft.voicechat.pluginnet.message;

import com.ruinscraft.voicechat.buffer.PacketDataSerializable;

public abstract class PluginMessage<T extends PluginMessage> implements PacketDataSerializable<T> {

    private String channelId;

    public PluginMessage(String channelId) {
        this.channelId = "voicechat:" + channelId;
    }

    public String getChannelId() {
        return channelId;
    }

}
