package pl.north93.nativescreen.renderer.broadcaster.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/*default*/ class RawPacketOutboundHandler extends ChannelOutboundHandlerAdapter
{
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
    {
        if (msg instanceof RawPacket)
        {
            final RawPacket rawPacket = (RawPacket) msg;
            final ByteBuf rawData = rawPacket.getRawData();

            ctx.write(rawData);
            return;
        }

        ctx.write(msg, promise);
    }
}
