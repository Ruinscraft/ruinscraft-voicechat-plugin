package com.ruinscraft.voicechat.server;

import com.ruinscraft.voicechat.server.packet.VCPacket;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.UUID;

public class VCClient {

    private UUID playerId;
    private SocketAddress address;
    private long lastKeepAlive;
    private long lastKeepAliveResponse;

    public VCClient(UUID playerId, SocketAddress address) {
        this.playerId = playerId;
        this.address = address;
        lastKeepAlive = 0L;
        lastKeepAliveResponse = System.currentTimeMillis();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public long getLastKeepAlive() {
        return lastKeepAlive;
    }

    public void setLastKeepAlive(long lastKeepAlive) {
        this.lastKeepAlive = lastKeepAlive;
    }

    public long getLastKeepAliveResponse() {
        return lastKeepAliveResponse;
    }

    public void setLastKeepAliveResponse(long lastKeepAliveResponse) {
        this.lastKeepAliveResponse = lastKeepAliveResponse;
    }

    public void send(VCServer vcServer, VCPacket vcPacket) throws Exception {
        byte[] data = vcPacket.writeSecret(vcServer.getSecret(playerId));
        vcServer.getServerSocket().send(new DatagramPacket(data, data.length, address));
    }

}
