package pl.north93.nativescreen.winapi;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;

public class WindowsInputEmulator
{
    public static void sendWindowClickByClassName(final String className, final boolean right, final int x, final int y)
    {
        final HWND hwnd = BetterUser32.INSTANCE.FindWindowA(className, null);
        sendWindowClick(hwnd, right, x, y);
    }

    public static void sendWindowClick(final HWND windowPointer, final boolean right, final int x, final int y)
    {
        final LPARAM clickLocation = new LPARAM(makeLong(x, y));
        BetterUser32.INSTANCE.PostMessageA(windowPointer, BetterUser32.WM_LBUTTONDOWN, new WinDef.WPARAM(0), clickLocation);
    }

    private static long makeLong(final int a, final int b)
    {
        return a | (b << 16);
    }
}
