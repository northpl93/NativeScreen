package pl.north93.nativescreen.input.helper;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.north93.nativescreen.input.Key;
import pl.north93.nativescreen.input.NavigationOutputHandler;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;
import pl.north93.nativescreen.renderer.MapColor;

@ToString(of = {"sensitivity", "mouseX", "mouseY"})
public class CursorRendererComponent implements IMapRenderer, NavigationOutputHandler
{
    @Getter @Setter
    private int sensitivity = 5;
    @Getter
    private int mouseX = - 1;
    @Getter
    private int mouseY = - 1;
    private Instant lastUsed = Instant.now();

    @Override
    public void render(final IBoard board, final IMapCanvas canvas, final Player player) throws Exception
    {
        if (this.shouldSkipCursorRendering())
        {
            return;
        }

        if (this.mouseX < 0 || this.mouseY < 0 || this.mouseX > canvas.getWidth() || this.mouseY > canvas.getHeight())
        {
            this.mouseX = canvas.getWidth() / 2;
            this.mouseY = canvas.getHeight() / 2;
        }

        for (int x = this.mouseX - 10; x < this.mouseX + 10; x++)
        {
            for (int y = this.mouseY - 10; y < this.mouseY + 10; y++)
            {
                canvas.setPixel(x, y, (byte) MapColor.RED);
            }
        }
    }

    @Override
    public void onMouseMove(final float deltaX, final float deltaY)
    {
        this.mouseX -= deltaX * this.sensitivity;
        this.mouseY -= deltaY * this.sensitivity;
        this.notifyCursorUsed();
    }

    private boolean shouldSkipCursorRendering()
    {
        final Duration durationSinceLastUsage = Duration.between(this.lastUsed, Instant.now());
        return durationSinceLastUsage.get(ChronoUnit.SECONDS) > 3;
    }

    private void notifyCursorUsed()
    {
        this.lastUsed = Instant.now();
    }

    @Override
    public void onKeyDown(final Key key)
    {
    }

    @Override
    public void onKeyUp(final Key key)
    {
    }
}
