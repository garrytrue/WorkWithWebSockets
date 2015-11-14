package com.garrytrue.workwithwebsocket.a.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements IBtnClickListener {
    private static final String TAG = "MainActivity";
    private RelativeLayout mContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        String savedValue = savedInstanceState.getString
                (getString(R.string.bundle_key_current_fragment_tag));
        if (savedInstanceState != null && !TextUtils.isEmpty(savedValue)) {
            handleSavedState(savedValue);
        } else {
            showModeFragment(FragmentSelectWorkMode.newInstance(), getString(R.string
                    .fragment_select_work_mode_tag));
        }
    }
    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContainer = (RelativeLayout) findViewById(R.id.fragment_container);
    }


    private void handleSavedState(String tag) {
        Fragment fr = getFragmentManager().findFragmentById(getFragmentContainerId());
        String currentTag = fr.getTag();
        if (fr != null && !TextUtils.isEmpty(currentTag)) {
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
                showModeFragment(FragmentSelectWorkMode.newInstance(), tag);
            } else if (tag.equals(getString(R.string.fragment_client_mode_tag))) {
                showModeFragment(FragmentClientMode.newInstance(), tag);
            } else if (tag.equals(getString(R.string.fragment_server_mode_tag))) {
                showModeFragment(FragmentServerMode.newInstance(), tag);
            }
        }
    }

    public int getFragmentContainerId() {
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
                showModeFragment(FragmentClientMode.newInstance(), getString(R.string.fragment_client_mode_tag));
                break;
            case R.id.btn_start_server:
                showModeFragment(FragmentServerMode.newInstance(), getString(R.string.fragment_server_mode_tag));
                startServerService();
        }
    }

    private void startServerService() {
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
}
