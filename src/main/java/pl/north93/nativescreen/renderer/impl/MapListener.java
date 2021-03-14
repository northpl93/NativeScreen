package pl.north93.nativescreen.renderer.impl;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import lombok.ToString;
import pl.north93.nmsutils.tracker.event.EntityTrackedPlayerEvent;
import pl.north93.nmsutils.tracker.event.EntityUnTrackedPlayerEvent;

@ToString(onlyExplicitlyIncluded = true)
public class MapListener implements Listener
{
    private final MapManagerImpl mapManager;
    private final MapController mapController;

    public MapListener(final MapManagerImpl mapManager, final MapController mapController)
    {
        this.mapManager = mapManager;
        this.mapController = mapController;
    }

    @EventHandler
    public void handleMapUploadWhenTracked(final EntityTrackedPlayerEvent event)
    {
        final MapImpl map = this.mapController.getMapFromEntity(event.getEntity());
        if (map == null)
        {
            return;
        }

        this.mapController.handlePlayerEnter(map, event.getPlayer());
    }

    @EventHandler
    public void handleMapUnTrack(final EntityUnTrackedPlayerEvent event)
    {
        final MapImpl map = this.mapController.getMapFromEntity(event.getEntity());
        if (map == null)
        {
            return;
        }

        map.removeTracingPlayer(event.getPlayer());
    }

    @EventHandler
    public void deletePlayerMapData(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        // make sure we don't create any memory leaks
        this.mapManager.unTrackPlayerFromAllMaps(player);
    }

    @EventHandler
    public void deleteBoardWhenWorldUnloads(final WorldUnloadEvent event)
    {
        final World world = event.getWorld();
        for (final BoardImpl board : this.mapManager.getBoards())
        {
            if (world.equals(board.getWorld()))
            {
                // pozostawienie tablic w niezaladowanych swiatach powoduje wyciek pamieci
                this.mapManager.removeBoard(board);
            }
        }
    }

    @EventHandler
    public void onInteractWithMap(final PlayerInteractAtEntityEvent event)
    {
        final Entity entity = event.getRightClicked();
        if (! (entity instanceof ItemFrame))
        {
            return;
        }

        if (this.isEntityBelongsToAnyBoard(entity.getEntityId()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMapDestroy(final HangingBreakEvent event)
    {
        final Hanging entity = event.getEntity();
        if (! (entity instanceof ItemFrame))
        {
            return;
        }

        if (this.isEntityBelongsToAnyBoard(entity.getEntityId()))
        {
            event.setCancelled(true);
        }
    }

    private boolean isEntityBelongsToAnyBoard(final int entityId)
    {
        for (final BoardImpl board : this.mapManager.getBoards())
        {
            if (board.isEntityBelongsToBoard(entityId))
            {
                return true;
            }
        }

        return false;
    }
}
