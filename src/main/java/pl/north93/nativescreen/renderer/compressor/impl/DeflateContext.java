package pl.north93.nativescreen.renderer.compressor.impl;

import java.util.zip.Deflater;

import lombok.Getter;
import lombok.ToString;

/**
 * Holds thread-local context which is used to deflate packet.
 */
@Getter
@ToString
/*default*/ final class DeflateContext
{
    private static final ThreadLocal<DeflateContext> deflate = ThreadLocal.withInitial(DeflateContext::new);

    private final byte[] buffer;
    private final Deflater deflater;

    private DeflateContext()
    {
        this.buffer = new byte[8196];
        this.deflater = new Deflater(Deflater.BEST_COMPRESSION);
    }

    public static DeflateContext getContext()
    {
        return deflate.get();
    }
}
