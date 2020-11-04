package pl.north93.nativescreen.renderer.impl;

import java.awt.*;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import pl.north93.nativescreen.renderer.MapColor;

@Slf4j
@UtilityClass
class ColorConverterCache
{
    private static final byte[] TRANSLATION_CACHE = new byte[16_777_216];

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

                    final int index = (- color.getRGB()) - 1;
                    TRANSLATION_CACHE[index] = newValue;
                }
            }
        }

        log.info("Initialised color cache");
    }

    public static byte translateColor(final int rgbValue)
    {
        final int rgbNoAlpha = 0xff000000 | rgbValue;
        final int index = (- rgbNoAlpha) - 1;
        return TRANSLATION_CACHE[index];
    }
}
