package pl.north93.nativescreen.video;

import java.awt.image.BufferedImage;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

public class TestVideoRenderer implements IMapRenderer
{
    private static final Java2DFrameConverter CONVERTER = new Java2DFrameConverter();
    private final FFmpegFrameGrabber grabber;
    private BufferedImage latestFrame;

    public TestVideoRenderer(final String source) throws Exception
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
            frame = this.grabber.grabFrame(false, true, true, false, true);
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
