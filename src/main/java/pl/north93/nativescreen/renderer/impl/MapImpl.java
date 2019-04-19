package pl.north93.nativescreen.renderer.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import lombok.ToString;
import pl.north93.nativescreen.renderer.IMap;
import pl.north93.nativescreen.utils.EntityTrackerHelper;
import pl.north93.nativescreen.utils.MetadataUtils;

@ToString(of = {"frameId", "itemFrame"})
class MapImpl implements IMap
{
    private final BoardImpl board;
    private final UUID      frameId;
    private       ItemFrame itemFrame;

    public MapImpl(final MapController controller, final BoardImpl board, final ItemFrame itemFrame)
    {
        this.board = board;
        this.frameId = itemFrame.getUniqueId();
        this.itemFrame = itemFrame;
        controller.updateMapInEntity(itemFrame, this);
    }

    /**
     * @return tablica do ktorej nalezy ta mapa.
     */
    public BoardImpl getBoard()
    {
        return this.board;
    }

    /**
     * Zwraca ID entity ramki uzywanej przez ta mape.
     *
     * @return ID entity ramki zawierajacej mape.
     */
    public int getFrameEntityId()
    {
        return this.getItemFrame().map(ItemFrame::getEntityId).orElse(- 1);
    }

    /**
     * Sprawdza czy ta mapa jest sledzona przez podanego gracza.
     * Inaczej mowiac czy jest w zasiegu danego gracza.
     *
     * @param player Gracz ktorego sprawdzamy.
     * @return True jesli mapa jest widoczna u danego gracza.
     */
    public boolean isTrackedBy(final Player player)
    {
        return this.getNmsEntity().map(nmsEntity ->
        {
            final EntityTrackerEntry trackerEntry = EntityTrackerHelper.getTrackerEntry(nmsEntity);
            for (final EntityPlayer trackedPlayer : trackerEntry.trackedPlayers)
            {
                if (player.equals(trackedPlayer.getBukkitEntity()))
                {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    /**
     * Zwraca liste graczy sledzacych ta mape.
     *
     * @return lista graczy sledzacych ta mape.
     */
    public Collection<Player> getTrackingPlayers()
    {
        return this.getNmsEntity().map(nmsEntity ->
        {
            final EntityTrackerEntry trackerEntry = EntityTrackerHelper.getTrackerEntry(nmsEntity);
            // java.util.ConcurrentModificationException: null
            return trackerEntry.trackedPlayers.stream();
        }).orElseGet(Stream::empty).map(EntityPlayer::getBukkitEntity).collect(Collectors.toList());
    }

    private Optional<ItemFrame> getItemFrame()
    {
        if (this.itemFrame != null && this.itemFrame.isValid())
        {
            return Optional.of(this.itemFrame);
        }

        final ItemFrame newItemFrame = (ItemFrame) Bukkit.getEntity(this.frameId);
        return Optional.ofNullable(this.itemFrame = newItemFrame);
    }

    private Optional<Entity> getNmsEntity()
    {
        return this.getItemFrame().map(EntityTrackerHelper::toNmsEntity);
    }

    /**
     * Zabija ramke nalezaca do tej mapy.
     */
    public void cleanup()
    {
        MetadataUtils.removeEntityMetadata(this.frameId);

        final CraftItemFrame itemFrame = (CraftItemFrame) this.itemFrame;
        if (itemFrame == null)
        {
            return;
        }

        this.itemFrame = null;
        itemFrame.getHandle().die();
    }

    @Override
    public Location getLocation()
    {
        return this.getItemFrame().map(ItemFrame::getLocation).orElse(null);
    }
}
