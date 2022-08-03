package pl.north93.nativescreen.renderer.impl;

import java.awt.*;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import pl.north93.nativescreen.renderer.MapColor;

@Fork(1)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 5)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
public class ColorConverterBenchmark
{
    private static final int MAX_RGB = 256 * 256 * 256;
    private static final int SCREEN_SIZE = 1920 * 1024;

    private final int[] rgbScreenContent = new int[SCREEN_SIZE];
    private final byte[] outputScreenContent = new byte[SCREEN_SIZE];

    @Setup
    public void prepareTestData()
    {
        final Random notSoRandom = new Random(3470);

        for (int i = 0; i < SCREEN_SIZE; i++)
        {
            this.rgbScreenContent[i] = notSoRandom.nextInt(MAX_RGB);
        }
    }

    @Benchmark
    public void withoutCache()
    {
        for (int i = 0; i < SCREEN_SIZE; i++)
        {
            this.outputScreenContent[i] = (byte) MapColor.find(new Color(this.rgbScreenContent[i]));
        }
    }

    @Benchmark
    public void javaHashMapBasedCache()
    {
        for (int i = 0; i < SCREEN_SIZE; i++)
        {
            this.outputScreenContent[i] = HashMapBasedCache.translateColor(this.rgbScreenContent[i]);
        }
    }

    @Benchmark
    public void fastUtilBasedCache()
    {
        for (int i = 0; i < SCREEN_SIZE; i++)
        {
            this.outputScreenContent[i] = FastUtilBasedCache.translateColor(this.rgbScreenContent[i]);
        }
    }

    @Benchmark
    public void oldArrayBasedCache()
    {
        for (int i = 0; i < SCREEN_SIZE; i++)
        {
            this.outputScreenContent[i] = OldArrayBasedCache.translateColor(this.rgbScreenContent[i]);
        }
    }

    @Benchmark
    public void arrayBasedCache()
    {
        for (int i = 0; i < SCREEN_SIZE; i++)
        {
            this.outputScreenContent[i] = ColorConverterCache.translateColor(this.rgbScreenContent[i]);
        }
    }
}
