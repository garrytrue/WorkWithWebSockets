package com.garrytrue.workwithwebsocket.a.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.interfaces.IBtnClickListener;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.utils.Utils;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 08.11.15.
 */
public class FragmentSelectWorkMode extends Fragment {
    private Button mBtnStartServer, mBtnStartClient;
    private EditText mServerAddress;
    private String mAddress;
    private PreferencesManager preferencesManager;
    private static final String TAG = "FragmentSelectWorkMode";
    private IBtnClickListener mClickListener;

    public static FragmentSelectWorkMode newInstance() {

        Bundle args = new Bundle();

        FragmentSelectWorkMode fragment = new FragmentSelectWorkMode();
        fragment.setArguments(args);
        return fragment;
    }

    private View.OnClickListener mRunClientModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           if(validateInput()){
             mClickListener.onClick(v.getId());
           }

        }
    };
    private View.OnClickListener mRunServerModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            validateInput();
            mClickListener.onClick(v.getId());

        }
    };
    @Override
    public void onAttach(Activity activity){
        if(activity instanceof IBtnClickListener){
            mClickListener = (IBtnClickListener)activity;
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_mode, container, false);

    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        preferencesManager = new PreferencesManager(getActivity());
        initUI(v);
        showLastInput();
    }

    private void initUI(View v) {
        mServerAddress = (EditText) v.findViewById(R.id.et_server_address);
        mBtnStartServer = (Button) v.findViewById(R.id.btn_start_server);
        mBtnStartClient = (Button) v.findViewById(R.id.btn_start_client);
        mBtnStartClient.setOnClickListener(mRunClientModeClickListener);
        mBtnStartServer.setOnClickListener(mRunServerModeClickListener);
    }

    private boolean validateInput() {
        mAddress = mServerAddress.getText().toString();
        if (Utils.isAddressValid(mAddress)) {
            Log.d(TAG, "validateInput: Valid input");
            preferencesManager.putServerAddress(mAddress);
        } else {
            Log.d(TAG, "validateInput: Invalid input");
            mServerAddress.setError(getString(R.string.error_input_address));
        }
        return Utils.isAddressValid(mAddress);
    }

    private void showLastInput() {
        if (Utils.isAddressValid(preferencesManager.getServerAddress()))
            mServerAddress.setText(preferencesManager.getServerAddress());
    }
}
