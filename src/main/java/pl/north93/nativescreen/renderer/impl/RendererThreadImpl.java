package pl.north93.nativescreen.renderer.impl;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.renderer.IMapRenderer;
import pl.north93.nativescreen.renderer.IRendererThread;

@Log4j2
@ToString(of = {"assignedBoard", "working", "targetFps", "latestFrameTime"})
class RendererThreadImpl extends Thread implements IRendererThread
{
    private static final double ONE_SECOND_NANOS = 1_000_000_000;
    private final MapController mapController;
    private final BoardImpl assignedBoard;
    private boolean working = true;
    @Getter @Setter
    private int targetFps = 30;
    @Getter
    private long latestFrameTime = 0;

    public RendererThreadImpl(final MapController mapController, final BoardImpl assignedBoard)
    {
        super("Renderer thread " + assignedBoard.getIdentifier());
        this.mapController = mapController;
        this.assignedBoard = assignedBoard;
    }

    @Override
    public void run()
    {
        try
        {
            while (this.working)
            {
                this.doTick();
            }
        }
        catch (final Exception e)
        {
            log.error("Exception in map renderer thread", e);
        }
    }

    private synchronized void doTick() throws Exception
    {
        final Collection<Player> playersInRange = this.assignedBoard.getPlayersInRange();
        if (playersInRange.isEmpty())
        {
            // wait until players enter range of our board
            log.info("There are no players in range of board {}, pausing thread...", this.assignedBoard);
            this.wait();
        }

        final RendererHolder rendererHolder = this.assignedBoard.getRendererHolder();
        try (final InProgressRenderer renderer = rendererHolder.startRendering())
        {
            final long renderingStart = System.nanoTime();
            this.doRender(playersInRange, renderer);
            this.latestFrameTime = System.nanoTime() - renderingStart;
        }

        final long timeToWait = Math.max(0, this.getTargetNanosecondsPerFrame() - this.latestFrameTime);
        final long timeToWaitMillis = TimeUnit.NANOSECONDS.toMillis(timeToWait);

        if (log.isDebugEnabled())
        {
            final long latestFrameTimeMillis = TimeUnit.NANOSECONDS.toMillis(this.latestFrameTime);
            log.debug("Rendering frame done, took {}ms, waiting {}ms", latestFrameTimeMillis, timeToWaitMillis);
        }

        if (timeToWaitMillis > 0)
        {
            this.wait(timeToWaitMillis);
        }
    }

    private void doRender(final Collection<Player> playersInRange, final IMapRenderer renderer) throws Exception
    {
        final int width = this.assignedBoard.getWidth();
        final int height = this.assignedBoard.getHeight();

        final MapCanvasImpl canvas = MapCanvasImpl.createFromMaps(width, height);
        renderer.render(this.assignedBoard, canvas);

        this.mapController.pushNewCanvasToAudience(playersInRange, this.assignedBoard, canvas);
    }

    @Override
    public void wakeup()
    {
        if (this.getState() != State.WAITING)
        {
            // We intentionally do not wakeup thread when it's in TIMED_WAITING state,
            // because we don't want to render additional unnecessary frames, when
            // this method is called.
            return;
        }

        log.info("Waking up RendererThread of {}", this.assignedBoard.getIdentifier());
        synchronized (this)
        {
            this.notify();
        }
    }

    public void end()
    {
        this.working = false;
        log.info("RendererThread of {} stopped", this.assignedBoard.getIdentifier());
    }

    @Override
    public long getTargetNanosecondsPerFrame()
    {
        return (int) (ONE_SECOND_NANOS / this.targetFps);
    }
}
