package pl.north93.nativescreen.renderer.compressor.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.nativescreen.renderer.compressor.IPacketCompressor;
import pl.north93.northspigot.event.ChannelInitializeEvent;

@ToString
@AllArgsConstructor
public class CompressionInjector implements Listener
{
    private final IPacketCompressor packetCompressor;

    @EventHandler
    public void onChannelInitialize(final ChannelInitializeEvent event)
    {
        this.packetCompressor.injectChannel(event.getChannel());
    }
}
