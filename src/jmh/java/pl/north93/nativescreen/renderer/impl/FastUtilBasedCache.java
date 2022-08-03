package pl.north93.nativescreen.renderer.impl;

import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;

class FastUtilBasedCache
{
    private static final int COLOR_RANGE = 256;
    private static final Int2ByteMap CACHE = new Int2ByteOpenHashMap();

    static
    {
        for (int r = 0; r < COLOR_RANGE; r++)
        {
            for (int g = 0; g < COLOR_RANGE; g++)
            {
                for (int b = 0; b < COLOR_RANGE; b++)
                {
                    final int colorInRgb = (r << 16) | (g << 8) | b;
                    CACHE.put(colorInRgb, ColorConverterCache.translateColor(colorInRgb));
                }
            }
        }
    }

    public static byte translateColor(final int rgbValue)
    {
        return CACHE.get(rgbValue);
    }
}
