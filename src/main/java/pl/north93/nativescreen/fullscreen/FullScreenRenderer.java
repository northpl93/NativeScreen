package pl.north93.nativescreen.fullscreen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import pl.north93.nativescreen.input.Key;
import pl.north93.nativescreen.input.NavigationOutputHandler;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

public class FullScreenRenderer implements IMapRenderer, NavigationOutputHandler
{
    private final Robot robot;

    public FullScreenRenderer()
    {
        try
        {
            this.robot = new Robot();
        }
        catch (final AWTException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        final GraphicsDevice monitor1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        final Rectangle screenRect = monitor1.getDefaultConfiguration().getBounds();

        final BufferedImage capture = this.robot.createScreenCapture(screenRect);
        canvas.putImage(0, 0, capture);
    }

    @Override
    public void onKeyDown(final Key key)
    {
        if (key == Key.NAVIGATION_W)
        {
            this.robot.keyPress(KeyEvent.VK_W);
        }
        else if (key == Key.NAVIGATION_S)
        {
            this.robot.keyPress(KeyEvent.VK_S);
        }
        else if (key == Key.NAVIGATION_A)
        {
            this.robot.keyPress(KeyEvent.VK_A);
        }
        else if (key == Key.NAVIGATION_D)
        {
            this.robot.keyPress(KeyEvent.VK_D);
        }
        else if (key == Key.SHIFT)
        {
            // SHIFT -> E for NPC interaction
            this.robot.keyPress(KeyEvent.VK_E);
        }
        else if (key == Key.SPACE)
        {
            this.robot.keyPress(KeyEvent.VK_SPACE);
        }
    }

    @Override
    public void onKeyUp(final Key key)
    {
        if (key == Key.NAVIGATION_W)
        {
            this.robot.keyRelease(KeyEvent.VK_W);
        }
        else if (key == Key.NAVIGATION_S)
        {
            this.robot.keyRelease(KeyEvent.VK_S);
        }
        else if (key == Key.NAVIGATION_A)
        {
            this.robot.keyRelease(KeyEvent.VK_A);
        }
        else if (key == Key.NAVIGATION_D)
        {
            this.robot.keyRelease(KeyEvent.VK_D);
        }
        else if (key == Key.SHIFT)
        {
            // SHIFT -> E for NPC interaction
            this.robot.keyRelease(KeyEvent.VK_E);
        }
        else if (key == Key.SPACE)
        {
            this.robot.keyRelease(KeyEvent.VK_SPACE);
        }
    }

    @Override
    public void onMouseMove(final float deltaX, final float deltaY)
    {
        final Point point = MouseInfo.getPointerInfo().getLocation();

        final int newLocationX = (int) (point.getX() + deltaX);
        final int newLocationY = (int) (point.getY() + deltaY);

        this.robot.mouseMove(newLocationX, newLocationY);
    }
}
