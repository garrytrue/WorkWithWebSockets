package com.garrytrue.workwithwebsocket.a.fragments;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionClosed;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionError;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionOpen;
import com.garrytrue.workwithwebsocket.a.events.EventProblemParsURI;
import com.garrytrue.workwithwebsocket.a.interfaces.OnTaskCompliteListener;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.services.ClientService;
import com.garrytrue.workwithwebsocket.a.tasks.ProcessBitmapTask;
import com.garrytrue.workwithwebsocket.a.utils.Constants;
import com.garrytrue.workwithwebsocket.a.utils.Utils;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 08.11.15.
 */
public class FragmentClientMode extends Fragment {
    private ImageView mImageView;
    private ProgressBar mImageProgress;
    public static final int SELECT_IMAGE_FROM_GALLERY = 9;
    private static final String TAG = "FragmentClientMode";
    private Uri mImageUri;
    private EditText mEditTextPass;

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
            Utils.hideKeyboard(getActivity(), mEditTextPass.getWindowToken());
            Intent request = new Intent(getActivity(), ClientService.class);
            Bundle bundle = new Bundle();
//            if (isPasswordValid()) {
//                bundle.putString(getString(R.string.bundle_key_msg_pass), mEditTextPass.getText().toString());
//            } else {
//                Utils.showToast(getActivity(), getString(R.string.error_input_pass));
//                return;
//            }
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
            loadImageFromUri(uri);
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

    private void initUI(View v) {
        mImageView = (ImageView) v.findViewById(R.id.imageView);
       Button btnSelectImage = (Button) v.findViewById(R.id.btn_select_image);
        btnSelectImage.setOnClickListener(mSelectImageClickListener);
        Button btnSendImage = (Button) v.findViewById(R.id.btn_send_image);
        btnSendImage.setOnClickListener(mSendImageClickListener);
        mImageProgress = (ProgressBar) v.findViewById(R.id.pb_image_progress);
        mEditTextPass = (EditText) v.findViewById(R.id.editText);
        TextView tvServerAddress = (TextView) v.findViewById(R.id.tvServerAdr);
        tvServerAddress.setText(String.format(getString(R.string.server_address_w_format),new PreferencesManager
                (getActivity()).getServerAddress()));
        loadImageFromUri(mImageUri);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: URI " + mImageUri);
        if (mImageUri != null)
            outState.putString(getString(R.string.bundle_key_store_image_uri_lifecycle), mImageUri.toString());
        super.onSaveInstanceState(outState);
    }


    private boolean isPasswordValid() {
        String pass = mEditTextPass.getText().toString();
        return !TextUtils.isEmpty(pass);
    }

    private void showImageProgress() {
        if (mImageProgress.getVisibility() == View.GONE) {
            mImageProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideImageProgress() {
        if (mImageProgress.getVisibility() == View.VISIBLE) {
            mImageProgress.setVisibility(View.GONE);
        }
    }

    private void loadImageFromUri(Uri uri) {
        if (uri != null) {
            Picasso.with(getActivity()).invalidate(uri);
        }
        Picasso.with(getActivity()).load(uri).placeholder(R.mipmap.empty_src).into(mImageView);
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



}
