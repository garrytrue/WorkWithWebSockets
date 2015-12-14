package com.garrytrue.workwithwebsocket.fragments;

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
import com.garrytrue.workwithwebsocket.interfaces.IBtnClickListener;
import com.garrytrue.workwithwebsocket.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.utils.Utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;


public class FragmentSelectWorkMode extends Fragment {

    private static final String TAG = FragmentSelectWorkMode.class.getSimpleName();

    private EditText mServerAddress;
    private PreferencesManager mPreferencesManager;
    private IBtnClickListener mClickListener;


    public static FragmentSelectWorkMode newInstance() {
        return new FragmentSelectWorkMode();
    }

    private final  View.OnClickListener mRunClientModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateInput()) {
                Utils.hideKeyboard(getActivity(), mServerAddress.getWindowToken());
                mClickListener.onClick(v.getId());
            }

        }
    };
    private  final View.OnClickListener mRunServerModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateInput()) {
                Utils.hideKeyboard(getActivity(), mServerAddress.getWindowToken());
                mClickListener.onClick(v.getId());
            }
        }
    };

    @SuppressWarnings("deprecation")
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
        mPreferencesManager = new PreferencesManager(getActivity());
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
            mPreferencesManager.putServerAddress(address);
            return true;
        } else {
            Log.d(TAG, "validateInput: Invalid input");
            mServerAddress.setError(getString(R.string.error_input_address));
            return false;
        }
    }

    private void showLastInput() {
        mServerAddress.setText(mPreferencesManager.getServerAddress());
    }

    private String wifiIpAddress() {
        int ipAddress = ((WifiManager) getActivity().getSystemService(Activity
                .WIFI_SERVICE)).getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endian if needed
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
