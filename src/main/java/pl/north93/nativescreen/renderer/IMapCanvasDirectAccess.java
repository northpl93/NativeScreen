package pl.north93.nativescreen.renderer;

public interface IMapCanvasDirectAccess
{
    int getHeight();

    int getWidth();

    void setPixelUnsafe(final int x, final int y, final byte color);

    void setPixelUnsafeRGB(final int x, final int y, final int rgbData);

    int calculateIndex(int x, int y);

    byte[] getBytes();
}
