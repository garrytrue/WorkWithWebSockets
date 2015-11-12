package com.garrytrue.workwithwebsocket.a.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.events.EventImageReciered;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.tasks.AddToGalleryTask;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public class FragmentServerMode extends BaseClientServerFragment {
    private Button mBtnSaveImage;
    protected Uri mImageUri;
    private static final String TAG = "FragmentServerMode";

    public static FragmentServerMode newInstance() {
        Bundle args = new Bundle();
        FragmentServerMode fragment = new FragmentServerMode();
        fragment.setArguments(args);
        return fragment;
    }
    View.OnClickListener mBtnSaveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mImageUri != null)
            new AddToGalleryTask(getActivity()).execute(mImageUri);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_mode, container, false);
    }
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString
                (getString(R.string.bundle_key_store_image_uri_lifecycle)))) {
            mImageUri = Uri.parse(savedInstanceState.getString
                    (getString(R.string.bundle_key_store_image_uri_lifecycle)));
            Log.d(TAG, "onViewCreated: restore URI " + mImageUri);
        }
        initUI(v);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: URI " + mImageUri);
        if (mImageUri != null)
            outState.putString(getString(R.string.bundle_key_store_image_uri_lifecycle), mImageUri.toString());
        super.onSaveInstanceState(outState);
    }


    protected void initUI(View v) {
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mBtnSaveImage = (Button) v.findViewById(R.id.btn_save_image);
        mBtnSaveImage.setOnClickListener(mBtnSaveClickListener);
        mImageProgress = (ProgressBar) v.findViewById(R.id.pb_image_progress);
        TextView tvServerAddress = (TextView) v.findViewById(R.id.tvServerAdr);
        tvServerAddress.setText(String.format(getString(R.string.server_address_w_format), new PreferencesManager
                (getActivity()).getServerAddress()));
        showImageProgress();
    }

    protected void onReciveImageEvent(EventImageReciered ev) {
        mImageUri = ev.getImageUri();
        loadImageFromUri(mImageUri);
        mBtnSaveImage.setVisibility(View.VISIBLE);
    }
}
