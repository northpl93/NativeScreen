package pl.north93.nativescreen.renderer.compressor.impl;

import java.util.concurrent.ExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.channel.Channel;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.renderer.compressor.ICompressablePacket;
import pl.north93.nativescreen.renderer.compressor.IPacketCompressor;

@Log4j2
@ToString
public class PacketCompressorImpl implements IPacketCompressor
{
    private final ExecutorService executor;

    public PacketCompressorImpl(final JavaPlugin javaPlugin, final ExecutorService executor)
    {
        Bukkit.getPluginManager().registerEvents(new CompressionInjector(this), javaPlugin);
        this.executor = executor;
    }

    @Override
    public void injectChannel(final Channel channel)
    {
        log.info("Injecting packet compressor in channel {}", channel);
        channel.pipeline().addBefore("encoder", "north_compress", new CompressedPacketToByteEncoder());
    }

    @Override
    public void sendPacket(final Channel channel, final ICompressablePacket packet)
    {
        this.executor.submit(new PacketCompressTask(channel, packet));
    }
}
