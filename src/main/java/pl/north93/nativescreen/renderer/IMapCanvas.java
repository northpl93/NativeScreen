package pl.north93.nativescreen.renderer;

import java.awt.image.BufferedImage;
import java.io.File;

public interface IMapCanvas
{
    int getHeight();

    int getWidth();

    void setPixel(int x, int y, byte color);

    void putImage(int x, int y, BufferedImage image);

    void putCanvas(int x, int y, IMapCanvas canvas);

    void doDirectAccess(IMapCanvasDirectAccessor directAccessor);

    void fill(byte color);

    byte getPixel(int x, int y);

    void writeDebugImage(File location);

    boolean equals(Object other);
}
