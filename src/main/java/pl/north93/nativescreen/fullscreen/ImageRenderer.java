package pl.north93.nativescreen.fullscreen;

import java.awt.image.BufferedImage;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

public class ImageRenderer implements IMapRenderer
{
    private final BufferedImage bufferedImage;

    public ImageRenderer(final BufferedImage bufferedImage)
    {
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        canvas.putImage(0, 0, this.bufferedImage);
    }
}
