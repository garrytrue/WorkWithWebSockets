package com.garrytrue.workwithwebsocket.a.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.interfaces.IBtnClickListener;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.utils.Utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 08.11.15.
 */
public class FragmentSelectWorkMode extends Fragment {
    private EditText mServerAddress;
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
            if (validateInput()) {
                Utils.hideKeyboard(getActivity(), mServerAddress.getWindowToken());
                mClickListener.onClick(v.getId());
            }

        }
    };
    private View.OnClickListener mRunServerModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateInput()) {
                Utils.hideKeyboard(getActivity(), mServerAddress.getWindowToken());
                mClickListener.onClick(v.getId());
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof IBtnClickListener) {
            mClickListener = (IBtnClickListener) activity;
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
        Button btnStartServer = (Button) v.findViewById(R.id.btn_start_server);
        Button btnStartClient = (Button) v.findViewById(R.id.btn_start_client);
        btnStartClient.setOnClickListener(mRunClientModeClickListener);
        btnStartServer.setOnClickListener(mRunServerModeClickListener);
        TextView tvIp = (TextView) v.findViewById(R.id.tv_ip);
        tvIp.setText(String.format(getString(R.string.preferred_ip), wifiIpAddress()));
    }

    private boolean validateInput() {
        String address = mServerAddress.getText().toString();
        if (Utils.isAddressValid(address)) {
            Log.d(TAG, "validateInput: Valid input");
            preferencesManager.putServerAddress(address);
        } else {
            Log.d(TAG, "validateInput: Invalid input");
            mServerAddress.setError(getString(R.string.error_input_address));
        }
        return Utils.isAddressValid(address);
    }

    private void showLastInput() {
        mServerAddress.setText(preferencesManager.getServerAddress());
    }

    private String wifiIpAddress() {
        int ipAddress = ((WifiManager) getActivity().getSystemService(Activity
                .WIFI_SERVICE)).getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e(TAG, "wifiIpAddress: ", ex);
            ipAddressString = null;
        }

        return ipAddressString;
    }
}
