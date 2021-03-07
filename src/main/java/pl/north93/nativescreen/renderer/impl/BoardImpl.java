package pl.north93.nativescreen.renderer.impl;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapRenderer;

@Slf4j
@ToString(of = {"identifier", "width", "height", "rendererHolder"})
class BoardImpl implements IBoard
{
    private final Map<Player, Integer> trackingPlayers;
    private final RendererHolder rendererHolder;
    @Getter
    private final RendererThreadImpl rendererThread;
    @Getter
    private final MapController mapController;
    @Getter
    private final String identifier;
    @Getter
    private final int width, height;
    private final MapImpl[][] maps;

    public BoardImpl(final MapController mapController, final String identifier, final int width, final int height, final MapImpl[][] maps)
    {
        this.trackingPlayers = new ConcurrentHashMap<>();
        this.rendererHolder = new RendererHolder();
        this.rendererThread = new RendererThreadImpl(mapController, this);
        this.mapController = mapController;
        this.identifier = identifier;
        this.width = width;
        this.height = height;
        this.maps = maps;
    }

    @Override
    public World getWorld()
    {
        final MapImpl firstMap = this.maps[0][0];
        return firstMap.getLocation().getWorld();
    }

    @Override
    public Collection<Player> getPlayersInRange()
    {
        return this.trackingPlayers.keySet();
    }

    @Override
    public void setRenderer(final IMapRenderer renderer)
    {
        this.rendererHolder.changeRenderer(renderer);
    }

    public RendererHolder getRendererHolder()
    {
        return this.rendererHolder;
    }

    @Nullable
    @Override
    public IMapRenderer getRenderer()
    {
        return this.rendererHolder.getRenderer();
    }

    public void addTrackingPlayer(final Player player)
    {
        this.trackingPlayers.compute(player, (p, trackedMaps) ->
        {
            return trackedMaps == null ? 1 : trackedMaps + 1;
        });
    }

    public void removeTrackingPlayer(final Player player)
    {
        this.trackingPlayers.compute(player, (p, trackedMaps) ->
        {
            final int newTrackedMaps = trackedMaps == null ? 0 : trackedMaps - 1;
            if (newTrackedMaps == 0)
            {
                return null;
            }

            return newTrackedMaps;
        });
    }

    @Override
    public MapImpl getMap(final int x, final int y)
    {
        return this.maps[x][y];
    }

    public boolean isEntityBelongsToBoard(final int entityId)
    {
        for (final MapImpl[] yMaps : this.maps)
        {
            for (final MapImpl map : yMaps)
            {
                if (map.getFrameEntityId() == entityId)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void unTrackPlayerFromThisBoard(final Player player)
    {
        for (final MapImpl[] yMaps : this.maps)
        {
            for (final MapImpl map : yMaps)
            {
                map.removeTracingPlayer(player);
            }
        }

        // theoretically calls to removeTracingPlayer should remove player from our map
        // but to make sure we do this again
        this.trackingPlayers.remove(player);
    }

    /**
     * Destroy this board and make sure that this object can't be used again.
     */
    public void cleanup()
    {
        this.rendererThread.end();
        this.setRenderer(null);
        this.trackingPlayers.clear();
        for (int x = 0; x < this.width; x++)
        {
            for (int y = 0; y < this.height; y++)
            {
                this.maps[x][y].cleanup();
                this.maps[x][y] = null; // upewniamy sie ze tablica jest bezuzyteczna
            }
        }
    }
}
