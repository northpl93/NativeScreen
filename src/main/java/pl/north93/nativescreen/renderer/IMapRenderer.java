package pl.north93.nativescreen.renderer;

public interface IMapRenderer
{
    void render(IBoard board, IMapCanvas canvas) throws Exception;

    default void cleanup() throws Exception
    {
    }
}
