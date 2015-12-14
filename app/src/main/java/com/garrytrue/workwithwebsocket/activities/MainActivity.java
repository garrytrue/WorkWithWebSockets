package com.garrytrue.workwithwebsocket.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.fragments.FragmentClientMode;
import com.garrytrue.workwithwebsocket.fragments.FragmentSelectWorkMode;
import com.garrytrue.workwithwebsocket.fragments.FragmentServerMode;
import com.garrytrue.workwithwebsocket.interfaces.IBtnClickListener;
import com.garrytrue.workwithwebsocket.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.services.ServerService;
import com.garrytrue.workwithwebsocket.utils.Utils;

import static com.garrytrue.workwithwebsocket.utils.Constants.BUNDLE_KEY_CURRENT_FRAGMENT_TAG;
import static com.garrytrue.workwithwebsocket.utils.Constants.BUNDLE_KEY_DEVICE_IP;
import static com.garrytrue.workwithwebsocket.utils.Constants.TAG_FRAGMENT_CLIENT_MODE;
import static com.garrytrue.workwithwebsocket.utils.Constants.TAG_FRAGMENT_SELECT_WORK_MODE;
import static com.garrytrue.workwithwebsocket.utils.Constants.TAG_FRAGMENT_SERVER_MODE;


public class MainActivity extends AppCompatActivity implements IBtnClickListener {
    private RelativeLayout mContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        if (savedInstanceState == null) {
            showModeFragment(FragmentSelectWorkMode.newInstance(), TAG_FRAGMENT_SELECT_WORK_MODE);
        }
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContainer = (RelativeLayout) findViewById(R.id.fragment_container);
    }

    private int getFragmentContainerId() {
        return mContainer.getId();
    }

    private void showModeFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(getFragmentContainerId(), fragment, tag);
        ft.commit();
        Utils.hideKeyboard(this, mContainer.getWindowToken());
    }

    // Handle btn click from fragments
    @Override
    public void onClick(final int id) {
        switch (id) {
            case R.id.btn_start_client:
                showModeFragment(FragmentClientMode.newInstance(), TAG_FRAGMENT_CLIENT_MODE);
                break;
            case R.id.btn_start_server:
                showModeFragment(FragmentServerMode.newInstance(), TAG_FRAGMENT_SERVER_MODE);
                startServerService();
                break;
        }
    }

    private void startServerService() {
        Intent intent = new Intent(this, ServerService.class);
        intent.putExtra(BUNDLE_KEY_DEVICE_IP, new PreferencesManager
                (this).getServerAddress());
        startService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        String tag = getFragmentManager().findFragmentById
                (getFragmentContainerId()).getTag();
        outState.putString(BUNDLE_KEY_CURRENT_FRAGMENT_TAG, tag);
        super.onSaveInstanceState(outState);
    }
}
