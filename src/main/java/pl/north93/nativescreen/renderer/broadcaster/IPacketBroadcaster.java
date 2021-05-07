package pl.north93.nativescreen.renderer.broadcaster;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * Simple system used to bypass Minecraft's channel pipeline.
 */
public interface IPacketBroadcaster
{
    void injectChannel(Channel channel);

    void broadcastRawPacket(Collection<Player> players, ByteBuf rawData);
}
