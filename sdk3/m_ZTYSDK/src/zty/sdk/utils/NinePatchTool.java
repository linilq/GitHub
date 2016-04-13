package zty.sdk.utils;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class NinePatchTool {

    private final static int NO_COLOR = 0x00000001;

    public static Drawable decodeDrawableFromStream(InputStream is) throws Exception {
        Bitmap bm = decodeFromStream(is);
        if (null == bm.getNinePatchChunk()) {
            return new BitmapDrawable(bm);
        } else {
            Rect padding = new Rect();
            readPaddingFromChunk(bm.getNinePatchChunk(), padding);
            return new NinePatchDrawable(bm, bm.getNinePatchChunk(), padding, null);
        }
    }

    public static Bitmap decodeFromStream(InputStream in) throws Exception {
        Bitmap srcBm = BitmapFactory.decodeStream(in);
        byte[] chunk = readChunk(srcBm);
        boolean isNinePatchChunk = NinePatch.isNinePatchChunk(chunk);
        if (isNinePatchChunk) {
            Bitmap tgtBm = Bitmap.createBitmap(srcBm, 1, 1,
                    srcBm.getWidth() - 2, srcBm.getHeight() - 2);
            srcBm.recycle();
            Field f = tgtBm.getClass().getDeclaredField("mNinePatchChunk");
            f.setAccessible(true);
            f.set(tgtBm, chunk);
            return tgtBm;
        } else {
            return srcBm;
        }
    }

    public static void readPaddingFromChunk(byte[] chunk, Rect paddingRect) {
        paddingRect.left = getInt(chunk, 12);
        paddingRect.right = getInt(chunk, 16);
        paddingRect.top = getInt(chunk, 20);
        paddingRect.bottom = getInt(chunk, 24);
    }

    public static byte[] readChunk(Bitmap yuantuBmp) throws IOException {

        final int BM_W = yuantuBmp.getWidth();
        final int BM_H = yuantuBmp.getHeight();

        int xPointCount = 0;
        int yPointCount = 0;

        int xBlockCount;
        int yBlockCount;

        ByteArrayOutputStream ooo = new ByteArrayOutputStream();
        for (int i = 0; i < 32; i++) {
            ooo.write(0);
        }

        { // x
            int[] pixelsTop = new int[BM_W - 2];
            yuantuBmp.getPixels(pixelsTop, 0, BM_W, 1, 0, BM_W - 2, 1);
            boolean topFirstPixelIsBlack = pixelsTop[0] == Color.BLACK;
            boolean topLastPixelIsBlack = pixelsTop[pixelsTop.length - 1] == Color.BLACK;
            int tmpLastColor = Color.TRANSPARENT;
            for (int i = 0, len = pixelsTop.length; i < len; i++) {
                if (tmpLastColor != pixelsTop[i]) {
                    xPointCount++;
                    writeInt(ooo, i);
                    tmpLastColor = pixelsTop[i];
                }
            }
            if (topLastPixelIsBlack) {
                xPointCount++;
                writeInt(ooo, pixelsTop.length);
            }
            xBlockCount = xPointCount + 1;
            if (topFirstPixelIsBlack) {
                xBlockCount--;
            }
            if (topLastPixelIsBlack) {
                xBlockCount--;
            }
        }

        { // y
            int[] pixelsLeft = new int[BM_H - 2];
            yuantuBmp.getPixels(pixelsLeft, 0, 1, 0, 1, 1, BM_H - 2);
            boolean firstPixelIsBlack = pixelsLeft[0] == Color.BLACK;
            boolean lastPixelIsBlack = pixelsLeft[pixelsLeft.length - 1] == Color.BLACK;
            int tmpLastColor = Color.TRANSPARENT;
            for (int i = 0, len = pixelsLeft.length; i < len; i++) {
                if (tmpLastColor != pixelsLeft[i]) {
                    yPointCount++;
                    writeInt(ooo, i);
                    tmpLastColor = pixelsLeft[i];
                }
            }
            if (lastPixelIsBlack) {
                yPointCount++;
                writeInt(ooo, pixelsLeft.length);
            }
            yBlockCount = yPointCount + 1;
            if (firstPixelIsBlack) {
                yBlockCount--;
            }
            if (lastPixelIsBlack) {
                yBlockCount--;
            }
        }

        {// color
            for (int i = 0; i < xBlockCount * yBlockCount; i++) {
                writeInt(ooo, NO_COLOR);
            }
        }

        byte[] data = ooo.toByteArray();
        data[0] = 1;
        data[1] = (byte) xPointCount;
        data[2] = (byte) yPointCount;
        data[3] = (byte) (xBlockCount * yBlockCount);
        dealPaddingInfo(yuantuBmp, data);
        return data;
    }

    private static void dealPaddingInfo(Bitmap bm, byte[] data) {
        { // padding left & padding right
            int[] bottomPixels = new int[bm.getWidth() - 2];
            bm.getPixels(bottomPixels, 0, bottomPixels.length, 1,
                    bm.getHeight() - 1, bottomPixels.length, 1);
            for (int i = 0; i < bottomPixels.length; i++) {
                if (Color.BLACK == bottomPixels[i]) { // padding left
                    writeInt(data, 12, i);
                    break;
                }
            }
            for (int i = bottomPixels.length - 1; i >= 0; i--) {
                if (Color.BLACK == bottomPixels[i]) { // padding right
                    writeInt(data, 16, bottomPixels.length - i - 2);
                    break;
                }
            }
        }
        { // padding top & padding bottom
            int[] rightPixels = new int[bm.getHeight() - 2];
            bm.getPixels(rightPixels, 0, 1, bm.getWidth() - 1, 0, 1,
                    rightPixels.length);
            for (int i = 0; i < rightPixels.length; i++) {
                if (Color.BLACK == rightPixels[i]) { // padding top
                    writeInt(data, 20, i);
                    break;
                }
            }
            for (int i = rightPixels.length - 1; i >= 0; i--) {
                if (Color.BLACK == rightPixels[i]) { // padding bottom
                    writeInt(data, 24, rightPixels.length - i - 2);
                    break;
                }
            }
        }
    }

    private static void writeInt(OutputStream out, int v) throws IOException {
        out.write((v) & 0xFF);
        out.write((v >> 8) & 0xFF);
        out.write((v >> 16) & 0xFF);
        out.write((v >> 24) & 0xFF);
    }

    private static void writeInt(byte[] b, int offset, int v) {
        b[offset] = (byte) (v);
        b[offset + 1] = (byte) (v >> 8);
        b[offset + 2] = (byte) (v >> 16);
        b[offset + 3] = (byte) (v >> 24);
    }

    private static int getInt(byte[] bs, int from) {
        int b1 = bs[from];
        int b2 = bs[from + 1];
        int b3 = bs[from + 2];
        int b4 = bs[from + 3];
        return b1 | (b2 << 8) | (b3 << 16) | b4 << 24;
    }

}
