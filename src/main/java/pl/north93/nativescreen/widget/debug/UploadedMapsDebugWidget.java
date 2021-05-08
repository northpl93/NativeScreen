package pl.north93.nativescreen.widget.debug;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.MapColor;
import pl.north93.nativescreen.widget.AbstractHistogramWidget;

public class UploadedMapsDebugWidget extends AbstractHistogramWidget
{
    public UploadedMapsDebugWidget(final int width, final int height, final int xBase, final int yBase)
    {
        super(width, height, xBase, yBase, (byte) MapColor.AQUA);
    }

    @Override
    protected int getCurrentValue(final IBoard board)
    {
        return board.getRendererThread().getLatestUploadedMaps();
    }

    @Override
    protected int getCurrentMaxHeight(final IBoard board)
    {
        return board.getWidth() * board.getHeight();
    }
}
