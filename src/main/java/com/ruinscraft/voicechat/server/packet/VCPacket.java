package com.ruinscraft.voicechat.server.packet;

import com.ruinscraft.voicechat.buffer.PacketDataSerializable;
import com.ruinscraft.voicechat.buffer.PacketDataSerializerReimpl;
import com.ruinscraft.voicechat.server.AES;
import com.ruinscraft.voicechat.server.VCServer;
import io.netty.buffer.Unpooled;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class VCPacket<T extends VCPacket> implements PacketDataSerializable<T> {

    private static final Map<Byte, Class<? extends VCPacket>> packetRegistry;

    static {
        packetRegistry = new HashMap<>();
        packetRegistry.put((byte) 0, VCMicPacket.class);
        packetRegistry.put((byte) 1, VCSoundPacket.class);
        packetRegistry.put((byte) 2, VCAuthPacket.class);
        packetRegistry.put((byte) 3, VCAuthAckPacket.class);
        packetRegistry.put((byte) 4, VCPingPacket.class);
        packetRegistry.put((byte) 5, VCKeepAlivePacket.class);
    }

    private static byte getPacketType(VCPacket<? extends VCPacket> packet) {
        for (Map.Entry<Byte, Class<? extends VCPacket>> entry : packetRegistry.entrySet()) {
            if (packet.getClass().equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    // TODO: clean this up
    public static VCPacket read(DatagramSocket socket, VCServer server) throws Exception {
        DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
        socket.receive(packet);
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        PacketDataSerializerReimpl b = new PacketDataSerializerReimpl(Unpooled.wrappedBuffer(data));
        UUID playerId = b.readUUID();
        UUID secret = server.getSecret(playerId);
        byte[] decrypt = AES.decrypt(secret, b.readByteArray());
        PacketDataSerializerReimpl buffer = new PacketDataSerializerReimpl(Unpooled.wrappedBuffer(decrypt));
        UUID readSecret = buffer.readUUID();
        if (!secret.equals(readSecret)) {
            throw new InvalidKeyException("Secrets do not match");
        }
        byte packetType = buffer.readByte();
        Class<? extends VCPacket> packetClass = packetRegistry.get(packetType);
        if (packetClass == null) {
            throw new InstantiationException("Could not find packet with ID " + packetType);
        }
        VCPacket vcPacket = packetClass.newInstance();
        vcPacket = (VCPacket) vcPacket.fromBytes(buffer);
        vcPacket.address = packet.getSocketAddress();
        return vcPacket;
    }

    private long timestamp;
    private long ttl;
    private SocketAddress address;

    public VCPacket() {
        this.timestamp = System.currentTimeMillis();
        this.ttl = 2000L;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTtl() {
        return ttl;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public byte[] writeSecret(UUID secret) throws Exception {
        PacketDataSerializerReimpl buf = new PacketDataSerializerReimpl(Unpooled.buffer());
        buf.writeUUID(secret);

        byte type = getPacketType(this);
        if (type < 0) {
            throw new IllegalArgumentException("Packet type not found");
        }

        buf.writeByte(type);
        toBytes(buf);

        return AES.encrypt(secret, buf.array());
    }

}
