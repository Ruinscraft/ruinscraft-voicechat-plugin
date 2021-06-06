package com.ruinscraft.voicechat.server;

import com.ruinscraft.voicechat.OpusApplication;
import org.bukkit.configuration.ConfigurationSection;

public class VCServerConfig {

    private String bindAddr;
    private int port;
    private double voiceDistance;
    private double voiceFadeDistance;
    private OpusApplication opusApplication;
    private int mtuSize;
    private int keepAlive;
    private boolean groupsEnabled;

    public VCServerConfig() {
        bindAddr = "0.0.0.0";
        port = 24454;
        voiceDistance = 32D;
        voiceFadeDistance = 16D;
        opusApplication = OpusApplication.VOIP;
        mtuSize = 1024;
        keepAlive = 1000;
        groupsEnabled = true;
    }

    public String getBindAddr() {
        return bindAddr;
    }

    public void setBindAddr(String bindAddr) {
        this.bindAddr = bindAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public double getVoiceDistance() {
        return voiceDistance;
    }

    public void setVoiceDistance(double voiceDistance) {
        this.voiceDistance = voiceDistance;
    }

    public double getVoiceFadeDistance() {
        return voiceFadeDistance;
    }

    public void setVoiceFadeDistance(double voiceFadeDistance) {
        this.voiceFadeDistance = voiceFadeDistance;
    }

    public OpusApplication getOpusApplication() {
        return opusApplication;
    }

    public void setOpusApplication(OpusApplication opusApplication) {
        this.opusApplication = opusApplication;
    }

    public int getMtuSize() {
        return mtuSize;
    }

    public void setMtuSize(int mtuSize) {
        this.mtuSize = mtuSize;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isGroupsEnabled() {
        return groupsEnabled;
    }

    public void setGroupsEnabled(boolean groupsEnabled) {
        this.groupsEnabled = groupsEnabled;
    }

    public static VCServerConfig fromConfigSection(ConfigurationSection configSection) {
        VCServerConfig config = new VCServerConfig();
        config.bindAddr = configSection.getString("bind-address");
        config.port = configSection.getInt("port");
        config.voiceDistance = configSection.getDouble("voice-distance");
        config.voiceFadeDistance = configSection.getDouble("voice-fade-distance");
        config.opusApplication = OpusApplication.valueOf(configSection.getString("opus-application"));
        config.mtuSize = configSection.getInt("mtu-size");
        config.keepAlive = configSection.getInt("keep-alive");
        config.groupsEnabled = configSection.getBoolean("groups-enabled");
        return config;
    }

}
