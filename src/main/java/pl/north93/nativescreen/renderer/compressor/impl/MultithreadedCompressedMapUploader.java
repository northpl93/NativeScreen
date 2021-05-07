package pl.north93.nativescreen.renderer.compressor.impl;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import org.bukkit.entity.Player;

import lombok.ToString;
import pl.north93.nativescreen.renderer.IMap;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.IMapUploader;
import pl.north93.nativescreen.renderer.broadcaster.IPacketBroadcaster;
import pl.north93.nativescreen.renderer.compressor.packet.CompressedMapPacket;

/**
 * Implementation of {@link IMapUploader} which bypasses Minecraft's compression system
 * and compresses packets in multithreaded way.
 *
 * Use it only when compression is enabled in server config, otherwise
 * you will cause protocol error in client.
 */
@ToString
public class MultithreadedCompressedMapUploader implements IMapUploader
{
    private final IPacketBroadcaster packetBroadcaster;
    private final ExecutorService compressionExecutor;

    public MultithreadedCompressedMapUploader(final IPacketBroadcaster packetBroadcaster, final ExecutorService compressionExecutor)
    {
        this.packetBroadcaster = packetBroadcaster;
        this.compressionExecutor = compressionExecutor;
    }

    @Override
    public void uploadMapToAudience(final Collection<Player> audience, final IMap map, final IMapCanvasDirectAccess newCanvas)
    {
        final CompressedMapPacket compressedMapPacket = new CompressedMapPacket(map.getMapId(), newCanvas);

        this.compressionExecutor.submit(new PacketCompressTask(this.packetBroadcaster, audience, compressedMapPacket));
    }
}
