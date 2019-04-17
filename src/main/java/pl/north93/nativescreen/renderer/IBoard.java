package pl.north93.nativescreen.renderer;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface IBoard
{
    String getIdentifier();

    World getWorld();

    int getWidth();

    int getHeight();

    Collection<Player> getPlayersInRange();

    void setRenderer(IMapRenderer renderer);

    IMapRenderer getRenderer();

    IMap getMap(int x, int y);

    IRendererThread getRendererThread();
}
