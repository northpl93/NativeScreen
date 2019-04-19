package pl.north93.nativescreen.renderer.impl;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

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
        final ItemFrame itemFrame = this.getItemFrame();
        if (itemFrame == null)
        {
            return -1;
        }

        return itemFrame.getEntityId();
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
        final Entity nmsEntity = this.getNmsEntity();
        if (nmsEntity == null)
        {
            return false;
        }

        final EntityTrackerEntry trackerEntry = EntityTrackerHelper.getTrackerEntry(nmsEntity);
        for (final EntityPlayer trackedPlayer : trackerEntry.trackedPlayers)
        {
            if (player.equals(trackedPlayer.getBukkitEntity()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Zwraca liste graczy sledzacych ta mape.
     *
     * @return lista graczy sledzacych ta mape.
     */
    public Collection<Player> getTrackingPlayers()
    {
        final Entity nmsEntity = this.getNmsEntity();
        if (nmsEntity == null)
        {
            return Collections.emptyList();
        }

        final EntityTrackerEntry trackerEntry = EntityTrackerHelper.getTrackerEntry(nmsEntity);
        // java.util.ConcurrentModificationException: null
        return trackerEntry.trackedPlayers.stream().map(EntityPlayer::getBukkitEntity).collect(Collectors.toSet());
    }

    @Nullable
    private ItemFrame getItemFrame()
    {
        if (this.itemFrame != null && this.itemFrame.isValid())
        {
            return this.itemFrame;
        }

        final ItemFrame newItemFrame = (ItemFrame) Bukkit.getEntity(this.frameId);
        return this.itemFrame = newItemFrame;
    }

    @Nullable
    private Entity getNmsEntity()
    {
        final ItemFrame itemFrame = this.getItemFrame();
        if (itemFrame == null)
        {
            return null;
        }

        return EntityTrackerHelper.toNmsEntity(itemFrame);
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
        final ItemFrame itemFrame = this.getItemFrame();
        if (itemFrame == null)
        {
            return null;
        }

        return itemFrame.getLocation();
    }
}
