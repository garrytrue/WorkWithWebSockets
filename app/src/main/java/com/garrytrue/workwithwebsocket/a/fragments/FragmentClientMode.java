package com.garrytrue.workwithwebsocket.a.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.services.ClientIntentService;
import com.garrytrue.workwithwebsocket.a.services.ClientService;
import com.garrytrue.workwithwebsocket.a.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 08.11.15.
 */
public class FragmentClientMode extends Fragment {
    private ImageView mImageView;
    private Button mBtnSelectImage, mBtnSendImage;
    private ProgressBar mImageProgress;
    public static final int SELECT_IMAGE_FROM_GALLERY = 9;
    private static final String TAG = "FragmentClientMode";
    private Uri mImageUri;
    private ServiceConnection mConnection;
    private Messenger mClientService;
    private EditText mEditTestMessage;

    private BroadcastReceiver responceReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
        }
    };

    private View.OnClickListener mSelectImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showImageProgress();
            selectImageFromGallery();

        }
    };
    private View.OnClickListener mSendImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: 09.11.15 need send command to service
            String data = mEditTestMessage.getText().toString();
            Log.d(TAG, "onClick: DATA "+ data);
//            Bundle container = new Bundle();
//            container.putString(getString(R.string.bundle_key_msg_data), data);
//            Message msg = Message.obtain(null, ClientService.COMMAND_SEND_MSG_TO_SERVICE,
//                    container);
//            msg.replyTo = mClientService;
//
//            try {
//                mClientService.send(msg);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            Intent request = new Intent(getActivity(), ClientIntentService.class);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.bundle_key_inet_address), new PreferencesManager
                    (getActivity()).getServerAddress());
            bundle.putString(getString(R.string.bundle_key_msg_data), mImageUri.toString());
            request.setAction(Constants.ACTION_START_CONNECTION);
            request.putExtras(bundle);
           getActivity().startService(request);

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
        initUI(v);
//        initServiceConnection();
//        Intent intent = new Intent(getActivity(), ClientService.class);
//        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_RECIVE_MSG_IN_CLIENT);
        filter.addAction(Constants.ACTION_RECIVE_ERR_IN_CLIENT);
        filter.addAction(Constants.ACTION_CLOSE_CONN_IN_CLIENT);
        getActivity().registerReceiver(responceReciver, filter);
    }
    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(responceReciver);
    }

    private void initServiceConnection() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected() called with: " + "name = [" + name + "], service = [" + service + "]");
                mClientService = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected() called with: " + "name = [" + name + "]");
            }
        };
    }

    private void initUI(View v) {
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mBtnSelectImage = (Button) v.findViewById(R.id.btn_select_image);
        mBtnSelectImage.setOnClickListener(mSelectImageClickListener);
        mBtnSendImage = (Button) v.findViewById(R.id.btn_send_image);
        mBtnSendImage.setOnClickListener(mSendImageClickListener);
        mImageProgress = (ProgressBar) v.findViewById(R.id.pb_image_progress);
        mEditTestMessage = (EditText) v.findViewById(R.id.editText);
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
                    Log.d(TAG, "onActivityResult: Image Uri "+ mImageUri);
                    Picasso.with(getActivity()).load(mImageUri).resize(400,
                            400)
                            .centerCrop().into
                            (mImageView);
                    hideImageProgress();
            }
        }
    }
//   public static class WebSocetClientResponceHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            int what = msg.what;
//            Log.d(TAG, "handleMessage: WHAT "+ what);
//            switch (what) {
//                case ClientService.COMMAND_RECIVE_MSG_FROM_SERVICE:
//                    String data = msg.getData().getString("bundle_key_msg_data");
//                    Log.d(TAG, "handleMessage: Get Msg from Service " + data);
//
//                    break;
//            }
//        }
//
//    }

}
