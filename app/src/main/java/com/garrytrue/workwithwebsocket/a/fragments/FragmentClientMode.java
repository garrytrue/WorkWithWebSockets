package com.garrytrue.workwithwebsocket.a.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.garrytrue.workwithwebsocket.a.interfaces.OnTaskCompliteListener;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.services.ClientService;
import com.garrytrue.workwithwebsocket.a.tasks.ProcessBitmapTask;
import com.garrytrue.workwithwebsocket.a.utils.Constants;
import com.garrytrue.workwithwebsocket.a.utils.Utils;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 08.11.15.
 */
public class FragmentClientMode extends BaseClientServerFragment {
    public static final int SELECT_IMAGE_FROM_GALLERY = 9;
    private static final String TAG = "FragmentClientMode";
    private Uri mImageUri;

    private View.OnClickListener mSelectImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mImageView.setImageResource(R.mipmap.empty_src);
            showImageProgress();
            selectImageFromGallery();

        }
    };
    private View.OnClickListener mSendImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent request = new Intent(getActivity(), ClientService.class);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.bundle_key_inet_address), new PreferencesManager
                    (getActivity()).getServerAddress());
            if (mImageUri != null) {
                bundle.putString(getString(R.string.bundle_key_msg_data), mImageUri.toString());
            } else {
                Utils.showToast(getActivity(), getString(R.string.error_not_image));
                return;
            }

            request.setAction(Constants.ACTION_START_CONNECTION);
            request.putExtras(bundle);
            getActivity().startService(request);

        }
    };

    private OnTaskCompliteListener mOnTaskCompliteListener = new OnTaskCompliteListener() {
        @Override
        public void onTaskComplited(Uri uri) {
            Log.d(TAG, "onTaskComplited: URI " + uri);
            mImageUri = uri;
            loadImageFromUri(mImageUri);
            hideImageProgress();
        }
    };

    public static FragmentClientMode newInstance() {

        Bundle args = new Bundle();

        FragmentClientMode fragment = new FragmentClientMode();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_mode, container, false);
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
        Button btnSelectImage = (Button) v.findViewById(R.id.btn_select_image);
        btnSelectImage.setOnClickListener(mSelectImageClickListener);
        Button btnSendImage = (Button) v.findViewById(R.id.btn_send_image);
        btnSendImage.setOnClickListener(mSendImageClickListener);
        mImageProgress = (ProgressBar) v.findViewById(R.id.pb_image_progress);
        TextView tvServerAddress = (TextView) v.findViewById(R.id.tvServerAdr);
        tvServerAddress.setText(String.format(getString(R.string.server_address_w_format), new PreferencesManager
                (getActivity()).getServerAddress()));
        loadImageFromUri(mImageUri);
    }


    private void selectImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(pickIntent, SELECT_IMAGE_FROM_GALLERY);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case SELECT_IMAGE_FROM_GALLERY:
                    mImageUri = data.getData();
                    Log.d(TAG, "onActivityResult: Image Uri " + mImageUri);
                    ProcessBitmapTask processBitmapTask = new ProcessBitmapTask(getActivity());
                    processBitmapTask.setTaskCompliteLiistener(mOnTaskCompliteListener);
                    processBitmapTask.execute(mImageUri);
            }
        }
    }
    protected void onSendedImageEvent() {
        mImageView.setImageResource(R.mipmap.empty_src);
    }
}
