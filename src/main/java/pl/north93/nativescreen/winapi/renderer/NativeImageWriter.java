package pl.north93.nativescreen.winapi.renderer;

import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccessor;
import pl.north93.nativescreen.winapi.NativeImage;

public class NativeImageWriter implements IMapCanvasDirectAccessor
{
    private final NativeImage nativeImage;
    private final int modifierX;
    private final int modifierY;

    public NativeImageWriter(final NativeImage nativeImage, final int modifierX, final int modifierY)
    {
        this.nativeImage = nativeImage;
        this.modifierX = modifierX;
        this.modifierY = modifierY;
    }

    @Override
    public void doDirectAccess(final IMapCanvasDirectAccess directAccess)
    {
        final int maxX = this.nativeImage.getWidth() + this.modifierX;
        final int maxY = this.nativeImage.getHeight() + this.modifierY;

        int nativeIndex = 0;
        final int[] nativeImageData = this.nativeImage.getData();

        for (int y = this.modifierY; y < maxY; y++)
        {
            for (int x = this.modifierX; x < maxX; x++)
            {
                directAccess.setPixelUnsafeRGB(x, y, nativeImageData[nativeIndex++]);
            }
        }
    }
}
