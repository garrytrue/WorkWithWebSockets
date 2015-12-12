package com.garrytrue.workwithwebsocket.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.garrytrue.workwithwebsocket.events.EventImageSaved;
import com.garrytrue.workwithwebsocket.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.utils.BitmapFileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import de.greenrobot.event.EventBus;

public class AddToGalleryTask extends AsyncTask<Uri, Void, Void> {
    private static final String TAG = AddToGalleryTask.class.getSimpleName();

    private final WeakReference<Context> mContextRef;

    public AddToGalleryTask(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Uri... params) {
        File imageFile;
        try {
            imageFile = BitmapFileUtils.createImageFile();
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
            cancel(true);
            return null;
        }
        if (!isCancelled()) {
            InputStream inputStream = null;
            final Context localContext = mContextRef.get();
            final FileOutputStream fileOutputStream;
            if (localContext != null && imageFile != null) {
                try {
                    inputStream = localContext.getContentResolver().openInputStream(params[0]);
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
                final String imageUri = BitmapFileUtils.PATH_PREFIX + imageFile.getPath();
                galleryAddPic(localContext, imageUri);
                BitmapFileUtils.deleteCachedFiles(localContext);
                new PreferencesManager(localContext).putDownloadedImageUri(Uri.parse(imageUri));
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
