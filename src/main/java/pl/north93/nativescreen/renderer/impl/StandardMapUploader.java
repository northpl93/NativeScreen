package pl.north93.nativescreen.renderer.impl;

import net.minecraft.server.v1_12_R1.PacketDataSerializer;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapUploader;

public final class StandardMapUploader implements IMapUploader
{
    public static final int BUFFER_SIZE = 5 + 5 + 1 + 1 + 5 + 4 + 5 + 16384;
    private static final int PACKET_PLAY_OUT_MAP_ID = 0x24;
    private static final int MAP_SIZE = 128;

    @Override
    public void uploadMapToPlayer(final Player player, final int mapId, final IMapCanvas newCanvas)
    {
        final CraftPlayer craftPlayer = ((CraftPlayer) player);

        final ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer(BUFFER_SIZE, BUFFER_SIZE);
        writeMapPacket(buffer, mapId, newCanvas);

        final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channel.writeAndFlush(buffer);
    }

    public static void writeMapPacket(final ByteBuf buffer, final int mapId, final IMapCanvas newCanvas)
    {
        final PacketDataSerializer serializer = new PacketDataSerializer(buffer);

        // = = = packet header
        serializer.d(PACKET_PLAY_OUT_MAP_ID); // writeVarInt(1-5) - packetId

        // = = = packet data
        serializer.d(mapId); // WriteVarInt(1-5) - map identificator
        serializer.writeByte(4); // writeByte(1) - map scale
        serializer.writeBoolean(false); // writeBoolean(1) - Tracking Position
        serializer.d(0); // writeVarInt(1-5) - amount of icons
        serializer.writeByte(MAP_SIZE); // writeByte(1) - columns
        serializer.writeByte(MAP_SIZE); // writeByte(1) - rows
        serializer.writeByte(0); // writeByte(1) - x offset
        serializer.writeByte(0); // writeByte(1) - z offset

        final byte[] newCanvasBytes = newCanvas.getBytes();
        serializer.a(newCanvasBytes); // writeByteArrayWithLength(5 + size) - writes varInt and following byte array
    }
}

