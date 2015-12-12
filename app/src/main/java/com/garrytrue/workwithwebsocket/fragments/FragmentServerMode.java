package com.garrytrue.workwithwebsocket.fragments;

import android.content.Intent;
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
import com.garrytrue.workwithwebsocket.events.EventImageReceived;
import com.garrytrue.workwithwebsocket.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.services.ServerService;
import com.garrytrue.workwithwebsocket.tasks.AddToGalleryTask;
import com.garrytrue.workwithwebsocket.utils.Constants;


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
            if (mImageUri != null)
                new AddToGalleryTask(getActivity()).execute(mImageUri);
        }
    };
    View.OnClickListener mBtnStopServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent stopIntent = new Intent(getActivity(), ServerService.class);
            getActivity().stopService(stopIntent);
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
                (Constants.BUNDLE_KEY_TEMP_IMAGE_URI))) {
            mImageUri = Uri.parse(savedInstanceState.getString
                    (Constants.BUNDLE_KEY_TEMP_IMAGE_URI));
            Log.d(TAG, "onViewCreated: restore URI " + mImageUri);
        }
        initUI(v);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: URI " + mImageUri);
        if (mImageUri != null)
            outState.putString(Constants.BUNDLE_KEY_TEMP_IMAGE_URI, mImageUri.toString());
        super.onSaveInstanceState(outState);
    }


    protected void initUI(View v) {
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mBtnSaveImage = (Button) v.findViewById(R.id.btn_save_image);
        mBtnSaveImage.setOnClickListener(mBtnSaveClickListener);
        Button btnStopService = (Button) v.findViewById(R.id.btn_stop_service);
        btnStopService.setOnClickListener(mBtnStopServiceClickListener);
        mImageProgress = (ProgressBar) v.findViewById(R.id.pb_image_progress);
        TextView tvServerAddress = (TextView) v.findViewById(R.id.tvServerAdr);
        tvServerAddress.setText(String.format(getString(R.string.server_address_w_format), new PreferencesManager
                (getActivity()).getServerAddress()));
        loadImageFromUri(mImageUri);
        if (mImageUri == null) {
            showImageProgress();
        }else{
            mBtnSaveImage.setVisibility(View.VISIBLE);
        }

    }

    protected void onReceiveImageEvent(EventImageReceived ev) {
        mImageUri = new PreferencesManager(getActivity()).getDownLoadedImageUri();
        Log.d(TAG, "onReceiveImageEvent: " + mImageUri);
        loadImageFromUri(mImageUri);
        mBtnSaveImage.setVisibility(View.VISIBLE);
    }
    protected void onImageSavedEvent() {
        mImageUri = new PreferencesManager(getActivity()).getDownLoadedImageUri();
        Log.d(TAG, "onReceiveImageEvent: " + mImageUri);
        loadImageFromUri(mImageUri);
        mBtnSaveImage.setVisibility(View.GONE);
    }
}
