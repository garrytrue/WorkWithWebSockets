package com.garrytrue.workwithwebsocket.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.garrytrue.workwithwebsocket.interfaces.OnTaskCompleteListener;
import com.garrytrue.workwithwebsocket.utils.BitmapFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;


public class ProcessBitmapTask extends AsyncTask<Uri, Void, Uri> {
    private static final String TAG = ProcessBitmapTask.class.getSimpleName();
    private final WeakReference<Context> mWeakContext;
    private WeakReference<OnTaskCompleteListener> mITaskCompleteListenerRef;


    public ProcessBitmapTask(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    public void setTaskCompleteListener(OnTaskCompleteListener listener) {
        mITaskCompleteListenerRef = new WeakReference<>(listener);
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        final Context localContext = mWeakContext.get();
        if (localContext != null && params.length > 0) {
            Log.d(TAG, "doInBackground: start process bitmap");
                try {
                    Bitmap bmp = getBitmap(params[0], localContext.getContentResolver());
                    Log.d(TAG, "doInBackground: bitmap is " + bmp.toString());
                    File file = new File(localContext.getCacheDir(),
                            BitmapFileUtils.TEMP_BMP_FILE_NAME);
                    if (file.exists()) {
                        Log.d(TAG, "doInBackground: file is exist");
                        file.delete();
                    }
                    BitmapFileUtils.saveToFile(new File(localContext.getCacheDir(),
                            BitmapFileUtils.TEMP_BMP_FILE_NAME), bmp);
                    bmp.recycle();
                    Log.d(TAG, "doInBackground: uri " + Uri.fromFile(file));
                    return Uri.fromFile(file);
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: ", e);
                }
            }
        return null;
    }

    @Override
    protected void onPostExecute(Uri result) {
        Log.d(TAG, "onPostExecute: " + result);
        if (mITaskCompleteListenerRef != null) {
            mITaskCompleteListenerRef.get().onTaskCompleted(result);
        }
    }

    private Bitmap getBitmap(Uri uri, ContentResolver resolver) throws IOException {
        InputStream in = resolver.openInputStream(uri);

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(in, null, o);
        if(in != null) {
            in.close();
        }
        int scale = 1;
        if (o.outHeight > BitmapFileUtils.IMAGE_MAX_SIZE || o.outWidth > BitmapFileUtils
                .IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(BitmapFileUtils.IMAGE_MAX_SIZE
                    / (double) Math.max(o.outHeight, o.outWidth))
                    / Math.log(0.5)));
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        in = resolver.openInputStream(uri);
        Bitmap b = BitmapFactory.decodeStream(in, null, o2);
        if(in != null) {
            in.close();
        }
        return b;
    }
}

