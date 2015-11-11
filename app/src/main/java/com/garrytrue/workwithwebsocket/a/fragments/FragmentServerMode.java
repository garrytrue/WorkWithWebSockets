package com.garrytrue.workwithwebsocket.a.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionClosed;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionError;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionOpen;
import com.garrytrue.workwithwebsocket.a.events.EventImageReciered;
import com.garrytrue.workwithwebsocket.a.events.EventProblemParsURI;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.utils.Utils;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public class FragmentServerMode extends Fragment {
    private ImageView mImageView;
    private ProgressBar mImageProgress;
    private Button mBtnSaveImage;

    public static FragmentServerMode newInstance() {

        Bundle args = new Bundle();

        FragmentServerMode fragment = new FragmentServerMode();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_mode, container, false);
    }
    public void onViewCreated(View v, Bundle savedInstanceState) {
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString
                (getString(R.string.bundle_key_store_image_uri_lifecycle)))) {
//            mImageUri = Uri.parse(savedInstanceState.getString
//                    (getString(R.string.bundle_key_store_image_uri_lifecycle)));
//            Log.d(TAG, "onViewCreated: restore URI " + mImageUri);
        }
        initUI(v);
    }


    private void initUI(View v) {
        mImageView = (ImageView) v.findViewById(R.id.imageView);
         mBtnSaveImage = (Button) v.findViewById(R.id.btn_save_image);
//        btnSaveImage.setOnClickListener(mSendImageClickListener);
        mImageProgress = (ProgressBar) v.findViewById(R.id.pb_image_progress);
        TextView tvServerAddress = (TextView) v.findViewById(R.id.tvServerAdr);
        tvServerAddress.setText(String.format(getString(R.string.server_address_w_format),new PreferencesManager
                (getActivity()).getServerAddress()));
        showImageProgress();
    }
    private void showImageProgress() {
        if (mImageProgress.getVisibility() == View.GONE) {
            mImageProgress.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        listenCallbacks();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onStop(){
        notListenCallbacks();
        super.onStop();
    }

    private void hideImageProgress() {
        if (mImageProgress.getVisibility() == View.VISIBLE) {
            mImageProgress.setVisibility(View.GONE);
        }
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
        loadImageFromUri(event.getImageUri());
        mBtnSaveImage.setVisibility(View.VISIBLE);


    }
    private void loadImageFromUri(Uri uri) {
        if (uri != null) {
            Picasso.with(getActivity()).invalidate(uri);
        }
        Picasso.with(getActivity()).load(uri).placeholder(R.mipmap.empty_src).into(mImageView);
    }

}
