package com.garrytrue.workwithwebsocket.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

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
public class BitmapFileUtils {
    private static final int BUFFER_SIZE = 1024;
    private static final String FILE_SUFFIX = ".jpg";
    public static final int IMAGE_MAX_SIZE = 1024;
    public static final String PATH_PREFIX = "file:";
    public static final String TEMP_BMP_FILE_NAME = "temp_cropped_file" + FILE_SUFFIX;
    public static final String DOWNLOADED_BMP_FILE_NAME = "file_downloded_";
    public static final String TEMP_DOWNLOADED_FILE_NAME = "temp_downloaded_file" + FILE_SUFFIX;


    private BitmapFileUtils(){
        throw new AssertionError();
    }

    public static String generateBmpFileName() {
        return new StringBuilder().append(DOWNLOADED_BMP_FILE_NAME)
                .append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())).toString();
    }

    public static void saveToFile(File file, Bitmap bitmap) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.close();
    }

    public static void saveToFile(File file, byte[] array) throws IOException{
        FileOutputStream out = null;
            out = new FileOutputStream(file);
            out.write(array);
            out.flush();
            out.close();
    }

    public static File createImageFile() throws IOException{
        File image = File.createTempFile(generateBmpFileName(), FILE_SUFFIX, Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES));
        return image;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
      public static void deleteCachedFiles(Context c) {
        File[] fileList = c.getCacheDir().listFiles();
        for (File f : fileList) {
            f.delete();
        }

    }

}
