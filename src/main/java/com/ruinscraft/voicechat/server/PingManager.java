package com.ruinscraft.voicechat.server;

import com.ruinscraft.voicechat.VCPlugin;
import com.ruinscraft.voicechat.server.packet.VCPingPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PingManager {

    private VCPlugin vcPlugin;
    private Map<UUID, Ping> listeners;

    public PingManager(VCPlugin vcPlugin) {
        this.vcPlugin = vcPlugin;
        listeners = new HashMap<>();
    }

    public void onPongPacket(VCPingPacket packet) {
        Ping ping = listeners.get(packet.getId());
        if (ping == null) {
            return;
        }
        ping.listener.onPong(packet);
        listeners.remove(packet.getId());
    }

    public void checkTimeouts() {
        List<Map.Entry<UUID, Ping>> timedOut = listeners.entrySet().stream().filter(uuidPingEntry -> uuidPingEntry.getValue().isTimedOut()).collect(Collectors.toList());
        for (Map.Entry<UUID, Ping> ping : timedOut) {
            ping.getValue().listener.onTimeout();
            listeners.remove(ping.getKey());
        }
    }

    public void sendPing(VCClient vcClient, long timeout, PingListener listener) throws Exception {
        UUID id = UUID.randomUUID();
        long timestamp = System.currentTimeMillis();
        vcPlugin.getVoiceChatServer().sendPacket(new VCPingPacket(id, timestamp), vcClient);
        listeners.put(id, new Ping(listener, timestamp, timeout));
    }

    private static class Ping {
        private PingListener listener;
        private long timestamp;
        private long timeout;

        public Ping(PingListener listener, long timestamp, long timeout) {
            this.listener = listener;
            this.timestamp = timestamp;
            this.timeout = timeout;
        }

        public boolean isTimedOut() {
            return (System.currentTimeMillis() - timestamp) >= timeout;
        }
    }

    public interface PingListener {
        void onPong(VCPingPacket packet);

        void onTimeout();
    }

}
