package com.ruinscraft.voicechat.server;

import com.ruinscraft.voicechat.VCPlugin;
import com.ruinscraft.voicechat.server.packet.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VCServer extends Thread {

    private VCPlugin vcPlugin;
    private VCServerConfig config;
    private DatagramSocket serverSocket;
    private Map<UUID, VCClient> clients;
    private Map<UUID, UUID> secrets;
    private BlockingQueue<VCPacket> packetQueue;
    private ProcessThread processThread;
    private PingManager pingManager;

    public VCServer(VCPlugin vcPlugin, VCServerConfig config) {
        this.vcPlugin = vcPlugin;
        this.config = config;
        clients = new HashMap<>();
        secrets = new HashMap<>();
        packetQueue = new LinkedBlockingQueue<>();
        processThread = new ProcessThread();
        pingManager = new PingManager(vcPlugin);

        processThread.start();

        setDaemon(true);
        setName("VoiceChatServerThread");
    }

    public VCServerConfig getConfig() {
        return config;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    public void sendPacket(VCPacket<?> packet, VCClient client) throws Exception {
        client.send(this, packet);
    }

    public UUID getSecret(UUID playerId) {
        if (secrets.containsKey(playerId)) {
            return secrets.get(playerId);
        } else {
            UUID secret = UUID.randomUUID();
            secrets.put(playerId, secret);
            return secret;
        }
    }

    @Override
    public void run() {
        try {
            InetAddress address = null;
            String addr = config.getBindAddr();
            try {
                if (!addr.isEmpty()) {
                    address = InetAddress.getByName(addr);
                }
            } catch (Exception e) {
                vcPlugin.getLogger().warning("Failed to parse bind IP address '" + addr + "'");
                vcPlugin.getLogger().info("Binding to default IP address");
                e.printStackTrace();
            }
            try {
                serverSocket = new DatagramSocket(config.getPort(), address);
                serverSocket.setTrafficClass(0x04); // IPTOS_RELIABILITY
            } catch (BindException e) {
                vcPlugin.getLogger().warning("Failed to bind to address '" + addr + "'");
                e.printStackTrace();
                return;
            }
            vcPlugin.getLogger().info("Server started at port " + config.getPort());

            while (!serverSocket.isClosed()) {
                try {
                    VCPacket vcPacket = VCPacket.read(serverSocket, this);
                    packetQueue.add(vcPacket);
                } catch (Exception e) {
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void processProximityPacket(Player player, VCMicPacket packet) throws Exception {
        double distance = config.getVoiceDistance();

        List<VCClient> closeBy = vcPlugin.getServer().getOnlinePlayers().stream()
                .filter(OfflinePlayer::isOnline)
                .filter(p -> !p.equals(player))
                .filter(p -> p.getLocation().distance(player.getLocation()) < distance)
                .map(p -> clients.get(p.getUniqueId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        VCSoundPacket soundPacket = new VCSoundPacket(player.getUniqueId(), packet.getData(), packet.getSequenceNumber());

        for (VCClient client : closeBy) {
            client.send(this, soundPacket);
        }
    }

    private void keepAlive() throws Exception {
        long timestamp = System.currentTimeMillis();
        VCKeepAlivePacket keepAlivePacket = new VCKeepAlivePacket();
        List<UUID> clientsToDrop = new ArrayList<>(clients.size());
        for (VCClient client : clients.values()) {
            if (timestamp - client.getLastKeepAliveResponse() >= config.getKeepAlive() * 10L) {
                clientsToDrop.add(client.getPlayerId());
            } else if (timestamp - client.getLastKeepAlive() >= config.getKeepAlive()) {
                client.setLastKeepAlive(timestamp);
                sendPacket(keepAlivePacket, client);
            }
        }
        for (UUID uuid : clientsToDrop) {
            disconnectClient(uuid);
            vcPlugin.getLogger().info("Player {" + uuid + "} timed out");
            Player player = vcPlugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                vcPlugin.getLogger().info("Reconnecting player {" + player.getUniqueId() + "}");
                // TODO: reconnect
//                Voicechat.SERVER.initializePlayerConnection(player);
            } else {
                vcPlugin.getLogger().info("Could not reconnect player {" + player.getUniqueId() + "}");
            }
        }
    }

    public void disconnectClient(UUID playerId) {
        secrets.remove(playerId);
        clients.remove(playerId);
    }

    public void close() {
        serverSocket.close();
    }

    public boolean running() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    private class ProcessThread extends Thread {
        private volatile boolean running;

        public ProcessThread() {
            this.running = true;

            setDaemon(true);
            setName("VoiceChatPacketProcessingThread");
        }

        @Override
        public void run() {
            while (running) {
                try {
                    pingManager.checkTimeouts();
                    keepAlive();

                    VCPacket vcPacket = packetQueue.poll(10, TimeUnit.MILLISECONDS);
                    if (vcPacket == null || System.currentTimeMillis() - vcPacket.getTimestamp() > vcPacket.getTtl()) {
                        continue;
                    }

                    if (vcPacket instanceof VCAuthPacket authPacket) {
                        UUID secret = secrets.get(authPacket.getPlayerId());
                        if (secret != null && secret.equals(authPacket.getSecret())) {
                            final VCClient client;
                            if (!clients.containsKey(authPacket.getPlayerId())) {
                                client = new VCClient(authPacket.getPlayerId(), vcPacket.getAddress());
                                clients.put(client.getPlayerId(), client);
                                vcPlugin.getLogger().info("Successfully authenticated player {" + client.getPlayerId() + "}");
                            } else {
                                client = clients.get(authPacket.getPlayerId());
                            }
                            sendPacket(new VCAuthAckPacket(), client);
                        }
                    }

                    UUID playerId = null;
                    for (VCClient client : clients.values()) {
                        if (client.getAddress().equals(vcPacket.getAddress())) {
                            playerId = client.getPlayerId();
                        }
                    }
                    if (playerId == null) {
                        continue;
                    }

                    VCClient client = clients.get(playerId);

                    if (vcPacket instanceof VCMicPacket micPacket) {
                        Player player = vcPlugin.getServer().getPlayer(playerId);
                        if (player == null || !player.isOnline()) {
                            continue;
                        }
                        // TODO: get player state and process based on other factors (eg. theater room)
                        processProximityPacket(player, micPacket);
                    } else if (vcPacket instanceof VCPingPacket vcPingPacket) {
                        pingManager.onPongPacket(vcPingPacket);
                    } else if (vcPacket instanceof VCKeepAlivePacket) {
                        client.setLastKeepAliveResponse(System.currentTimeMillis());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void close() {
            running = false;
        }
    }

}
