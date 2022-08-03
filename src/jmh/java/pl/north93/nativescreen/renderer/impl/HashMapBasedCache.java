package pl.north93.nativescreen.renderer.impl;

import java.util.HashMap;
import java.util.Map;

class HashMapBasedCache
{
    private static final int COLOR_RANGE = 256;
    private static final Map<Integer, Byte> CACHE = new HashMap<>();

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
