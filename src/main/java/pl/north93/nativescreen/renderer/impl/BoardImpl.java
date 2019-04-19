package pl.north93.nativescreen.renderer.impl;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.World;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapRenderer;

@ToString(of = {"identifier", "width", "height", "renderer"})
class BoardImpl implements IBoard
{
    @Getter
    private final MapController mapController;
    @Getter
    private final RendererThreadImpl rendererThread;
    @Getter
    private final String identifier;
    @Getter
    private final int width, height;
    private final MapImpl[][] maps;
    @Getter @Setter
    private IMapRenderer renderer;

    public BoardImpl(final MapController mapController, final String identifier, final int width, final int height, final MapImpl[][] maps)
    {
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
        final HashSet<Player> players = new HashSet<>();
        for (final MapImpl[] yMaps : this.maps)
        {
            for (final MapImpl map : yMaps)
            {
                players.addAll(map.getTrackingPlayers());
            }
        }
        return players;
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

    /**
     * Niszczy ta tablice i upewnia sie, ze juz nie bedzie dalo
     * sie jej uzyc.
     */
    public void cleanup()
    {
        this.rendererThread.end();
        this.renderer = null;
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
