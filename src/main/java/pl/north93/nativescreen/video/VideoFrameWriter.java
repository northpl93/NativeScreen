package pl.north93.nativescreen.video;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacv.Frame;

import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccessor;

public class VideoFrameWriter implements IMapCanvasDirectAccessor
{
    private final Frame frame;

    public VideoFrameWriter(final Frame frame)
    {
        this.frame = frame;
    }

    @Override
    public void doDirectAccess(final IMapCanvasDirectAccess directAccess)
    {
        final UByteIndexer frameIndexer = this.frame.createIndexer();

        final long height = Math.min(frameIndexer.size(0), directAccess.getHeight());
        final long width = Math.min(frameIndexer.size(1), directAccess.getWidth());

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int channelB = frameIndexer.get(y, x, 0);
                final int channelG = frameIndexer.get(y, x, 1);
                final int channelR = frameIndexer.get(y, x, 2);

                // frameIndexer.get already does (channelX & 0xFF), so don't waste time here
                final int rgb = (channelR << 16) | (channelG << 8) | channelB;
                directAccess.setPixelUnsafeRGB(x, y, rgb);
            }
        }

        frameIndexer.close();
    }
}
