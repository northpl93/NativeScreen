package pl.north93.nativescreen.renderer.impl;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import pl.north93.nativescreen.renderer.IMapCanvas;

@Fork(1)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 5)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
public class MapCanvasBenchmark
{
    private final IMapCanvas mapCanvas = MapCanvasImpl.createFromMaps(15, 8);

    @Benchmark
    public void linearArrayWrite()
    {
        for (int y = 0; y < this.mapCanvas.getHeight(); y++)
        {
            for (int x = 0; x < this.mapCanvas.getWidth(); x++)
            {
                this.mapCanvas.setPixel(x, y, (byte) 0);
            }
        }
    }

    @Benchmark
    public void nonlinearArrayWrite()
    {
        for (int x = 0; x < this.mapCanvas.getWidth(); x++)
        {
            for (int y = 0; y < this.mapCanvas.getHeight(); y++)
            {
                this.mapCanvas.setPixel(x, y, (byte) 0);
            }
        }
    }

    @Benchmark
    public void linearArrayRead(final Blackhole blackhole)
    {
        for (int y = 0; y < this.mapCanvas.getHeight(); y++)
        {
            for (int x = 0; x < this.mapCanvas.getWidth(); x++)
            {
                blackhole.consume(this.mapCanvas.getPixel(x, y));
            }
        }
    }

    @Benchmark
    public void nonlinearArrayRead(final Blackhole blackhole)
    {
        for (int x = 0; x < this.mapCanvas.getWidth(); x++)
        {
            for (int y = 0; y < this.mapCanvas.getHeight(); y++)
            {
                blackhole.consume(this.mapCanvas.getPixel(x, y));
            }
        }
    }

    @Benchmark
    public void arrayFill()
    {
        this.mapCanvas.fill((byte) 0);
    }
}
