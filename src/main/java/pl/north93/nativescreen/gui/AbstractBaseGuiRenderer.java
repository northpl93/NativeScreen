package pl.north93.nativescreen.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import pl.north93.nativescreen.input.Key;
import pl.north93.nativescreen.input.NavigationOutputHandler;
import pl.north93.nativescreen.input.helper.CursorRendererComponent;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

@Getter
public abstract class AbstractBaseGuiRenderer implements IMapRenderer, NavigationOutputHandler
{
    private final String windowName;
    private final CursorRendererComponent cursorRendererComponent;

    public AbstractBaseGuiRenderer(final String windowName)
    {
        this.windowName = windowName;
        this.cursorRendererComponent = new CursorRendererComponent();
    }

    @Override
    public final void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        final BufferedImage bufferedImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics graphics = bufferedImage.getGraphics();

        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        graphics.setColor(Color.GRAY);
        graphics.fillRect(0, 0, canvas.getWidth(), 50);

        this.drawHeaderText(bufferedImage, graphics, board);

        this.render(board, bufferedImage, graphics);
        canvas.putImage(0, 0, bufferedImage);

        this.cursorRendererComponent.render(board, canvas);
    }

    public abstract void render(final IBoard board, final BufferedImage bufferedImage, final Graphics graphics);

    private void drawHeaderText(final BufferedImage bufferedImage, final Graphics graphics, final IBoard board)
    {
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("TimesRoman", Font.PLAIN, 45));

        graphics.drawString(this.windowName, 5, 43);

        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        final int targetFps = board.getRendererThread().getTargetFps();
        final long latestFrameTime = TimeUnit.NANOSECONDS.toMillis(board.getRendererThread().getLatestFrameTime());
        final String text = MessageFormat.format("{0} {1}x{2} TFPS: {3} LFT: {4}ms", board.getIdentifier(), width, height, targetFps, latestFrameTime);

        final int textWidth = graphics.getFontMetrics().stringWidth(text);
        graphics.drawString(text, bufferedImage.getWidth() - textWidth, 43);
    }

    @Override
    public void onKeyDown(final Key key)
    {
        this.cursorRendererComponent.onKeyDown(key);
    }

    @Override
    public void onKeyUp(final Key key)
    {
        this.cursorRendererComponent.onKeyUp(key);
    }

    @Override
    public void onMouseMove(final float deltaX, final float deltaY)
    {
        this.cursorRendererComponent.onMouseMove(deltaX, deltaY);
    }
}
