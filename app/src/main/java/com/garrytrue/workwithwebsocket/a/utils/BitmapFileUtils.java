package com.garrytrue.workwithwebsocket.a.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public final class BitmapFileUtils {
    private static final String TAG = "BitmapFileUtils";
    public static final int IMAGE_MAX_SIZE = 1024;

    public static final String FILE_SUFFIX = ".jpg";
    public static final String PATH_PREFIX = "file:";
    public static final String TEMP_BMP_FILE_NAME = "temp_cropped_file" + FILE_SUFFIX;
    public static final String DOWNLOADED_BMP_FILE_NAME = "file_downloded_";
    public static final String TEMP_DOWNLOADED_FILE_NAME = "temp_downloaded_file" + FILE_SUFFIX;

    public static String getBmpFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append(DOWNLOADED_BMP_FILE_NAME)
                .append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        return sb.toString();
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

    public static File createImageFile() {
        File image = null;
        try {
            image = File.createTempFile(getBmpFileName(), FILE_SUFFIX, Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES));
        } catch (IOException ex) {
            Log.e(TAG, "createImageFile: ", ex);
        }
        return image;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

}
