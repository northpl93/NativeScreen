package pl.north93.nativescreen.fullscreen;

import java.util.Random;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

public class RandomColorRenderer implements IMapRenderer
{
    private final Random random = new Random();

    @Override
    public void render(final IBoard board, final IMapCanvas canvas)
    {
        canvas.fill((byte) this.random.nextInt(Byte.MAX_VALUE));
    }
}
