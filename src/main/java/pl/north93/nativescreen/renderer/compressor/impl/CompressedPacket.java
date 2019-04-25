package pl.north93.nativescreen.renderer.compressor.impl;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
/*default*/ final class CompressedPacket
{
    private final ByteBuf compressedData;
}
