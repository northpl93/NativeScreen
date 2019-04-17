package pl.north93.nativescreen;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

public class TestFullScreenRenderer implements IMapRenderer
{
    @Override
    public void render(final IBoard board, final IMapCanvas canvas, final Player player) throws Exception
    {
        final GraphicsDevice monitor1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        final Rectangle screenRect = monitor1.getDefaultConfiguration().getBounds();

        final BufferedImage capture = new Robot().createScreenCapture(screenRect);
        canvas.putImage(0, 0, capture);
    }
}
