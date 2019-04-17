package pl.north93.nativescreen.winapi;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

public interface BetterUser32 extends User32
{
    BetterUser32 INSTANCE = Native.loadLibrary("user32", BetterUser32.class);

    // https://docs.microsoft.com/en-us/windows/desktop/inputdev/wm-lbuttondown
    int WM_LBUTTONDOWN = 0x0201;

    HWND FindWindowA(String lpClassName, String lpWindowName);

    void PostMessageA(HWND windowPointer, int messageId, WPARAM wParam, LPARAM lParam);
}
