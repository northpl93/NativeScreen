package pl.north93.nativescreen.renderer.compressor;

import io.netty.buffer.ByteBuf;

public interface ICompressablePacket
{
    /**
     * @return maximal size of buffer which is used to write data of packet.
     */
    int predictBufferSize();

    void writeData(ByteBuf buffer);
}
