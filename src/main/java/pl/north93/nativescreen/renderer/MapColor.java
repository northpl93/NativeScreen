package pl.north93.nativescreen.renderer;

import java.awt.*;

import gnu.trove.map.TCharIntMap;
import gnu.trove.map.hash.TCharIntHashMap;

/**
 * Klasa mapujaca kolor RGB na minecraftowy kolor mapy.
 * <p>
 * Skopiowane z:
 * /PluginUtils/src/main/java/pl/rafsze/utils/bukkit/gui/map
 *
 * @author Rafał Szewczyk
 */
public class MapColor
{
    private static final Color[] colors;

    static
    {
        final Color[] baseColors = new Color[]{
                new Color(0, 0, 0, 0),
                new Color(127, 178, 56),
                new Color(247, 233, 163),
                new Color(199, 199, 199),
                new Color(255, 0, 0),
                new Color(160, 160, 255),
                new Color(167, 167, 167),
                new Color(0, 124, 0),
                new Color(255, 255, 255),
                new Color(164, 168, 184),
                new Color(151, 109, 77),
                new Color(112, 112, 112),
                new Color(64, 64, 255),
                new Color(143, 119, 72),
                new Color(255, 252, 245),
                new Color(216, 127, 51),
                new Color(178, 76, 216),
                new Color(102, 153, 216),
                new Color(229, 229, 51),
                new Color(127, 204, 25),
                new Color(242, 127, 165),
                new Color(76, 76, 76),
                new Color(153, 153, 153),
                new Color(76, 127, 153),
                new Color(127, 63, 178),
                new Color(51, 76, 178),
                new Color(102, 76, 51),
                new Color(102, 127, 51),
                new Color(153, 51, 51),
                new Color(25, 25, 25),
                new Color(250, 238, 77),
                new Color(92, 219, 213),
                new Color(74, 128, 255),
                new Color(0, 217, 58),
                new Color(129, 86, 49),
                new Color(112, 2, 0)
        };

        colors = new Color[baseColors.length * 4];

        for (int i = 0; i < baseColors.length; i++)
        {

            Color base = baseColors[i];
            colors[i * 4 + 0] = new Color(base.getRed() * 180 / 255, base.getGreen() * 180 / 255, base.getBlue() * 180 / 255);
            colors[i * 4 + 1] = new Color(base.getRed() * 220 / 255, base.getGreen() * 220 / 255, base.getBlue() * 220 / 255);
            colors[i * 4 + 2] = base;
            colors[i * 4 + 3] = new Color(base.getRed() * 135 / 255, base.getGreen() * 135 / 255, base.getBlue() * 135 / 255);
        }
    }

    public static final int TRANSPARENT = 0;

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

    private static final TCharIntMap byChatCode = new TCharIntHashMap(32, 0.5f, '\0', - 1);

    static
    {
        byChatCode.put('0', BLACK);
        byChatCode.put('1', DARK_BLUE);
        byChatCode.put('2', DARK_GREEN);
        byChatCode.put('3', DARK_AQUA);
        byChatCode.put('4', DARK_RED);
        byChatCode.put('5', DARK_PURPLE);
        byChatCode.put('6', GOLD);
        byChatCode.put('7', GRAY);
        byChatCode.put('8', DARK_GRAY);
        byChatCode.put('9', BLUE);
        byChatCode.put('a', GREEN);
        byChatCode.put('A', GREEN);
        byChatCode.put('b', AQUA);
        byChatCode.put('B', AQUA);
        byChatCode.put('c', RED);
        byChatCode.put('C', RED);
        byChatCode.put('d', LIGHT_PURPLE);
        byChatCode.put('D', LIGHT_PURPLE);
        byChatCode.put('e', YELLOW);
        byChatCode.put('E', YELLOW);
        byChatCode.put('f', WHITE);
        byChatCode.put('F', WHITE);
    }

    public static int byChatCode(char code)
    {
        return byChatCode.get(code);
    }

    public static int find(Color color)
    {
        if (color.getAlpha() < 128)
        {
            return 0;
        }

        int result = 0;
        double distance = Double.MAX_VALUE;

        for (int i = 4; i < colors.length; i++)
        {
            Color c = colors[i];
            double d = distance(color, c);

            if (d < distance)
            {
                distance = d;
                result = i;
            }
        }

        return result;
    }

    private static double distance(Color c1, Color c2)
    {
        double ra = (c1.getRed() + c2.getRed()) / 2.0;

        int rd = c1.getRed() - c2.getRed();
        int gd = c1.getGreen() - c2.getGreen();
        int bd = c1.getBlue() - c2.getBlue();

        double weightR = 2 + ra / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - ra) / 256.0;

        return weightR * rd * rd + weightG * gd * gd + weightB * bd * bd;
    }
}
