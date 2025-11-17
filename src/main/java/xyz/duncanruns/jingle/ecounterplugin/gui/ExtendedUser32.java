package xyz.duncanruns.jingle.ecounterplugin.gui;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Extended User32 interface to include methods not in the standard JNA User32
 */
public interface ExtendedUser32 extends StdCallLibrary {
    ExtendedUser32 INSTANCE = Native.load("user32", ExtendedUser32.class);

    /**
     * Converts the client-area coordinates of a specified point to screen coordinates.
     * @param hWnd Handle to the window whose client area is used for conversion
     * @param lpPoint Pointer to a POINT structure containing client coordinates to convert
     * @return If the function succeeds, the return value is nonzero
     */
    boolean ClientToScreen(HWND hWnd, POINT lpPoint);
}
