package pl.north93.nativescreen.renderer.compressor.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/*default*/ class CompressedPacketToByteEncoder extends MessageToByteEncoder<CompressedPacket>
{
    public CompressedPacketToByteEncoder()
    {
        super(CompressedPacket.class);
    }

    @Override
    protected void encode(final ChannelHandlerContext channelHandlerContext, final CompressedPacket compressedPacket, final ByteBuf byteBuf) throws Exception
    {
        final ByteBuf compressedData = compressedPacket.getCompressedData();

        try
        {
            byteBuf.writeBytes(compressedData);
        }
        finally
        {
            compressedData.release();
        }
    }
}
