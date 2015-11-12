package com.garrytrue.workwithwebsocket.a.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionClosed;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionError;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionOpen;
import com.garrytrue.workwithwebsocket.a.events.EventImageReciered;
import com.garrytrue.workwithwebsocket.a.events.EventProblemParsURI;
import com.garrytrue.workwithwebsocket.a.utils.Utils;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 12.11.15.
 */
public class BaseClientServerFragment extends Fragment {
    protected ProgressBar mImageProgress;
    protected ImageView mImageView;
    private static final String TAG = BaseClientServerFragment.class.getSimpleName();


    @Override
    public void onStart() {
        super.onStart();
        listenCallbacks();
    }

    @Override
    public void onStop() {
        notListenCallbacks();
        super.onStop();
    }

    private void listenCallbacks() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        } else
            throw new IllegalStateException(
                    getString(R.string.exception_eventbus));
    }

    private void notListenCallbacks() {
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(EventConnectionOpen event) {
        Utils.showToast(getActivity(), getString(R.string.msg_connection_is_open));
    }

    public void onEventMainThread(EventProblemParsURI event) {
        Utils.showToast(getActivity(), getString(R.string.msg_wrong_uri));
    }

    public void onEventMainThread(EventConnectionClosed event) {
        Utils.showToast(getActivity(), event.getReason());
    }

    public void onEventMainThread(EventConnectionError event) {
        Utils.showToast(getActivity(), event.getMessage());
    }

    public void onEventMainThread(EventImageReciered event) {
        hideImageProgress();
        onReciveImageEvent(event);
    }

    protected void showImageProgress() {
        if (mImageProgress.getVisibility() == View.GONE) {
            mImageProgress.setVisibility(View.VISIBLE);
        }
    }

    protected void hideImageProgress() {
        if (mImageProgress.getVisibility() == View.VISIBLE) {
            mImageProgress.setVisibility(View.GONE);
        }
    }

    protected void loadImageFromUri(Uri uri) {
        Log.d(TAG, "loadImageFromUri: Uri " + uri);
        if (uri != null) {
            Picasso.with(getActivity()).invalidate(uri);
        }
        Picasso.with(getActivity()).load(uri).placeholder(R.mipmap.empty_src).into(mImageView);
    }

    protected void onReciveImageEvent(EventImageReciered ev) {
    }
}
