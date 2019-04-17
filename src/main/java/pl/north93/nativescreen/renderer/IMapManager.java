package pl.north93.nativescreen.renderer;

import java.util.Collection;

import org.bukkit.Location;

public interface IMapManager
{
    IBoard createBoard(String identifier, Location leftCorner, Location rightCorner);

    Collection<? extends IBoard> getBoards();

    IBoard getBoardByName(String name);

    void removeBoard(IBoard board);
}
