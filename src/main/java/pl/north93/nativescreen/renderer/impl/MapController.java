package pl.north93.nativescreen.renderer.impl;

import static pl.north93.nativescreen.utils.MetadataUtils.getMetadata;
import static pl.north93.nativescreen.utils.MetadataUtils.setMetadata;


import java.util.Collection;

import net.minecraft.server.v1_12_R1.EntityPlayer;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IMapUploader;
import pl.north93.nativescreen.utils.EntityMetaPacketHelper;

@Log4j2
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
/*default*/ class MapController implements Listener
{
    private final MainPlugin mainPlugin;
    private final IMapUploader mapUploader;

    public void handlePlayerEnter(final MapImpl map, final Player player)
    {
        map.addTracingPlayer(player);

        // put map into ItemFrame client-side
        this.mainPlugin.getServer().getScheduler().runTask(this.mainPlugin, () ->
        {
            // this method is called before entity spawn packets are send, so we must
            // delay sending of entitymetadata
            this.uploadFilledMapItem(player, map.getFrameEntityId(), map.getMapId());
        });

        // wake up the renderer thread, in case if it was paused
        final RendererThreadImpl rendererThread = map.getBoard().getRendererThread();
        rendererThread.wakeup();

        // todo send latestCanvas to player?
    }

    private void uploadFilledMapItem(final Player player, final int frameEntityId, final int mapId)
    {
        final EntityMetaPacketHelper helper = new EntityMetaPacketHelper(frameEntityId);

        final ItemStack mapItem = new ItemStack(Material.MAP, 1, (short) mapId);
        helper.addMeta(6, EntityMetaPacketHelper.MetaType.SLOT, mapItem);
        helper.addMeta(7, EntityMetaPacketHelper.MetaType.VAR_INT, 0);

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        channel.writeAndFlush(helper.complete());
    }

    public int pushNewCanvasToAudience(final Collection<Player> players, final BoardImpl board, final MapCanvasImpl mapCanvas)
    {
        int uploadedMaps = 0;
        for (int i = 0; i < board.getWidth(); i++)
        {
            for (int j = 0; j < board.getHeight(); j++)
            {
                final MapCanvasViewImpl canvasView = mapCanvas.createCanvasView(i, j);
                final MapImpl map = board.getMap(i, j);

                if (map.isCanvasSameAsLatest(canvasView))
                {
                    continue;
                }

                uploadedMaps++;
                map.updateCanvas(canvasView);
                this.mapUploader.uploadMapToAudience(players, map, canvasView);
            }
        }

        return uploadedMaps;
    }

    /*default*/ MapImpl getMapFromEntity(final org.bukkit.entity.Entity entity)
    {
        return getMetadata(entity, "map_mapImpl");
    }

    /*default*/ void updateMapInEntity(final ItemFrame itemFrame, final MapImpl map)
    {
        setMetadata(itemFrame, "map_mapImpl", map);
    }
}
