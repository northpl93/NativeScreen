package pl.north93.nativescreen.renderer.impl;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapRenderer;

class BoardImpl implements IBoard
{
    private final MapController      mapController;
    private final RendererThreadImpl rendererThread;
    private final String      name;
    private final int         width, height;
    private final MapImpl[][] maps;
    private IMapRenderer renderer;

    public BoardImpl(final MapController mapController, final String name, final int width, final int height, final MapImpl[][] maps)
    {
        this.rendererThread = new RendererThreadImpl(mapController, this);
        this.mapController = mapController;
        this.name = name;
        this.width = width;
        this.height = height;
        this.maps = maps;
    }

    @Override
    public String getIdentifier()
    {
        return this.name;
    }

    @Override
    public World getWorld()
    {
        final MapImpl firstMap = this.maps[0][0];
        return firstMap.getLocation().getWorld();
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
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
    public void setRenderer(final IMapRenderer renderer)
    {
        this.renderer = renderer;
    }

    @Override
    public @Nullable IMapRenderer getRenderer()
    {
        return this.renderer;
    }

    @Override
    public MapImpl getMap(final int x, final int y)
    {
        return this.maps[x][y];
    }

    @Override
    public RendererThreadImpl getRendererThread()
    {
        return this.rendererThread;
    }

    public MapController getMapController()
    {
        return this.mapController;
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
