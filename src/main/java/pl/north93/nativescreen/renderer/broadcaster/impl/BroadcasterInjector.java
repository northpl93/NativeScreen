package pl.north93.nativescreen.renderer.broadcaster.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.nativescreen.renderer.broadcaster.IPacketBroadcaster;
import pl.north93.nmsutils.protocol.event.ChannelInitializeEvent;

@ToString
@AllArgsConstructor
public class BroadcasterInjector implements Listener
{
    private final IPacketBroadcaster packetBroadcaster;

    @EventHandler
    public void onChannelInitialize(final ChannelInitializeEvent event)
    {
        this.packetBroadcaster.injectChannel(event.getChannel());
    }
}
