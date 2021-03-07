package pl.north93.nativescreen.renderer.impl;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IMap;
import pl.north93.nativescreen.utils.MetadataUtils;

@ToString(of = {"frameId", "itemFrame"})
class MapImpl implements IMap
{
    @Getter
    private final BoardImpl board;
    private final UUID frameId;
    private final Set<Player> trackingPlayers;
    private ItemFrame itemFrame;

    public MapImpl(final MapController controller, final BoardImpl board, final ItemFrame itemFrame)
    {
        this.board = board;
        this.frameId = itemFrame.getUniqueId();
        this.trackingPlayers = new ConcurrentSet<>();
        this.itemFrame = itemFrame;
        controller.updateMapInEntity(itemFrame, this);
    }

    /**
     * Returns entity identifier used by this map.
     * -1 if this map doesn't have spawned entity.
     *
     * @return Entity ID of this map.
     */
    public int getFrameEntityId()
    {
        return this.getItemFrame().map(ItemFrame::getEntityId).orElse(- 1);
    }

    /**
     * Checks does this map is tracking specified player.
     * In other words, does the player see the map.
     *
     * @param player Player who we are checking.
     * @return True if the player can see the map.
     */
    public boolean isTrackedBy(final Player player)
    {
        return this.trackingPlayers.contains(player);
    }

    public void addTracingPlayer(final Player player)
    {
        if (this.trackingPlayers.add(player))
        {
            this.board.addTrackingPlayer(player);
        }
    }

    public void removeTracingPlayer(final Player player)
    {
        if (this.trackingPlayers.remove(player))
        {
            this.board.removeTrackingPlayer(player);
        }
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

    /**
     * Kills entity that belongs to this frame.
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
        this.trackingPlayers.clear();
    }

    @Override
    public Location getLocation()
    {
        return this.getItemFrame().map(ItemFrame::getLocation).orElse(null);
    }
}
