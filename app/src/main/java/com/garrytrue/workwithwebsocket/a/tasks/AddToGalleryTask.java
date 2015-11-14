package com.garrytrue.workwithwebsocket.a.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.garrytrue.workwithwebsocket.a.events.EventImageSaved;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.utils.BitmapFileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import de.greenrobot.event.EventBus;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 12.11.15.
 */
public class AddToGalleryTask extends AsyncTask<Uri, Void, Void> {
    private static final String TAG = "AddToGalleryTask";
    private WeakReference<Context> mContextRef;

    public AddToGalleryTask(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Uri... params) {
        File imageFile = null;
        try {
            imageFile = BitmapFileUtils.createImageFile();
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
            cancel(true);
            return null;
        }
        if (!isCancelled()) {
            FileOutputStream fileOutputStream;
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
            Log.d(TAG, "doInBackground: " + imageFile.getPath());
            if (mContextRef != null && imageFile != null) {
                galleryAddPic(mContextRef.get(), BitmapFileUtils.PATH_PREFIX + imageFile.getPath());
                BitmapFileUtils.deleteCachedFiles(mContextRef.get());
                new PreferencesManager(mContextRef.get()).putDownloadedImageUri(Uri.parse((BitmapFileUtils
                        .PATH_PREFIX + imageFile.getPath())));
                EventBus.getDefault().post(new EventImageSaved());
            }
        }
        return null;
    }


    private void galleryAddPic(Context c, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(path));
        Log.d(TAG, "galleryAddPic: " + contentUri);
        mediaScanIntent.setData(contentUri);
        c.sendBroadcast(mediaScanIntent);
    }

}
