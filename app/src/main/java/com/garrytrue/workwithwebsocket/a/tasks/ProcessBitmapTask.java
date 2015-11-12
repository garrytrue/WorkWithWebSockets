package com.garrytrue.workwithwebsocket.a.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.garrytrue.workwithwebsocket.a.interfaces.OnTaskCompliteListener;
import com.garrytrue.workwithwebsocket.a.utils.BitmapFileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class ProcessBitmapTask extends AsyncTask<Uri, Void, Uri> {
    private WeakReference<Context> mWeakContext;
    private WeakReference<OnTaskCompliteListener> mITaskCompliteListener;
    private static final String TAG = "ProcessBitmapTask";

    public ProcessBitmapTask(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    public void setTaskCompliteLiistener(OnTaskCompliteListener listener) {
        mITaskCompliteListener = new WeakReference<>(listener);
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        if (params.length > 0 && mWeakContext != null) {
            Log.d(TAG, "doInBackground: start process bitmap");
            Bitmap bmp = getBitmap(params[0], mWeakContext.get());
            if (!isCancelled() && bmp != null) {
                Log.d(TAG, "doInBackground: bitmap is " + bmp.toString());
                File file = new File(mWeakContext.get().getCacheDir(),
                        BitmapFileUtils.TEMP_BMP_FILE_NAME);
                if (file.exists()) {
                    Log.d(TAG, "doInBackground: file is exist");
                    file.delete();
                }
                BitmapFileUtils.saveToFile(bmp, new File(mWeakContext.get().getCacheDir(),
                        BitmapFileUtils.TEMP_BMP_FILE_NAME));
                bmp.recycle();
                Log.d(TAG, "doInBackground: uri " + Uri.fromFile(file));
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Uri result) {
        Log.d(TAG, "onPostExecute: " + result);
        if (mITaskCompliteListener != null) {
            mITaskCompliteListener.get().onTaskComplited(result);
        }
    }

    private Bitmap getBitmap(Uri uri, Context c) {
        InputStream in = null;
        try {
            in = c.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > BitmapFileUtils.IMAGE_MAX_SIZE || o.outWidth > BitmapFileUtils
                    .IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.round(Math.log(BitmapFileUtils.IMAGE_MAX_SIZE
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = c.getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
