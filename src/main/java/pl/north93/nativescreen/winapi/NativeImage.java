package pl.north93.nativescreen.winapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class NativeImage
{
    private final int width;
    private final int height;
    private final int[] data;
}
