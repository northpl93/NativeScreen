package pl.north93.nativescreen.video;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.IMapRenderer;

public class TestVideoRenderer implements IMapRenderer
{
    private final FrameGrab frameGrab;

    public TestVideoRenderer() throws Exception
    {
        final File file = new File("C:\\Users\\Michał\\Desktop\\test.mp4");

        this.frameGrab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        //this.frameGrab.seekToSecondPrecise(0);
    }

    @Override
    public void render(final IBoard board, final IMapCanvas canvas) throws Exception
    {
        final Picture picture = this.frameGrab.getNativeFrame();
        if (picture == null)
        {
            return;
        }

        ///System.out.println(picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor());

        //for JDK (jcodec-javase)
        final BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);

        canvas.putImage(0, 0, bufferedImage);
    }
}
