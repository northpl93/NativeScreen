package pl.north93.nativescreen.winapi.renderer;

import com.sun.jna.platform.win32.WinDef.HWND;

import lombok.ToString;
import pl.north93.nativescreen.input.Key;
import pl.north93.nativescreen.input.NavigationOutputHandler;
import pl.north93.nativescreen.input.helper.CursorRendererComponent;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;
import pl.north93.nativescreen.renderer.MapColor;
import pl.north93.nativescreen.winapi.BetterUser32;
import pl.north93.nativescreen.winapi.NativeImage;
import pl.north93.nativescreen.winapi.Screenshoter;
import pl.north93.nativescreen.winapi.WindowsInputEmulator;

@ToString
public class NativeWindowRenderer implements IMapRenderer, NavigationOutputHandler
{
    private final String windowClassName;
    private final CursorRendererComponent cursorRendererComponent;

    public NativeWindowRenderer(final String windowClassName)
    {
        // SunAwtFrame, LWJGL, The Witcher, TaskManagerWindow
        this.windowClassName = windowClassName;
        this.cursorRendererComponent = new CursorRendererComponent();
    }

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        final NativeImage image = this.tryCaptureImage();
        if (image == null)
        {
            canvas.fill((byte) MapColor.RED);
            return;
        }

        final int startX = this.getWindowStartX(canvas, image);
        final int startY = this.getWindowStartY(canvas, image);
        canvas.doDirectAccess(new NativeImageWriter(image, startX, startY));

        this.cursorRendererComponent.render(board, canvas);
    }

    private NativeImage tryCaptureImage()
    {
        final HWND windowPointer = this.getWindowPointer();
        if (windowPointer == null)
        {
            return null;
        }

        try
        {
            return Screenshoter.captureWindow(windowPointer);
        }
        catch (final IllegalArgumentException e)
        {
            return null;
        }
    }

    private HWND getWindowPointer()
    {
        return BetterUser32.INSTANCE.FindWindowA(this.windowClassName, null);
    }

    private int getWindowStartX(final IMapCanvas canvas, final NativeImage image)
    {
        return Math.max(0, (canvas.getWidth() - image.getWidth()) / 2);
    }

    private int getWindowStartY(final IMapCanvas canvas, final NativeImage image)
    {
        return Math.max(0, (canvas.getHeight() - image.getHeight()) / 2);
    }

    @Override
    public void onKeyDown(final Key key)
    {
        if (key == Key.MOUSE_LEFT)
        {
            final int mouseX = this.cursorRendererComponent.getMouseX();
            final int mouseY = this.cursorRendererComponent.getMouseY();
            WindowsInputEmulator.sendWindowClick(this.getWindowPointer(), false, mouseX, mouseY);
        }
        else
        {
            this.cursorRendererComponent.onKeyDown(key);
        }
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
