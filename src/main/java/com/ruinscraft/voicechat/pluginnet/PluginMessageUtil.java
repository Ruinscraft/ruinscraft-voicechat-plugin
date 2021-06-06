package com.ruinscraft.voicechat.pluginnet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;
import com.ruinscraft.voicechat.player.VCPlayerStateManager;
import com.ruinscraft.voicechat.pluginnet.message.PlayerStateMessage;
import com.ruinscraft.voicechat.pluginnet.message.PlayerStatesMessage;
import com.ruinscraft.voicechat.pluginnet.message.PluginMessage;
import com.ruinscraft.voicechat.pluginnet.message.SecretMessage;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

public final class PluginMessageUtil {

    public static final String CHANNEL_OUT_INIT = new SecretMessage().getChannelId();
    public static final String CHANNEL_OUT_PLAYER_STATE = new PlayerStateMessage().getChannelId();
    public static final String CHANNEL_OUT_PLAYER_STATES = new PlayerStatesMessage().getChannelId();
    public static final String CHANNEL_IN_PLAYER_STATE = CHANNEL_OUT_PLAYER_STATE;

    public static void registerChannels(JavaPlugin javaPlugin, VCPlayerStateManager playerStateManager) {
        Messenger messenger = javaPlugin.getServer().getMessenger();

        // Register outgoing channels
        messenger.registerOutgoingPluginChannel(javaPlugin, CHANNEL_OUT_INIT);
        messenger.registerOutgoingPluginChannel(javaPlugin, CHANNEL_OUT_PLAYER_STATE);
        messenger.registerOutgoingPluginChannel(javaPlugin, CHANNEL_OUT_PLAYER_STATES);

        // Register incoming channels
        messenger.registerIncomingPluginChannel(javaPlugin, CHANNEL_IN_PLAYER_STATE, (channel, player, bytes) -> {
            PlayerStateMessage playerStateMessage = new PlayerStateMessage();
            PacketDataSerializerReimpl buf = new PacketDataSerializerReimpl(Unpooled.wrappedBuffer(bytes));
            playerStateMessage.fromBytes(buf);
            playerStateManager.updateState(playerStateMessage.getPlayerState());
            buf.release();
        });
    }

    public static void unregisterChannels(JavaPlugin javaPlugin) {
        Messenger messenger = javaPlugin.getServer().getMessenger();

        // Unregister outgoing channels
        messenger.unregisterOutgoingPluginChannel(javaPlugin, CHANNEL_OUT_INIT);
        messenger.unregisterOutgoingPluginChannel(javaPlugin, CHANNEL_OUT_PLAYER_STATE);
        messenger.unregisterOutgoingPluginChannel(javaPlugin, CHANNEL_OUT_PLAYER_STATES);

        // Unregister incoming channels
        messenger.unregisterIncomingPluginChannel(javaPlugin, CHANNEL_IN_PLAYER_STATE);
    }

    public static void sendToClient(JavaPlugin javaPlugin, Player player, PluginMessage<?> message) {
        PacketDataSerializerReimpl buf = new PacketDataSerializerReimpl(Unpooled.buffer());
        message.toBytes(buf);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        player.sendPluginMessage(javaPlugin, message.getChannelId(), bytes);
        buf.release();
    }

}
