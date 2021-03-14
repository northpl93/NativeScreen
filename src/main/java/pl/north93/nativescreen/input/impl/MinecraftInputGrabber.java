package pl.north93.nativescreen.input.impl;

import static pl.north93.nativescreen.utils.MetadataUtils.getMetadata;
import static pl.north93.nativescreen.utils.MetadataUtils.setMetadata;


import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.input.INavigationController;
import pl.north93.nmsutils.protocol.IProtocolManager;

@Log4j2
public class MinecraftInputGrabber implements Listener
{
    private final NavigationControllerImpl navigationController = new NavigationControllerImpl();

    public MinecraftInputGrabber(final IProtocolManager protocolHooker)
    {
        protocolHooker.registerHandler(new GrabberPacketHandler(this, this.navigationController));
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

    public boolean grabPlayer(final Player player)
    {
        setMetadata(player, "isGrabbed", true);

        // todo fetch screen&vehicle data and set proper yaw&pitch
        final Entity vehicle = player.getVehicle();
        if (vehicle == null)
        {
            return false;
        }

        player.leaveVehicle();

        final Location location = player.getLocation();
        location.setYaw(0);
        location.setPitch(0);

        player.teleport(location);
        vehicle.addPassenger(player);

        final CraftEntity craftEntity = (CraftEntity) vehicle;
        craftEntity.getHandle().setPosition(79.5, 7.75, 466.5);
        craftEntity.getHandle().yaw = 90;
        craftEntity.getHandle().pitch = 0;

        vehicle.setGravity(false);
        return true;
    }

    public void unGrabPlayer(final Player player)
    {
        setMetadata(player, "isGrabbed", false);
    }
}
