package com.garrytrue.workwithwebsocket.fragments;

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
import com.garrytrue.workwithwebsocket.interfaces.OnTaskCompleteListener;
import com.garrytrue.workwithwebsocket.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.services.ClientService;
import com.garrytrue.workwithwebsocket.tasks.ProcessBitmapTask;
import com.garrytrue.workwithwebsocket.utils.Constants;
import com.garrytrue.workwithwebsocket.utils.Utils;
import static com.garrytrue.workwithwebsocket.utils.Constants.*;


public class FragmentClientMode extends BaseClientServerFragment {
    private static final int SELECT_IMAGE_FROM_GALLERY = 9;
    private static final String TAG = FragmentClientMode.class.getSimpleName();

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
            bundle.putString(Constants.BUNDLE_KEY_DEVICE_IP, new PreferencesManager
                    (getActivity()).getServerAddress());
            if (mImageUri != null) {
                bundle.putString(BUNDLE_KEY_IMAGE_URI, mImageUri.toString());
            } else {
                Utils.showToast(getActivity(), getString(R.string.error_not_image));
                return;
            }
            request.putExtras(bundle);
            getActivity().startService(request);

        }
    };

    private OnTaskCompleteListener mOnTaskCompleteListener = new OnTaskCompleteListener() {
        @Override
        public void onTaskCompleted(Uri uri) {
            Log.d(TAG, "onTaskCompleted: URI " + uri);
            mImageUri = uri;
            loadImageFromUri(mImageUri);
            hideImageProgress();
        }
    };

    public static FragmentClientMode newInstance() {
        return new FragmentClientMode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_mode, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString
                (BUNDLE_KEY_TEMP_IMAGE_URI))) {
            mImageUri = Uri.parse(savedInstanceState.getString
                    (BUNDLE_KEY_TEMP_IMAGE_URI));
            Log.d(TAG, "onViewCreated: restore URI " + mImageUri);
        }
        initUI(v);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: URI " + mImageUri);
        if (mImageUri != null)
            outState.putString(BUNDLE_KEY_TEMP_IMAGE_URI, mImageUri.toString());
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
//        Load images from stored uri, if uri is null - load default image
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
                    processBitmapTask.setTaskCompleteListener(mOnTaskCompleteListener);
                    processBitmapTask.execute(mImageUri);
            }
        }
    }
    protected void onSentImageEvent() {
        mImageView.setImageResource(R.mipmap.empty_src);
    }
}
