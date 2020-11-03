package pl.north93.nativescreen.renderer.impl;

import java.awt.*;

import gnu.trove.map.hash.TIntByteHashMap;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import pl.north93.nativescreen.renderer.MapColor;

@Slf4j
@UtilityClass
class ColorConverterCache
{
    private static final byte NO_ENTRY_VALUE = -1;
    private static final TIntByteHashMap TRANSLATION_CACHE = new TIntByteHashMap(16_777_216, 1, -1, NO_ENTRY_VALUE);

    static
    {
        for (int r = 0; r < 256; r++)
        {
            for (int g = 0; g < 256; g++)
            {
                for (int b = 0; b < 256; b++)
                {
                    final Color color = new Color(r, g, b);
                    final byte newValue = (byte) MapColor.find(color);

                    TRANSLATION_CACHE.put(color.getRGB(), newValue);
                }
            }
        }

        log.info("Initialised color cache");
    }

    public static byte translateColor(final int rgbValue)
    {
        return TRANSLATION_CACHE.get(rgbValue);
    }
}
