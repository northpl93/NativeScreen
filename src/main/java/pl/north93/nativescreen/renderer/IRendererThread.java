package pl.north93.nativescreen.renderer;

public interface IRendererThread
{
    void start();

    /**
     * Wakes up thread if it's actual in WAITING state.
     */
    void wakeup();

    int getTargetFps();

    void setTargetFps(int targetFps);

    int getTargetMillisecondsPerFrame();

    long getLatestFrameTime();
}
