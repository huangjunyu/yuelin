package com.zxing.encoding;

import java.util.*;
import android.graphics.*;
import com.google.zxing.common.*;
import com.google.zxing.*;

public final class EncodingHandler
{
    private static final int BLACK = -16777216;
    private static final int WHITE = -1;
    
    public static Bitmap createQRCode(final String s, final int n) throws WriterException {
        new Hashtable<EncodeHintType, String>().put(EncodeHintType.CHARACTER_SET, "utf-8");
        final BitMatrix encode = new MultiFormatWriter().encode(s, BarcodeFormat.QR_CODE, n, n);
        final int width = encode.getWidth();
        final int height = encode.getHeight();
        final int[] array = new int[width * height];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                if (encode.get(j, i)) {
                    array[j + i * width] = BLACK;
                }
                else {
                    array[j + i * width] = WHITE;
                }
            }
        }
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(array, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
