package pl.north93.nativescreen.renderer.broadcaster.impl;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
/*default*/ final class RawPacket
{
    private final ByteBuf rawData;
}
