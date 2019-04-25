package pl.north93.nativescreen.renderer.compressor.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IMapCanvas;
import pl.north93.nativescreen.renderer.compressor.ICompressablePacket;
import pl.north93.nativescreen.renderer.impl.StandardMapUploader;

@ToString
@AllArgsConstructor
public class CompressedMapPacket implements ICompressablePacket
{
    private final int mapId;
    private final IMapCanvas mapCanvas;

    @Override
    public int predictBufferSize()
    {
        return StandardMapUploader.BUFFER_SIZE;
    }

    @Override
    public void writeData(final ByteBuf buffer)
    {
        StandardMapUploader.writeMapPacket(buffer, this.mapId, this.mapCanvas);
    }
}
