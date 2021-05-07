package pl.north93.nativescreen.renderer.compressor.impl;

import java.util.Collection;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import net.minecraft.server.v1_12_R1.PacketDataSerializer;

import org.bukkit.entity.Player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.nativescreen.renderer.broadcaster.IPacketBroadcaster;
import pl.north93.nativescreen.renderer.compressor.ICompressablePacket;

@Slf4j
@ToString
@AllArgsConstructor
/*default*/ final class PacketCompressTask implements Runnable
{
    private final IPacketBroadcaster packetBroadcaster;
    private final Collection<Player> channels;
    private final ICompressablePacket compressablePacket;

    @Override
    public void run()
    {
        try
        {
            final ByteBuf compressedData = this.preparePacket();
            this.packetBroadcaster.broadcastRawPacket(this.channels, compressedData);
        }
        catch (final Exception e)
        {
            log.error("Failed to compress packet", e);
        }
    }

    private ByteBuf preparePacket()
    {
        final int capacity = this.compressablePacket.predictBufferSize();
        final ByteBuf uncompressedData = UnpooledByteBufAllocator.DEFAULT.buffer(capacity, capacity);

        try
        {
            this.compressablePacket.writeData(uncompressedData);
            return this.doCompression(uncompressedData);
        }
        finally
        {
            uncompressedData.release();
        }
    }

    @SneakyThrows
    private ByteBuf doCompression(final ByteBuf uncompressedBuffer)
    {
        final int uncompressedSize = uncompressedBuffer.readableBytes();

        final ByteBuf compressedBuffer = UnpooledByteBufAllocator.DEFAULT.buffer(1024);
        final PacketDataSerializer compressedSerializer = new PacketDataSerializer(compressedBuffer);

        // write size of uncompressed data into output
        compressedSerializer.d(uncompressedSize); // writeVarInt - uncompressed size

        final DeflateContext context = DeflateContext.getContext();
        final Deflater deflater = context.getDeflater();

        // compress this shit and see what happens
        final DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(new ByteBufOutputStream(compressedBuffer), deflater);

        uncompressedBuffer.readBytes(deflaterOutputStream, uncompressedSize);
        deflaterOutputStream.finish();

        deflater.reset();

        return compressedBuffer;
    }

//    private ByteBuf doCompression(final ByteBuf uncompressedBuffer)
//    {
//        final int uncompressedSize = uncompressedBuffer.readableBytes();
//
//        final ByteBuf compressedBuffer = UnpooledByteBufAllocator.DEFAULT.buffer(128);
//        final PacketDataSerializer compressedSerializer = new PacketDataSerializer(compressedBuffer);
//
//        final DeflateContext context = DeflateContext.getContext();
//        final Deflater deflater = context.getDeflater();
//        final byte[] buffer = context.getBuffer();
//
//        final byte[] deflateInput = new byte[uncompressedSize];
//        uncompressedBuffer.readBytes(deflateInput);
//        compressedSerializer.d(deflateInput.length); // writeVarInt - uncompressed size
//
//        deflater.setInput(deflateInput, 0, uncompressedSize);
//        deflater.finish();
//
//        while (! deflater.finished())
//        {
//            final int deflatedSize = deflater.deflate(buffer);
//            compressedSerializer.writeBytes(buffer, 0, deflatedSize);
//        }
//
//        deflater.reset();
//
//        return compressedBuffer;
//    }
}