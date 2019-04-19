package pl.north93.nativescreen.renderer;

import org.bukkit.Location;

public interface IMap
{
    /**
     * @return tablica do ktorej nalezy ta mapa.
     */
    IBoard getBoard();

    Location getLocation();
}
