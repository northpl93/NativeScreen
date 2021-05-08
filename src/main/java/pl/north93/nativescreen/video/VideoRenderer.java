package pl.north93.nativescreen.video;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

public class VideoRenderer implements IMapRenderer
{
    private final FFmpegFrameGrabber grabber;

    public VideoRenderer(final String source) throws Exception
    {
        this.grabber = new FFmpegFrameGrabber(source);
        this.grabber.start();
    }

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        Frame frame;
        do
        {
            frame = this.grabber.grabFrame(false, true, true, false, false);
            if (frame == null)
            {
                return;
            }
        }
        while (frame.image == null);

        canvas.doDirectAccess(new VideoFrameWriter(frame));
    }

    @Override
    public void cleanup() throws Exception
    {
        this.grabber.close();
    }
}
