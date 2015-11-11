package com.garrytrue.workwithwebsocket.a.events;

import android.net.Uri;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public class EventImageReciered {
    private Uri mImageUri;

    public EventImageReciered(Uri mImageUri) {
        this.mImageUri = mImageUri;
    }

    public Uri getImageUri() {
        return mImageUri;
    }
}
