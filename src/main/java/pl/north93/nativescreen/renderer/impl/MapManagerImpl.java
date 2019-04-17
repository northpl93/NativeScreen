package pl.north93.nativescreen.renderer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapManager;

public class MapManagerImpl implements IMapManager
{
    private final MapController mapController = new MapController();
    private final List<BoardImpl> boards = new ArrayList<>();

    public MapManagerImpl(final JavaPlugin plugin)
    {
        final MapListener mapListener = new MapListener(this, this.mapController);
        Bukkit.getPluginManager().registerEvents(mapListener, plugin);
    }

    @Override
    public BoardImpl createBoard(final String identifier, final Location leftCorner, final Location rightCorner)
    {
        final BoardImpl board = BoardFactory.createBoard(this.mapController, identifier, leftCorner, rightCorner);
        this.boards.add(board);
        return board;
    }

    @Override
    public Collection<BoardImpl> getBoards()
    {
        return new ArrayList<>(this.boards);
    }

    @Override
    public IBoard getBoardByName(final String name)
    {
        for (final BoardImpl board : this.boards)
        {
            if (board.getIdentifier().equals(name))
            {
                return board;
            }
        }

        return null;
    }

    @Override
    public void removeBoard(final IBoard board)
    {
        final BoardImpl boardImpl = (BoardImpl) board;
        if (this.boards.remove(boardImpl))
        {
            boardImpl.cleanup();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
