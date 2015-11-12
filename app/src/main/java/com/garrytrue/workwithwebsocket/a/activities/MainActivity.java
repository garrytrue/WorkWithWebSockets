package com.garrytrue.workwithwebsocket.a.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.fragments.FragmentClientMode;
import com.garrytrue.workwithwebsocket.a.fragments.FragmentSelectWorkMode;
import com.garrytrue.workwithwebsocket.a.fragments.FragmentServerMode;
import com.garrytrue.workwithwebsocket.a.interfaces.IBtnClickListener;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.services.ServerService;
import com.garrytrue.workwithwebsocket.a.utils.Utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity implements IBtnClickListener {
    private RelativeLayout mContainer;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString
                (getString(R.string.bundle_key_current_fragment_tag)))) {
            handleSavedState(savedInstanceState.getString
                    (getString(R.string.bundle_key_current_fragment_tag)));
        } else {
            showSelectModeFragment();
        }

    }

    private void handleSavedState(String tag) {
        Fragment fr = getFragmentManager().findFragmentById(getFragmentContainerId());
        if (fr != null && !TextUtils.isEmpty(fr.getTag())) {
            String currentTag = fr.getTag();
            Log.d(TAG, "handleSavedState: CURRENT_TAG " + fr.getTag());
            fr = getFragmentManager().findFragmentByTag(currentTag);
            Log.d(TAG, "handleSavedState: NEW_TAG " + fr.getTag());
            if (fr.isAdded()) {
                Log.d(TAG, "handleSavedState: Fragment is addided");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(fr).commit();
            }
        } else {
            if (tag.equals(getString(R.string.fragment_select_work_mode_tag))) {
                showSelectModeFragment();
            } else if (tag.equals(getString(R.string.fragment_client_mode_tag))) {
                showClientModeFragment();
            } else if (tag.equals(getString(R.string.fragment_server_mode_tag))) {
                showServerModeFragment();
            }
        }
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContainer = (RelativeLayout) findViewById(R.id.fragment_container);
    }

    public int getFragmentContainerId() {
        return mContainer.getId();
    }

    private void showSelectModeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(getFragmentContainerId(), FragmentSelectWorkMode.newInstance(), getString(R.string
                .fragment_select_work_mode_tag));
        ft.commit();
        Utils.hideKeyboard(this, mContainer.getWindowToken());
    }

    private void showClientModeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(getFragmentContainerId(),
                FragmentClientMode.newInstance(), getString(R.string.fragment_client_mode_tag));
        ft.commit();
        Utils.hideKeyboard(this, mContainer.getWindowToken());
    }

    private void showServerModeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(getFragmentContainerId(),
                FragmentServerMode.newInstance(), getString(R.string.fragment_server_mode_tag));
        ft.commit();
        Utils.hideKeyboard(this, mContainer.getWindowToken());
    }

    // Handle btn click from fragments
    @Override
    public void onClick(final int id) {
        switch (id) {
            case R.id.btn_start_client:
                showClientModeFragment();
                break;
            case R.id.btn_start_server:
                showServerModeFragment();
                startServirService();
        }
    }

    private void startServirService() {
        Intent intent = new Intent(this, ServerService.class);
        intent.putExtra(getString(R.string.bundle_key_inet_address), new PreferencesManager
                (this).getServerAddress());
        startService(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getFragmentManager().findFragmentByTag(getString(R.string
                .fragment_select_work_mode_tag)) != null) {
            outState.putString(getString(R.string.bundle_key_current_fragment_tag), getString(R.string
                    .fragment_select_work_mode_tag));
        } else if (getFragmentManager().findFragmentByTag(getString(R.string
                .fragment_client_mode_tag)) != null) {
            outState.putString(getString(R.string.bundle_key_current_fragment_tag), getString(R.string
                    .fragment_client_mode_tag));
        } else if (getFragmentManager().findFragmentByTag(getString(R.string
                .fragment_server_mode_tag)) != null) {
            outState.putString(getString(R.string.bundle_key_current_fragment_tag), getString(R.string
                    .fragment_server_mode_tag));
        }
        super.onSaveInstanceState(outState);
    }

    private String wifiIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

}
