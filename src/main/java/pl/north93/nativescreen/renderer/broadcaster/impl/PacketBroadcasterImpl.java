package pl.north93.nativescreen.renderer.broadcaster.impl;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.renderer.broadcaster.IPacketBroadcaster;

@Log4j2
@ToString
public class PacketBroadcasterImpl implements IPacketBroadcaster
{
    public PacketBroadcasterImpl(final JavaPlugin javaPlugin)
    {
        Bukkit.getPluginManager().registerEvents(new BroadcasterInjector(this), javaPlugin);
    }

    @Override
    public void injectChannel(final Channel channel)
    {
        log.info("Injecting packet bypass in channel {}", channel);
        channel.pipeline().addBefore("prepender", "north_bypass", new RawPacketOutboundHandler());
    }

    @Override
    public void broadcastRawPacket(final Collection<Player> players, final ByteBuf rawData)
    {
        for (final Player player : players)
        {
            final CraftPlayer craftPlayer = (CraftPlayer) player;
            final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;

            channel.writeAndFlush(new RawPacket(rawData));
        }
    }
}
