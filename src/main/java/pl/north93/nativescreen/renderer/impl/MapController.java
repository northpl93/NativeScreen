package pl.north93.nativescreen.renderer.impl;

import static pl.north93.nativescreen.utils.MetadataUtils.deleteMetadata;
import static pl.north93.nativescreen.utils.MetadataUtils.getMetadata;
import static pl.north93.nativescreen.utils.MetadataUtils.getMetadataOrCompute;
import static pl.north93.nativescreen.utils.MetadataUtils.setMetadata;


import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.server.v1_12_R1.EntityPlayer;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import io.netty.channel.Channel;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.utils.EntityMetaPacketHelper;

@Log4j2
@ToString(onlyExplicitlyIncluded = true)
class MapController implements Listener
{
    public void handlePlayerEnter(final MapImpl map, final Player player)
    {
        final PlayerMapData playerMapData = this.getOrComputePlayerMapData(player);
        final int mapId = playerMapData.getMapId(map);

        // wysylamy do gracza informacje o umieszczeniu mapy w ramce
        this.uploadFilledMapItem(player, map.getFrameEntityId(), mapId);

        // wybudzamy wątek renderera, na wypadek gdyby oczekiwał na
        final RendererThreadImpl rendererThread = map.getBoard().getRendererThread();
        //rendererThread.wakeup();

        if (playerMapData.isClientCanvasMatchesServer(map))
        {
            // canvas bedacy u klienta pasuje do tego na serwerze
            return;
        }

        // uploadujemy canvas serwera do klienta i ustawiamy go jako aktywny u klienta
        playerMapData.uploadServerCanvasToClient(map);
    }

    private void uploadFilledMapItem(final Player player, final int frameEntityId, final int mapId)
    {
        final EntityMetaPacketHelper helper = new EntityMetaPacketHelper(frameEntityId);

        final ItemStack mapItem = new ItemStack(Material.MAP, 1, (short) mapId);
        helper.addMeta(6, EntityMetaPacketHelper.MetaType.SLOT, mapItem);

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        channel.writeAndFlush(helper.complete());
    }

    public void pushNewCanvasToBoardForPlayer(final Player player, final BoardImpl board, final MapCanvasImpl mapCanvas)
    {
        this.getPlayerMapData(player).ifPresent(playerMapData -> this.doPushNewCanvasToBoardForPlayer(playerMapData, board, mapCanvas));
    }

    private void doPushNewCanvasToBoardForPlayer(final PlayerMapData playerMapData, final BoardImpl board, final MapCanvasImpl mapCanvas)
    {
        int notUploaded = 0;
        for (int i = 0; i < board.getWidth(); i++)
        {
            for (int j = 0; j < board.getHeight(); j++)
            {
                final MapCanvasImpl subMapCanvas = mapCanvas.getSubMapCanvas(i, j);
                final MapImpl map = board.getMap(i, j);

                final MapContainer mapContainer = playerMapData.getOrComputeContainer(map);
                mapContainer.setServerCanvas(subMapCanvas);

                if (playerMapData.isClientCanvasMatchesServer(map))
                {
                    notUploaded++;
                    continue;
                }

                if (playerMapData.isMapVisible(map))
                {
                    playerMapData.uploadServerCanvasToClient(map);
                }
            }
        }

        final double total = board.getHeight() * board.getWidth();
        final double percent = notUploaded / total * 100;
        log.debug("Skipped uploading of {}% maps", percent);
    }

    public PlayerMapData getOrComputePlayerMapData(final Player player)
    {
        final Supplier<PlayerMapData> defaultValue = () -> new PlayerMapData(player);
        return getMetadataOrCompute(player, "PlayerMapData", defaultValue);
    }

    public Optional<PlayerMapData> getPlayerMapData(final Player player)
    {
        return Optional.ofNullable(getMetadata(player, "PlayerMapData"));
    }

    public void deletePlayerMapData(final Player player)
    {
        deleteMetadata(player, "PlayerMapData");
        log.info("Deleted map metadata of player {}", player.getName());
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
