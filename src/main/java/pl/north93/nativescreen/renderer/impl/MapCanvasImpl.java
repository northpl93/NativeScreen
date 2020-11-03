package pl.north93.nativescreen.renderer.impl;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.map.MapPalette;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.winapi.NativeImage;

@Log4j2
@ToString(of = {"xSize", "ySize"})
final class MapCanvasImpl implements IMapCanvas
{
    private static final int SINGLE_MAP_SIDE = 128;
    private final int xSize, ySize;
    private final byte[] buffer;

    public MapCanvasImpl(final int xSize, final int ySize, final byte[] buffer)
    {
        this.xSize = xSize;
        this.ySize = ySize;
        this.buffer = buffer;
    }

    public MapCanvasImpl(final int xSize, final int ySize)
    {
        this(xSize, ySize, new byte[xSize * ySize]);
    }

    public static MapCanvasImpl createFromMaps(final int xMaps, final int yMaps)
    {
        return new MapCanvasImpl(xMaps * SINGLE_MAP_SIDE, yMaps * SINGLE_MAP_SIDE);
    }

    @Override
    public int getHeight()
    {
        return this.ySize;
    }

    @Override
    public int getWidth()
    {
        return this.xSize;
    }

    @Override
    public void setPixel(final int x, final int y, final byte color)
    {
        if (x < 0 || y < 0 || x >= this.xSize || y >= this.ySize)
            return;

        this.buffer[this.calculateIndex(x, y)] = color;
    }

    @Override
    public void putImage(final int modifierX, final int modifierY, final BufferedImage image)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final int[] rgbArray = new int[width * height];
        image.getRGB(0, 0, width, height, rgbArray, 0, width);

        final int maxX = width + modifierX;
        final int maxY = height + modifierY;

        int rgbIndex = 0;
        for (int y = 0; y < maxY; y++)
        {
            for (int x = 0; x < maxX; x++)
            {
                final byte color = ColorConverterCache.translateColor(rgbArray[rgbIndex++]);
                this.setPixel(x, y, color);
            }
        }
    }

    @Override
    public void putImage(final int modifierX, final int modifierY, final NativeImage nativeImage)
    {
        final int maxX = nativeImage.getWidth() + modifierX;
        final int maxY = nativeImage.getHeight() + modifierY;

        int nativeIndex = 0;
        final int[] nativeImageData = nativeImage.getData();

        for (int y = modifierY; y < maxY; y++)
        {
            for (int x = modifierX; x < maxX; x++)
            {
                final byte color = ColorConverterCache.translateColor(nativeImageData[nativeIndex++]);
                this.setPixel(x, y, color);
            }
        }
    }

    @Override
    public void putCanvas(final int x, final int y, final IMapCanvas canvas)
    {
        final MapCanvasImpl impl = (MapCanvasImpl) canvas;

        for (int actualY = 0; actualY < impl.ySize; actualY++)
        {
            for (int actualX = 0; actualX < impl.xSize; actualX++)
            {
                final byte pixel = impl.getPixel(actualX, actualY);
                this.setPixel(actualX + x, actualY + y, pixel);
            }
        }
    }


    @Override
    public void fill(final byte color)
    {
        Arrays.fill(this.buffer, color);
    }

    @Override
    public byte getPixel(final int x, final int y)
    {
        return this.buffer[this.calculateIndex(x, y)];
    }

    @Override
    public byte[] getBytes()
    {
        return this.buffer;
    }

    public MapCanvasImpl getSubMapCanvas(final int xMap, final int yMap)
    {
        // definujemy nowa tablice na mape 128x128 pixeli
        final byte[] subMap = new byte[SINGLE_MAP_SIDE * SINGLE_MAP_SIDE];

        // szukamy punktu poczatkowego skad zaczynamy kopiowac
        final int startPoint = this.calculateIndex(xMap * 128, yMap * 128);

        for (int lines = 0, destLoc = 0; lines < SINGLE_MAP_SIDE; lines++, destLoc += SINGLE_MAP_SIDE)
        {
            // szukamy startu aktualnej linijki. kazda linijka ma 128 pixele
            final int currentLineStart = startPoint + (lines * this.xSize);
            System.arraycopy(this.buffer, currentLineStart, subMap, destLoc, SINGLE_MAP_SIDE);
        }

        return new MapCanvasImpl(SINGLE_MAP_SIDE, SINGLE_MAP_SIDE, subMap);
    }

    @Override
    public void writeDebugImage(final File location)
    {
        final BufferedImage image = new BufferedImage(this.xSize, this.ySize, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < this.xSize; x++)
        {
            for (int y = 0; y < this.ySize; y++)
            {
                image.setRGB(x, y, MapPalette.getColor(this.getPixel(x, y)).getRGB());
            }
        }
        try
        {
            ImageIO.write(image, "png", location);
        }
        catch (final IOException e)
        {
            log.error("Failed to write debug image", e);
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public IMapCanvas clone()
    {
        final byte[] bytes = new byte[this.buffer.length];
        System.arraycopy(this.buffer, 0, bytes, 0, this.buffer.length);

        return new MapCanvasImpl(this.xSize, this.ySize, bytes);
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

        final MapCanvasImpl mapCanvas = (MapCanvasImpl) o;
        return Arrays.equals(this.buffer, mapCanvas.buffer);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(this.buffer);
    }

    private int calculateIndex(final int x, final int y)
    {
        return y * this.xSize + x;
    }
}