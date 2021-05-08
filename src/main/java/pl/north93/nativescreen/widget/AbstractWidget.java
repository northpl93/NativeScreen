package pl.north93.nativescreen.widget;

import pl.north93.nativescreen.renderer.IMapRenderer;

public abstract class AbstractWidget implements IMapRenderer
{
    protected final int width;
    protected final int height;
    protected final int xBase;
    protected final int yBase;

    public AbstractWidget(final int width, final int height, final int xBase, final int yBase)
    {
        this.width = width;
        this.height = height;
        this.xBase = xBase;
        this.yBase = yBase;
    }
}
