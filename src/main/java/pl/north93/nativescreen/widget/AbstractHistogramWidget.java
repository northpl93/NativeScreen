package pl.north93.nativescreen.widget;

import java.awt.*;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.MapColor;

public abstract class AbstractHistogramWidget extends AbstractWidget
{
    protected final int[] histogram;
    private final byte histogramColor;
    protected int histogramPosition;

    public AbstractHistogramWidget(final int width, final int height, final int xBase, final int yBase, final byte histogramColor)
    {
        super(width, height, xBase, yBase);
        this.histogramColor = histogramColor;
        this.histogram = new int[width];
    }

    protected abstract int getCurrentValue(final IBoard board);

    protected abstract int getCurrentMaxHeight(final IBoard board);

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        this.updateHistogram(board);

        // render histogram
        for (int i = 0; i < this.width; i++)
        {
            final double heightPercent = Math.min(1, this.histogram[i] / (double) this.getCurrentMaxHeight(board));
            final int barHeight = (int) (heightPercent * this.height);

            for (int j = 0; j < barHeight; j++)
            {
                canvas.setPixel(this.xBase + i, this.yBase + this.height - j, this.histogramColor);
            }
        }

        // render current position
        final byte RED = (byte) MapColor.find(Color.RED);
        for (int i = 0; i < this.height; i++)
        {
            canvas.setPixel(this.xBase + this.histogramPosition, this.yBase + this.height - i, RED);
        }
    }

    private void updateHistogram(final IBoard board)
    {
        this.histogram[this.histogramPosition = ++this.histogramPosition % this.width] = this.getCurrentValue(board);
    }
}
