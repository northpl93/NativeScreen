package pl.north93.nativescreen.renderer;

import java.awt.image.BufferedImage;
import java.io.File;

import pl.north93.nativescreen.winapi.NativeImage;

public interface IMapCanvas extends Cloneable
{
    int getHeight();

    int getWidth();

    void setPixel(int x, int y, byte color);

    void putImage(int x, int y, BufferedImage image);

    void putImage(int x, int y, NativeImage nativeImage);

    void putCanvas(int x, int y, IMapCanvas canvas);

    void fill(byte color);

    byte getPixel(int x, int y);

    byte[] getBytes();

    void writeDebugImage(File location);

    IMapCanvas clone();

    boolean equals(Object other);
}
