package pl.north93.nativescreen.renderer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.server.v1_12_R1.DedicatedServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapManager;
import pl.north93.nativescreen.renderer.IMapUploader;
import pl.north93.nativescreen.renderer.compressor.MultithreadedCompressedMapUploader;

@Slf4j
@ToString(of = "boards")
public class MapManagerImpl implements IMapManager
{
    private final MapController mapController;
    private final List<BoardImpl> boards = new ArrayList<>();

    public MapManagerImpl(final MainPlugin plugin)
    {
        final IMapUploader mapUploader = this.constructMapUploader(plugin);
        this.mapController = new MapController(plugin, mapUploader);

        final MapListener mapListener = new MapListener(this, this.mapController);
        Bukkit.getPluginManager().registerEvents(mapListener, plugin);
    }

    private IMapUploader constructMapUploader(final JavaPlugin plugin)
    {
        final CraftServer craftServer = (CraftServer) plugin.getServer();
        final DedicatedServer dedicatedServer = craftServer.getHandle().getServer();

        final int compressThreshold = dedicatedServer.aG();
        log.info("Compress threshold: {}", compressThreshold);

        if (compressThreshold > 0)
        {
            // whole server must have enabled compression to allow our things to work
            return new MultithreadedCompressedMapUploader(plugin);
        }
        else
        {
            return new StandardMapUploader();
        }
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

    // iterates over all boards and tells them that they should remove specified player
    // from tracking lists
    public void unTrackPlayerFromAllMaps(final Player player)
    {
        for (final BoardImpl board : this.boards)
        {
            board.unTrackPlayerFromThisBoard(player);
        }
    }
}
