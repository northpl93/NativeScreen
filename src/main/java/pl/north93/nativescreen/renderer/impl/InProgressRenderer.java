package pl.north93.nativescreen.renderer.impl;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;

import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

@AllArgsConstructor
@ToString(of = {"renderer", "lock"})
class InProgressRenderer implements IMapRenderer, Closeable
{
    private final IMapRenderer renderer;
    private final Lock lock;

    @Override
    public void close()
    {
        this.lock.unlock();
    }

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        if (this.renderer == null)
        {
            return;
        }

        this.renderer.render(board, canvas);
    }
}
