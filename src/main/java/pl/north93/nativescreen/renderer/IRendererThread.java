package pl.north93.nativescreen.renderer;

/**
 * Each {@link IBoard} has own {@link IRendererThread} which is responsible
 * for executing {@link IMapRenderer#render(IBoard, IMapCanvas)} method.
 */
public interface IRendererThread
{
    /**
     * Starts renderer thread.
     * Should be called only once, after creation of board.
     */
    void start();

    /**
     * Wakes up thread if it's actual in WAITING state.
     */
    void wakeup();

    int getTargetFps();

    void setTargetFps(int targetFps);

    long getTargetNanosecondsPerFrame();

    long getLatestFrameTime();
}
