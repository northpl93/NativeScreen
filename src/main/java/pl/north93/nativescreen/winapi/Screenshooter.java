package pl.north93.nativescreen.winapi;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinNT;

public class Screenshooter
{
    public static NativeImage captureWindow(final HWND hWnd)
    {
        HDC hdcWindow = BetterUser32.INSTANCE.GetDC(hWnd);
        HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        WinDef.RECT bounds = new WinDef.RECT();
        User32.INSTANCE.GetClientRect(hWnd, bounds);

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        WinNT.HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, GDI32.SRCCOPY);
        //BetterUser32.INSTANCE.PrintWindow(hWnd, hdcMemDC, 0);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        //BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

        final int[] colors = buffer.getIntArray(0, width * height);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        BetterUser32.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return new NativeImage(width, height, colors);
    }

    public static NativeImage captureWindowByClass(final String className)
    {
        final HWND hwnd = BetterUser32.INSTANCE.FindWindowA(className, null);
        if (hwnd == null)
        {
            return null;
        }

        try
        {
            return captureWindow(hwnd);
        }
        catch (final IllegalArgumentException e)
        {
            return null;
        }
    }

    public static void main(final String... args) throws Exception
    {
        // W2ViewportClass
        final HWND hwnd = BetterUser32.INSTANCE.FindWindowA("W2ViewportClass", null);
        System.out.println(hwnd);

        //final BufferedImage captureWindow = captureWindow(hwnd);
        //File outputfile = new File("C:\\Users\\Michał\\Desktop\\image.jpg");
        //ImageIO.write(captureWindow, "jpg", outputfile);

//        final WinDef.RECT windowRectangle = new WinDef.RECT();
//        BetterUser32.INSTANCE.GetClientRect(hwnd, windowRectangle);
//
//        final HDC hdcScreen = BetterUser32.INSTANCE.GetDC(null);
//        final HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(hdcScreen);
//
//        final int cx = windowRectangle.right - windowRectangle.left;
//        final int cy = windowRectangle.bottom - windowRectangle.top;
//        final HBITMAP hbitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcScreen, cx, cy);
//
//        GDI32.INSTANCE.SelectObject(hdc, hbitmap);
//
//        BetterUser32.INSTANCE.PrintWindow(hwnd, hdc, 0);


    }
}
