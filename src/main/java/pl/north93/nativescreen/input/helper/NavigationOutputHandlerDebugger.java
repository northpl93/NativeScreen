package pl.north93.nativescreen.input.helper;

import org.bukkit.Bukkit;

import pl.north93.nativescreen.input.Key;
import pl.north93.nativescreen.input.NavigationOutputHandler;

/**
 * It's implementation of {@link NavigationOutputHandler} which prints
 * all incoming events to chat.
 */
public class NavigationOutputHandlerDebugger implements NavigationOutputHandler
{
    @Override
    public void onKeyDown(final Key key)
    {
        Bukkit.broadcastMessage("Registered key down event: " + key);
    }

    @Override
    public void onKeyUp(final Key key)
    {
        Bukkit.broadcastMessage("Registered key up event: " + key);
    }

    @Override
    public void onMouseMove(final float deltaX, final float deltaY)
    {
    }
}
