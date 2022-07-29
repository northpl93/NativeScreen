package pl.north93.nativescreen.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import lombok.ToString;
import pl.north93.nativescreen.renderer.IBoard;

@ToString
public class DebugMouseMovementRenderer extends AbstractBaseGuiRenderer
{
    public DebugMouseMovementRenderer()
    {
        super("Cursor debugging");
    }

    @Override
    public void render(final IBoard board, final BufferedImage image, final Graphics graphics)
    {
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("TimesRoman", Font.PLAIN, 40));

        graphics.drawString("x: " + this.getCursorRendererComponent().getMouseX(), 10, 90);
        graphics.drawString("y: " + this.getCursorRendererComponent().getMouseY(), 10, 130);
    }
}
