package pl.north93.nativescreen.renderer.compressor;

import java.util.Collection;

import io.netty.channel.Channel;

/**
 * Simple multithreaded system used to bypass builtin Minecraft's compression
 */
public interface IPacketCompressor
{
    void injectChannel(Channel channel);

    void broadcastPacket(Collection<Channel> channels, ICompressablePacket packet);
}
