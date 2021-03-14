package pl.north93.nativescreen.renderer;

import java.awt.*;
import java.lang.reflect.Field;

import org.bukkit.map.MapPalette;

/**
 * Quietly tuned MapPalette from Bukkit.
 */
public final class MapColor
{
    private static final Color[] colors;

    static
    {
        colors = fetchColorsArrayFromBukkit();
    }


    // Chat colors constants
    public static final int BLACK        = find(new Color(0, 0, 0));
    public static final int DARK_BLUE    = find(new Color(0, 0, 170));
    public static final int DARK_GREEN   = find(new Color(0, 170, 0));
    public static final int DARK_AQUA    = find(new Color(0, 170, 170));
    public static final int DARK_RED     = find(new Color(170, 0, 0));
    public static final int DARK_PURPLE  = find(new Color(170, 0, 170));
    public static final int GOLD         = find(new Color(255, 128, 0));
    public static final int GRAY         = find(new Color(170, 170, 170));
    public static final int DARK_GRAY    = find(new Color(85, 85, 85));
    public static final int BLUE         = find(new Color(64, 96, 255));
    public static final int GREEN        = find(new Color(0, 255, 0));
    public static final int AQUA         = find(new Color(0, 255, 255));
    public static final int RED          = find(new Color(255, 0, 0));
    public static final int LIGHT_PURPLE = find(new Color(255, 0, 255));
    public static final int YELLOW       = find(new Color(255, 255, 0));
    public static final int WHITE        = find(new Color(255, 255, 255));

    public static int find(final Color color)
    {
        if (color.getAlpha() < 128)
        {
            return 0;
        }

        int result = 0;
        double distance = Double.MAX_VALUE;

        for (int i = 4; i < colors.length; i++)
        {
            final Color c = colors[i];
            final double d = distance(color, c);

            if (d < distance)
            {
                distance = d;
                result = i;
            }
        }

        return result;
    }

    private static double distance(final Color c1, final Color c2)
    {
        final double ra = (c1.getRed() + c2.getRed()) / 2.0;

        final int rd = c1.getRed() - c2.getRed();
        final int gd = c1.getGreen() - c2.getGreen();
        final int bd = c1.getBlue() - c2.getBlue();

        final double weightR = 2 + ra / 256.0;
        final double weightG = 4.0;
        final double weightB = 2 + (255 - ra) / 256.0;

        return weightR * rd * rd + weightG * gd * gd + weightB * bd * bd;
    }

    private static Color[] fetchColorsArrayFromBukkit()
    {
        try
        {
            final Field colorsField = MapPalette.class.getDeclaredField("colors");
            colorsField.setAccessible(true);

            return (Color[]) colorsField.get(null);
        }
        catch (final NoSuchFieldException | IllegalAccessException e)
        {
            throw new RuntimeException("Failed to fetch array of supported colors", e);
        }
    }
}
