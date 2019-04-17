package pl.north93.nativescreen.input.helper;

import java.util.function.Consumer;

import lombok.ToString;
import pl.north93.nativescreen.input.Key;
import pl.north93.nativescreen.input.NavigationOutputHandler;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapRenderer;

/**
 * It's implementation of {@link NavigationOutputHandler} which delegates
 * all events to renderer of specified board.
 *
 * If board's renderer doesn't implement {@link NavigationOutputHandler},
 * then nothing happens.
 */
@ToString
public class NavigationOutputHandlerRendererRedirect implements NavigationOutputHandler
{
    private final IBoard board;

    public NavigationOutputHandlerRendererRedirect(final IBoard board)
    {
        this.board = board;
    }

    @Override
    public void onKeyDown(final Key key)
    {
        this.passExecution(handler -> handler.onKeyDown(key));
    }

    @Override
    public void onKeyUp(final Key key)
    {
        this.passExecution(handler -> handler.onKeyUp(key));
    }

    @Override
    public void onMouseMove(final float deltaX, final float deltaY)
    {
        this.passExecution(handler -> handler.onMouseMove(deltaX, deltaY));
    }

    private void passExecution(final Consumer<NavigationOutputHandler> handler)
    {
        final IMapRenderer renderer = this.board.getRenderer();
        if (renderer instanceof NavigationOutputHandler)
        {
            final NavigationOutputHandler rendererHandler = (NavigationOutputHandler) renderer;
            handler.accept(rendererHandler);
        }
    }
}
