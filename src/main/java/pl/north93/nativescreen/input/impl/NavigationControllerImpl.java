package pl.north93.nativescreen.input.impl;

import static java.lang.Math.abs;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.input.INavigationController;
import pl.north93.nativescreen.input.Key;
import pl.north93.nativescreen.input.NavigationOutputHandler;

@Log4j2
@ToString
/*default*/ class NavigationControllerImpl implements INavigationController
{
    private static final double CURSOR_MOVE_EPSILON = 0.5;
    private final Map<Key, Boolean> keyStates = new ConcurrentHashMap<>();
    private final List<NavigationOutputHandler> handlers = new ArrayList<>();

    @Override
    public void registerNavigationHandler(final NavigationOutputHandler handler)
    {
        this.handlers.add(handler);

        final String handlerName = handler.getClass().getName();
        log.info("Registered new navigation handler {}", handlerName);
    }

    public void signalMouseMovement(final float deltaX, final float deltaY)
    {
        final float fixedDeltaX = (abs(deltaX) > CURSOR_MOVE_EPSILON) ? deltaX : 0;
        final float fixedDeltaY = (abs(deltaY) > CURSOR_MOVE_EPSILON) ? deltaY : 0;

        if (fixedDeltaX == 0 && fixedDeltaY == 0)
        {
            return;
        }

        this.handlers.forEach(handler -> handler.onMouseMove(fixedDeltaX, fixedDeltaY));
    }

    public void signalKeyDown(final Key key)
    {
        final Boolean previousState = this.keyStates.put(key, true);
        if (previousState == null || ! previousState)
        {
            // signal key down
            this.handlers.forEach(handler -> handler.onKeyDown(key));
        }
    }

    public void signalKeyUp(final Key key)
    {
        final Boolean previousState = this.keyStates.put(key, false);
        if (previousState != null && previousState)
        {
            // signal key up
            this.handlers.forEach(handler -> handler.onKeyUp(key));
        }
    }

    public void signalKeyHit(final Key key)
    {
        if (this.isKeyDown(key))
        {
            // do not multiply signal hit of same key
            return;
        }

        final Runnable runnable = () -> this.doSignalKeyHit(key);
        Bukkit.getScheduler().runTaskAsynchronously(MainPlugin.getInstance(), runnable);
    }

    private void doSignalKeyHit(final Key key)
    {
        // signal key down
        this.keyStates.put(key, true);
        this.handlers.forEach(handler -> handler.onKeyDown(key));

        try
        {
            Thread.sleep(100);
        }
        catch (final InterruptedException e)
        {
            log.error("Interrupted thread during emulation of key release delay", e);
        }

        // signal key up
        this.keyStates.put(key, false);
        this.handlers.forEach(handler -> handler.onKeyUp(key));
    }

    @Override
    public boolean isKeyDown(final Key key) // check is key pressed
    {
        return this.keyStates.getOrDefault(key, false);
    }
}
