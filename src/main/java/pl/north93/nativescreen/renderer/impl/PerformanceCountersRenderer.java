package pl.north93.nativescreen.renderer.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.widget.debug.RenderTimeDebugWidget;
import pl.north93.nativescreen.widget.debug.UploadedMapsDebugWidget;

@ToString
@RequiredArgsConstructor
final class PerformanceCountersRenderer
{
    private final RenderTimeDebugWidget performanceDebugWidget = new RenderTimeDebugWidget(640, 192, 0, 0);
    private final UploadedMapsDebugWidget uploadedMapsDebugWidget = new UploadedMapsDebugWidget(640, 192, 640, 0);
    private final IBoard board;
    @Getter @Setter
    private boolean enabled;

    public void render(final IMapCanvas canvas) throws Exception
    {
        if (! this.enabled)
        {
            return;
        }

        this.performanceDebugWidget.render(this.board, canvas);
        this.uploadedMapsDebugWidget.render(this.board, canvas);
    }
}
