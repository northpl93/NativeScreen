package pl.north93.nativescreen.renderer;

import javax.annotation.Nullable;

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

    @Nullable
    IMapRenderer getRenderer();

    IMap getMap(int x, int y);

    IRendererThread getRendererThread();
}
