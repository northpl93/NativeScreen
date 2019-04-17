package pl.north93.nativescreen.renderer.impl;

import java.util.Collection;

import org.bukkit.entity.Player;

import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.renderer.IMapRenderer;
import pl.north93.nativescreen.renderer.IRendererThread;

@Log4j2
class RendererThreadImpl extends Thread implements IRendererThread
{
    private static final double ONE_SECOND = 1000D;
    private final MapController mapController;
    private final BoardImpl assignedBoard;
    private boolean working = true;
    private long frameTime = 0;
    private int targetFps = 30;

    public RendererThreadImpl(final MapController mapController, final BoardImpl assignedBoard)
    {
        this.mapController = mapController;
        this.assignedBoard = assignedBoard;
    }

    @Override
    public synchronized void wakeup()
    {
        this.notify();
    }

    @Override
    public int getTargetFps()
    {
        return this.targetFps;
    }

    @Override
    public long getLatestFrameTime()
    {
        return this.frameTime;
    }

    @Override
    public void setTargetFps(final int targetFps)
    {
        this.targetFps = targetFps;
    }

    @Override
    public int getTargetMillisecondsPerFrame()
    {
        return (int) (ONE_SECOND / this.targetFps);
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
            log.info("There are no players in range of board {}, waiting...", this.assignedBoard);
            this.wait(1000);
        }

        final IMapRenderer renderer = this.assignedBoard.getRenderer();
        if (renderer == null)
        {
            return;
        }

        final long renderingStart = System.currentTimeMillis();
        this.doRender(playersInRange, renderer);
        this.frameTime = System.currentTimeMillis() - renderingStart;

        final long timeToWait = Math.max(0, this.getTargetMillisecondsPerFrame() - this.frameTime);
        log.debug("Rendering frame done, took {}ms, waiting {}ms", this.frameTime, timeToWait);

        if (timeToWait > 0)
        {
            this.wait(timeToWait);
        }
    }

    private void doRender(final Collection<Player> playersInRange, final IMapRenderer renderer) throws Exception
    {
        final int width = this.assignedBoard.getWidth();
        final int height = this.assignedBoard.getHeight();

        for (final Player player : playersInRange)
        {
            final MapCanvasImpl canvas = MapCanvasImpl.createFromMaps(width, height);
            renderer.render(this.assignedBoard, canvas, player);

            this.mapController.pushNewCanvasToBoardForPlayer(player, this.assignedBoard, canvas);
        }
    }
}
