package com.garrytrue.workwithwebsocket.a.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public final class BitmapUtils {
    public static final int IMAGE_MAX_SIZE = 1024;
    private static final String TAG = "BitmapUtils";
    public static final String TEMP_BMP_FILE_NAME = "temp_cropped_file.jpg";
    public static final String DOWNLOADED_BMP_FILE_NAME = "downloaded_file.jpg";

    public static int calculateSampleSize(BitmapFactory.Options options, int actualWidth, int actualHeight) {
        final int bmWidth = options.outWidth;
        final int bmHeight = options.outHeight;
        int inSampleSize = 1;

        if (bmHeight > actualHeight || bmWidth > actualWidth) {
            final int heightRatio = Math.round((float) bmHeight / (float) actualHeight);
            final int widthRatio = Math.round((float) bmWidth / (float) actualWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static void saveToFile(Bitmap bitmap, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public static void saveToFile(byte[] array, File file) {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            out.write(array);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
