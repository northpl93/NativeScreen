package pl.north93.nativescreen.renderer.compressor;

import io.netty.channel.Channel;

/**
 * Simple multithreaded system used to bypass builtin Minecraft's compression
 */
public interface IPacketCompressor
{
    void injectChannel(Channel channel);

    void sendPacket(Channel channel, ICompressablePacket packet);
}
