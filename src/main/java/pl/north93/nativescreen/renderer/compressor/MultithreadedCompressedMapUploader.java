package pl.north93.nativescreen.renderer.compressor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.server.v1_12_R1.EntityPlayer;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.channel.Channel;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.IMapUploader;
import pl.north93.nativescreen.renderer.compressor.impl.PacketCompressorImpl;
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
    private final IPacketCompressor packetCompressor;

    public MultithreadedCompressedMapUploader(final JavaPlugin javaPlugin)
    {
        final ExecutorService executorService = Executors.newFixedThreadPool(8);
        this.packetCompressor = new PacketCompressorImpl(javaPlugin, executorService);
    }

    @Override
    public void uploadMapToPlayer(final Player player, final int mapId, final IMapCanvasDirectAccess newCanvas)
    {
        final CompressedMapPacket compressedMapPacket = new CompressedMapPacket(mapId, newCanvas);

        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final EntityPlayer entityPlayer = craftPlayer.getHandle();

        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        this.packetCompressor.sendPacket(channel, compressedMapPacket);
    }
}
