package pl.north93.nativescreen.renderer.impl;

import java.awt.*;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import pl.north93.nativescreen.renderer.MapColor;

@Slf4j
@UtilityClass
class ColorConverterCache
{
    private static final int COLOR_RANGE = 256;
    private static final int CUT_ALPHA_BITMASK = 0x00ffffff;
    private static final byte[] TRANSLATION_CACHE = new byte[COLOR_RANGE * COLOR_RANGE * COLOR_RANGE];

    static
    {
        for (int r = 0; r < COLOR_RANGE; r++)
        {
            for (int g = 0; g < COLOR_RANGE; g++)
            {
                for (int b = 0; b < COLOR_RANGE; b++)
                {
                    final Color color = new Color(r, g, b);
                    final byte newValue = (byte) MapColor.find(color);

                    final int index = color.getRGB() & CUT_ALPHA_BITMASK;
                    TRANSLATION_CACHE[index] = newValue;
                }
            }
        }

        log.info("Initialised color cache");
    }

    public static byte translateColor(final int rgbValue)
    {
        return TRANSLATION_CACHE[rgbValue & CUT_ALPHA_BITMASK];
    }
}
