package pl.north93.nativescreen.renderer.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.NotImplementedException;

import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccessor;

class MapCanvasViewImpl implements IMapCanvas, IMapCanvasDirectAccess
{
    private static final int SINGLE_MAP_SIDE = 128;
    private final MapCanvasImpl canvas;
    private final int xBase, yBase;

    public MapCanvasViewImpl(final MapCanvasImpl canvas, final int xBase, final int yBase, final int xSize, final int ySize)
    {
        this.canvas = canvas;
        this.xBase = xBase;
        this.yBase = yBase;
    }

    @Override
    public int getHeight()
    {
        return SINGLE_MAP_SIDE;
    }

    @Override
    public int getWidth()
    {
        return SINGLE_MAP_SIDE;
    }

    @Override
    public void setPixelUnsafe(final int x, final int y, final byte color)
    {
        this.canvas.setPixelUnsafe(this.xBase + x, this.yBase + y, color);
    }

    @Override
    public void setPixelUnsafeRGB(final int x, final int y, final int rgbData)
    {
        this.canvas.setPixelUnsafeRGB(this.xBase + x, this.yBase + y, rgbData);
    }

    @Override
    public int calculateIndex(final int x, final int y)
    {
        return this.canvas.calculateIndex(this.xBase + x, this.yBase + y);
    }

    @Override
    public byte[] getBytes()
    {
        return this.canvas.getBytes();
    }

    @Override
    public void setPixel(final int x, final int y, final byte color)
    {
        this.canvas.setPixel(this.xBase + x, this.yBase + y, color);
    }

    @Override
    public void putImage(final int x, final int y, final BufferedImage image)
    {
        this.canvas.putImage(this.xBase + x, this.yBase + y, image);
    }

    @Override
    public void putCanvas(final int x, final int y, final IMapCanvas canvas)
    {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void doDirectAccess(final IMapCanvasDirectAccessor directAccessor)
    {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void fill(final byte color)
    {
        throw new NotImplementedException("TODO");
    }

    @Override
    public byte getPixel(final int x, final int y)
    {
        return this.canvas.getPixel(this.xBase + x, this.yBase + y);
    }

    @Override
    public void writeDebugImage(final File location)
    {
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final MapCanvasViewImpl other = (MapCanvasViewImpl) o;

        final int baseIndex = this.canvas.calculateIndex(this.xBase, this.yBase);
        for (int i = 0; i < SINGLE_MAP_SIDE; i++)
        {
            final int currentIndexStart = i * this.canvas.getWidth() + baseIndex;
            final int currentIndexEnd = currentIndexStart + SINGLE_MAP_SIDE;

            final byte[] thisBytes = this.canvas.getBytes();
            final byte[] otherBytes = other.canvas.getBytes();

            if (! Arrays.equals(thisBytes, currentIndexStart, currentIndexEnd, otherBytes, currentIndexStart, currentIndexEnd))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
