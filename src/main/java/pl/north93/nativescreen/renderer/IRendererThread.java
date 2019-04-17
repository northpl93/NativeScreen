package pl.north93.nativescreen.renderer;

public interface IRendererThread
{
    void start();

    void wakeup();

    int getTargetFps();

    long getLatestFrameTime();

    void setTargetFps(int targetFps);

    int getTargetMillisecondsPerFrame();
}
