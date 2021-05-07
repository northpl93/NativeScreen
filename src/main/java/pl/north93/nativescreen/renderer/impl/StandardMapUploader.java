package pl.north93.nativescreen.renderer.impl;

import java.util.Collection;

import net.minecraft.server.v1_12_R1.PacketDataSerializer;

import org.bukkit.entity.Player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import pl.north93.nativescreen.renderer.IMap;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.IMapUploader;
import pl.north93.nativescreen.renderer.broadcaster.IPacketBroadcaster;

public final class StandardMapUploader implements IMapUploader
{
    private static final int PACKET_PLAY_OUT_MAP_ID = 0x24;
    private static final int PACKET_ID_SIZE = PacketDataSerializer.countBytes(PACKET_PLAY_OUT_MAP_ID);
    public static final int PACKET_STATIC_SIZE = 1 + 1 + 1 + 4;

    private final IPacketBroadcaster packetBroadcaster;

    public StandardMapUploader(final IPacketBroadcaster packetBroadcaster)
    {
        this.packetBroadcaster = packetBroadcaster;
    }

    @Override
    public void uploadMapToAudience(final Collection<Player> audience, final IMap map, final IMapCanvasDirectAccess newCanvas)
    {
        final ByteBuf byteBuf = writeMapPacket(map.getMapId(), newCanvas);

        this.packetBroadcaster.broadcastRawPacket(audience, byteBuf);
    }

    public static ByteBuf writeMapPacket(final int mapId, final IMapCanvasDirectAccess newCanvas)
    {
        // = = = calculate packet sizes
        final int mapIdSize = PacketDataSerializer.countBytes(mapId);
        final int canvasArrayLength = newCanvas.getHeight() * newCanvas.getWidth();
        final int canvasArrayLengthSize = PacketDataSerializer.countBytes(canvasArrayLength);
        final int packetDataSize = PACKET_ID_SIZE + mapIdSize + PACKET_STATIC_SIZE + canvasArrayLengthSize + canvasArrayLength;
        final int totalPacketSize = PacketDataSerializer.countBytes(packetDataSize) + packetDataSize;

        final ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.directBuffer(totalPacketSize, totalPacketSize);
        final PacketDataSerializer serializer = new PacketDataSerializer(byteBuf);

        // = = = packet header
        serializer.d(packetDataSize); // writeVarInt(1-5) - size of following data

        writeMapPacketData(serializer, mapId, newCanvas);
        return byteBuf;
    }

    public static void writeMapPacketData(final PacketDataSerializer serializer, final int mapId, final IMapCanvasDirectAccess newCanvas)
    {
        // = = = packet header
        serializer.d(PACKET_PLAY_OUT_MAP_ID); // writeVarInt(1-5) - packetId

        // = = = packet data
        serializer.d(mapId); // WriteVarInt(1-5) - map identificator
        serializer.writeByte(4); // writeByte(1) - map scale
        serializer.writeBoolean(false); // writeBoolean(1) - Tracking Position
        serializer.d(0); // writeVarInt(1-5) - amount of icons
        serializer.writeByte(newCanvas.getWidth()); // writeByte(1) - columns
        serializer.writeByte(newCanvas.getHeight()); // writeByte(1) - rows
        serializer.writeByte(0); // writeByte(1) - x offset
        serializer.writeByte(0); // writeByte(1) - z offset

        // writeVarInt - size of the following array, always 5?
        serializer.d(newCanvas.getHeight() * newCanvas.getWidth());

        // write map array without copying
        final byte[] newCanvasBytes = newCanvas.getBytes();
        for (int i = 0; i < newCanvas.getHeight(); i++)
        {
            final int startIndex = newCanvas.calculateIndex(0, i);
            serializer.writeBytes(newCanvasBytes, startIndex, newCanvas.getWidth());
        }
    }
}

