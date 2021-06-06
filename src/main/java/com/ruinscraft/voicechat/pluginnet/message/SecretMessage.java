package com.ruinscraft.voicechat.pluginnet.message;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;
import com.ruinscraft.voicechat.server.VCServerConfig;
import org.apache.commons.lang.NotImplementedException;

import java.util.UUID;

public class SecretMessage extends PluginMessage<SecretMessage> {

    private UUID secret;
    private VCServerConfig serverConfig;

    public SecretMessage(UUID secret, VCServerConfig serverConfig) {
        this();
        this.secret = secret;
        this.serverConfig = serverConfig;
    }

    public SecretMessage() {
        super("secret");
    }

    public UUID getSecret() {
        return secret;
    }

    public VCServerConfig getServerConfig() {
        return serverConfig;
    }

    @Override
    public SecretMessage fromBytes(PacketDataSerializerReimpl buf) {
        throw new NotImplementedException("No server implementation");
    }

    @Override
    public void toBytes(PacketDataSerializerReimpl buf) {
        buf.writeUUID(secret);
        buf.writeInt(serverConfig.getPort());
        buf.writeByte(serverConfig.getOpusApplication().ordinal());
        buf.writeInt(serverConfig.getMtuSize());
        buf.writeDouble(serverConfig.getVoiceDistance());
        buf.writeDouble(serverConfig.getVoiceFadeDistance());
        buf.writeInt(serverConfig.getKeepAlive());
        buf.writeBoolean(serverConfig.isGroupsEnabled());
    }

}
