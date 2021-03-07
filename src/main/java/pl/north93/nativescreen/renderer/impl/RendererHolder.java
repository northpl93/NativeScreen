package pl.north93.nativescreen.renderer.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.nativescreen.renderer.IMapRenderer;

@Slf4j
@ToString(of = "renderer")
class RendererHolder
{
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private IMapRenderer renderer;

    public InProgressRenderer startRendering()
    {
        final Lock lock = this.lock.readLock();
        lock.lock();

        return new InProgressRenderer(this.renderer, lock);
    }

    public void changeRenderer(final IMapRenderer newRenderer)
    {
        final Lock writeLock = this.lock.writeLock();
        try
        {
            writeLock.lock();

            final IMapRenderer previousRenderer = this.renderer;
            this.renderer = newRenderer;

            if (previousRenderer != null)
            {
                previousRenderer.cleanup();
            }
        }
        catch (final Exception e)
        {
            log.error("An exception has been thrown while cleaning up renderer", e);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    public IMapRenderer getRenderer()
    {
        return this.renderer;
    }
}
