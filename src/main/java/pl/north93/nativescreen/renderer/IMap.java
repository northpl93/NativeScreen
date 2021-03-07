package pl.north93.nativescreen.renderer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IMap
{
    short getMapId();

    /**
     * @return board for which this map belongs.
     */
    IBoard getBoard();

    Location getLocation();

    /**
     * Checks does this map is tracking specified player.
     * In other words, does the player see the map.
     *
     * @param player Player who we are checking.
     * @return True if the player can see the map.
     */
    boolean isTrackedBy(Player player);
}
