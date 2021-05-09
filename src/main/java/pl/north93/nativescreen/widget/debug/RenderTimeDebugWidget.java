package pl.north93.nativescreen.widget.debug;

import java.util.concurrent.TimeUnit;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.MapColor;
import pl.north93.nativescreen.widget.AbstractHistogramWidget;

public class RenderTimeDebugWidget extends AbstractHistogramWidget
{
    private static final double SCALE_MIN_FPS_MILLIS = 66; // 15FPS=66ms

    public RenderTimeDebugWidget(final int width, final int height, final int xBase, final int yBase)
    {
        super(width, height, xBase, yBase, (byte) 5);
    }

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        super.render(board, canvas);

        this.drawFpsBorderLine(canvas, 16); // 60FPS border
        this.drawFpsBorderLine(canvas, 33); // 30FPS border
        this.drawFpsBorderLine(canvas, 41); // 24FPS border
    }

    @Override
    protected int getCurrentValue(final IBoard board)
    {
        final long latestFrameTime = board.getRendererThread().getLatestFrameTime();
        return (int) TimeUnit.NANOSECONDS.toMillis(latestFrameTime);
    }

    @Override
    protected int getCurrentMaxHeight(final IBoard board)
    {
        return (int) SCALE_MIN_FPS_MILLIS;
    }

    private void drawFpsBorderLine(final IMapCanvas canvas, final int millis)
    {
        final byte LIGHT_GRAY = (byte) MapColor.GRAY;

        final double heightPercent = millis / SCALE_MIN_FPS_MILLIS;
        final int barHeight = (int) (heightPercent * this.height);

        for (int i = 0; i < this.width; i++)
        {
            canvas.setPixel(this.xBase + i, this.yBase + this.height - barHeight, LIGHT_GRAY);
        }
    }

    @Override
    public void cleanup()
    {
    }
}
