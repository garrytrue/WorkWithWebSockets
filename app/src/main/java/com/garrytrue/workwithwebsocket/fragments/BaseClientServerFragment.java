package com.garrytrue.workwithwebsocket.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.events.EventConnectionClosed;
import com.garrytrue.workwithwebsocket.events.EventConnectionError;
import com.garrytrue.workwithwebsocket.events.EventConnectionOpen;
import com.garrytrue.workwithwebsocket.events.EventHaveProblem;
import com.garrytrue.workwithwebsocket.events.EventImageReceived;
import com.garrytrue.workwithwebsocket.events.EventImageSaved;
import com.garrytrue.workwithwebsocket.events.EventImageSent;
import com.garrytrue.workwithwebsocket.utils.Utils;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public class BaseClientServerFragment extends Fragment {
    private static final String TAG = BaseClientServerFragment.class.getSimpleName();

    protected ProgressBar mImageProgress;
    protected ImageView mImageView;


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
            throw new IllegalStateException("This object was been subscribed");
    }

    private void notListenCallbacks() {
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(EventConnectionOpen event) {
        Utils.showToast(getActivity(), getString(R.string.msg_connection_is_open));
    }

    public void onEventMainThread(EventConnectionClosed event) {
        Utils.showToast(getActivity(), event.getReason());
    }

    public void onEventMainThread(EventConnectionError event) {
        Utils.showToast(getActivity(), event.getMessage());
    }

    public void onEventMainThread(EventImageReceived event) {
        hideImageProgress();
        onReceivedImageEvent(event);
    }

    public void onEventMainThread(EventImageSent event) {
        onSentImageEvent();
    }

    public void onEventMainThread(EventImageSaved event) {
        onImageSavedEvent();
    }

    public void onEventMainThread(EventHaveProblem event) {
        Utils.showToast(getActivity(), event.getMessage());
    }

    protected void onImageSavedEvent() {
    }

    protected void onSentImageEvent() {
    }
    protected void onReceivedImageEvent(EventImageReceived ev) {
    }


    protected void showImageProgress() {
        mImageProgress.setVisibility(View.VISIBLE);
    }

    protected void hideImageProgress() {
        mImageProgress.setVisibility(View.GONE);
    }

    protected void loadImageFromUri(Uri uri) {
        Log.d(TAG, "loadImageFromUri: Uri " + uri);
        if (uri != null) {
            Picasso.with(getActivity()).invalidate(uri);
        }
        Picasso.with(getActivity()).load(uri).placeholder(R.mipmap.empty_src).into(mImageView);
    }
}
