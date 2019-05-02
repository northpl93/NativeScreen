package pl.north93.nativescreen.renderer.compressor.impl;

import java.util.zip.Deflater;

import net.minecraft.server.v1_12_R1.PacketDataSerializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.nativescreen.renderer.compressor.ICompressablePacket;

@ToString
@AllArgsConstructor
/*default*/ final class PacketCompressTask implements Runnable
{
    private final Channel channel;
    private final ICompressablePacket compressablePacket;

    @Override
    public void run()
    {
        final ByteBuf compressedData = this.preparePacket();

        final CompressedPacket compressedPacket = new CompressedPacket(compressedData);
        this.channel.writeAndFlush(compressedPacket);
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

    private ByteBuf doCompression(final ByteBuf uncompressedBuffer)
    {
        final int uncompressedSize = uncompressedBuffer.readableBytes();

        final ByteBuf compressedBuffer = UnpooledByteBufAllocator.DEFAULT.buffer(128);
        final PacketDataSerializer compressedSerializer = new PacketDataSerializer(compressedBuffer);

        final DeflateContext context = DeflateContext.getContext();
        final Deflater deflater = context.getDeflater();
        final byte[] buffer = context.getBuffer();

        final byte[] deflateInput = new byte[uncompressedSize];
        uncompressedBuffer.readBytes(deflateInput);
        compressedSerializer.d(deflateInput.length); // writeVarInt - uncompressed size

        deflater.setInput(deflateInput, 0, uncompressedSize);
        deflater.finish();

        while (! deflater.finished())
        {
            final int deflatedSize = deflater.deflate(buffer);
            compressedSerializer.writeBytes(buffer, 0, deflatedSize);
        }

        deflater.reset();

        return compressedBuffer;
    }
}