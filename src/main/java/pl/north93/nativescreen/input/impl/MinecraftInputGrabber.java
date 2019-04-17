package pl.north93.nativescreen.input.impl;

import static pl.north93.nativescreen.utils.MetadataUtils.getMetadata;
import static pl.north93.nativescreen.utils.MetadataUtils.setMetadata;


import net.minecraft.server.v1_12_R1.NetworkManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import pl.north93.northspigot.event.ChannelInitializeEvent;
import pl.north93.nativescreen.input.INavigationController;

@Log4j2
public class MinecraftInputGrabber implements Listener
{
    private final NavigationControllerImpl navigationController = new NavigationControllerImpl();

    public MinecraftInputGrabber()
    {
    }

    public INavigationController getNavigationController()
    {
        return this.navigationController;
    }

    public boolean isPlayerGrabbed(final Player player)
    {
        final Boolean isGrabbed = getMetadata(player, "isGrabbed");
        return isGrabbed != null && isGrabbed;
    }

    public void grabPlayer(final Player player)
    {
        setMetadata(player, "isGrabbed", true);
    }

    @EventHandler
    public void onInitChannel(final ChannelInitializeEvent event)
    {
        final Channel channel = event.getChannel();

        final NetworkManager networkManager = channel.pipeline().get(NetworkManager.class);
        final NorthChannelHandler handler = new NorthChannelHandler(this, this.navigationController, networkManager);

        channel.pipeline().addBefore("packet_handler", "north_packet_handler", handler);
        log.info("Injected own channel initializer for: {}", channel);
    }
}
