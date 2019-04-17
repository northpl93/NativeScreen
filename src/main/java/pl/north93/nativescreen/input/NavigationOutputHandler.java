package pl.north93.nativescreen.input;

public interface NavigationOutputHandler
{
    void onKeyDown(Key key);

    void onKeyUp(Key key);

    void onMouseMove(float deltaX, float deltaY);
}
