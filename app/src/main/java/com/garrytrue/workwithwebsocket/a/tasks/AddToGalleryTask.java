package com.garrytrue.workwithwebsocket.a.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.garrytrue.workwithwebsocket.a.utils.BitmapFileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 12.11.15.
 */
public class AddToGalleryTask extends AsyncTask<Uri, Void, Void> {

    private WeakReference<Context> mContextRef;
    private static final String TAG = "AddToGalleryTask";

    public AddToGalleryTask(Context context) {
        mContextRef = new WeakReference<Context>(context);
    }

    @Override
    protected Void doInBackground(Uri... params) {
        File imageFile = BitmapFileUtils.createImageFile();
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        if (mContextRef != null && imageFile != null)
            try {
                inputStream = mContextRef.get().getContentResolver().openInputStream(params[0]);
                fileOutputStream = new FileOutputStream(imageFile);
                BitmapFileUtils.copyStream(inputStream, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException ex) {
                Log.e(TAG, "doInBackground: ", ex);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: ", e);
                    }
                }
            }
        Log.d(TAG, "doInBackground: "+ imageFile.getPath());
        if (mContextRef != null && imageFile != null)
            galleryAddPic(mContextRef.get(), BitmapFileUtils.PATH_PREFIX + imageFile.getPath());
        return null;
    }

    private void galleryAddPic(Context c, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        Log.d(TAG, "galleryAddPic: "+contentUri);
        mediaScanIntent.setData(contentUri);
        c.sendBroadcast(mediaScanIntent);
    }

}
