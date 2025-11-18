package xyz.duncanruns.jingle.ecounterplugin.gui;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * Extended GDI32 interface for hardware-accelerated screen capture.
 * Based on the implementation from Jingle-EyeSee-Plugin by DuncanRuns.
 */
public interface GDI32Extra extends com.sun.jna.platform.win32.GDI32 {
    GDI32Extra INSTANCE = Native.load("gdi32", GDI32Extra.class);

    /**
     * The StretchBlt function copies a bitmap from a source rectangle into a destination rectangle,
     * stretching or compressing the bitmap to fit the dimensions of the destination rectangle, if necessary.
     *
     * @param hdcDest Handle to the destination device context.
     * @param nXOriginDest The x-coordinate, in logical units, of the upper-left corner of the destination rectangle.
     * @param nYOriginDest The y-coordinate, in logical units, of the upper-left corner of the destination rectangle.
     * @param nWidthDest The width, in logical units, of the destination rectangle.
     * @param nHeightDest The height, in logical units, of the destination rectangle.
     * @param hdcSrc Handle to the source device context.
     * @param nXOriginSrc The x-coordinate, in logical units, of the upper-left corner of the source rectangle.
     * @param nYOriginSrc The y-coordinate, in logical units, of the upper-left corner of the source rectangle.
     * @param nWidthSrc The width, in logical units, of the source rectangle.
     * @param nHeightSrc The height, in logical units, of the source rectangle.
     * @param dwRop The raster operation to be performed (e.g., SRCCOPY).
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean StretchBlt(HDC hdcDest, int nXOriginDest, int nYOriginDest, int nWidthDest, int nHeightDest,
                       HDC hdcSrc, int nXOriginSrc, int nYOriginSrc, int nWidthSrc, int nHeightSrc, DWORD dwRop);

    /**
     * The SetStretchBltMode function sets the bitmap stretching mode in the specified device context.
     *
     * @param hdc Handle to the device context.
     * @param iStretchMode The stretching mode. Can be:
     *                     BLACKONWHITE (1) - AND
     *                     WHITEONBLACK (2) - OR
     *                     COLORONCOLOR (3) - Deletes pixels (faster, lower quality)
     *                     HALFTONE (4) - Maps pixels to nearest color (slower, higher quality)
     * @return If the function succeeds, the return value is the previous stretching mode.
     */
    int SetStretchBltMode(HDC hdc, int iStretchMode);

    /**
     * The CreateCompatibleDC function creates a memory device context (DC) compatible with the specified device.
     *
     * @param hdc Handle to an existing DC. If this handle is NULL, the function creates a memory DC compatible with the application's current screen.
     * @return If the function succeeds, the return value is the handle to a memory DC.
     */
    HDC CreateCompatibleDC(HDC hdc);

    /**
     * The DeleteDC function deletes the specified device context (DC).
     *
     * @param hdc A handle to the device context.
     * @return If the function succeeds, the return value is nonzero.
     */
    boolean DeleteDC(HDC hdc);

    // Raster operation codes
    int SRCCOPY = 0x00CC0020; // Copies the source rectangle directly to the destination rectangle
    int SRCPAINT = 0x00EE0086; // Combines the colors of the source and destination using OR
    int SRCAND = 0x008800C6; // Combines the colors of the source and destination using AND

    // Stretch modes
    int BLACKONWHITE = 1;
    int WHITEONBLACK = 2;
    int COLORONCOLOR = 3; // Fastest, deletes pixels
    int HALFTONE = 4; // Best quality, maps pixels
}
