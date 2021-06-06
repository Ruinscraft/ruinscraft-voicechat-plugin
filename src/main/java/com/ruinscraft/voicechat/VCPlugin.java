package com.ruinscraft.voicechat;

import com.ruinscraft.voicechat.listener.JoinListener;
import com.ruinscraft.voicechat.listener.QuitListener;
import com.ruinscraft.voicechat.player.VCPlayerStateManager;
import com.ruinscraft.voicechat.pluginnet.PluginMessageUtil;
import com.ruinscraft.voicechat.server.VCServer;
import com.ruinscraft.voicechat.server.VCServerConfig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VCPlugin extends JavaPlugin {

    private VCPlayerStateManager playerStateManager;
    private VCServer voiceChatServer;

    public VCPlayerStateManager getPlayerStateManager() {
        return playerStateManager;
    }

    public VCServer getVoiceChatServer() {
        return voiceChatServer;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        playerStateManager = new VCPlayerStateManager(this);

        VCServerConfig serverConfig = VCServerConfig.fromConfigSection(getConfig().getConfigurationSection("voice-server"));
        voiceChatServer = new VCServer(this, serverConfig);
        voiceChatServer.start();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(playerStateManager), this);

        PluginMessageUtil.registerChannels(this, playerStateManager);

        for (Player player : getServer().getOnlinePlayers()) {
            playerStateManager.notifyPlayer(player);
        }
    }

    @Override
    public void onDisable() {
        PluginMessageUtil.unregisterChannels(this);

        for (Player player : getServer().getOnlinePlayers()) {
            playerStateManager.removeState(player.getUniqueId());
        }

        if (voiceChatServer != null) {
            voiceChatServer.close();
            voiceChatServer = null;
        }
    }

}
