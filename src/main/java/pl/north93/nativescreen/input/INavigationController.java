package pl.north93.nativescreen.input;

public interface INavigationController
{
    void registerNavigationHandler(NavigationOutputHandler handler);

    boolean isKeyDown(Key key); // check is key pressed
}
