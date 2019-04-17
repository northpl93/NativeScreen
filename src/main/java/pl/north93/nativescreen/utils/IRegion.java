package pl.north93.nativescreen.utils;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.apache.commons.lang3.tuple.Pair;

public interface IRegion extends Iterable<Block>, Cloneable
{
    World getWorld();

    Location getCenter();

    List<Block> getBlocks();

    boolean contains(int x, int y, int z);

    boolean contains(Block b);

    boolean contains(Location l);

    List<Pair<Integer, Integer>> getChunksCoordinates();

    List<Chunk> getChunks();
}
