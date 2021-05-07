package pl.north93.nativescreen.renderer.compressor.packet;

import net.minecraft.server.v1_12_R1.PacketDataSerializer;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.compressor.ICompressablePacket;
import pl.north93.nativescreen.renderer.impl.StandardMapUploader;

@ToString
@AllArgsConstructor
public class CompressedMapPacket implements ICompressablePacket
{
    private final int mapId;
    private final IMapCanvasDirectAccess mapCanvas;

    @Override
    public int predictBufferSize()
    {
        return StandardMapUploader.PACKET_STATIC_SIZE;
    }

    @Override
    public void writeData(final ByteBuf buffer)
    {
        StandardMapUploader.writeMapPacketData(new PacketDataSerializer(buffer), this.mapId, this.mapCanvas);
    }
}
