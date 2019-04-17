package pl.north93.nativescreen.renderer;

import org.bukkit.entity.Player;

public interface IMapRenderer
{
    void render(IBoard board, IMapCanvas canvas, Player player) throws Exception;
}
