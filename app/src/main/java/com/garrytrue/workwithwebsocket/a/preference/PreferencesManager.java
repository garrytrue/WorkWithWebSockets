package com.garrytrue.workwithwebsocket.a.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.garrytrue.workwithwebsocket.R;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 08.11.15.
 */
public class PreferencesManager {
    private final SharedPreferences mCustomPreferences;
    private final SharedPreferences.Editor mSettingsEditor;
    private final Context mContext;

    public PreferencesManager(Context context) {
        mContext = context;
        mCustomPreferences = android.preference.PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        mSettingsEditor = mCustomPreferences.edit();
    }
    public void putServerAddress(String address) {
        mSettingsEditor.putString(mContext.getString(R.string.pref_key_server_address), address)
                .commit();
    }

    public String getServerAddress() {
        return mCustomPreferences.getString(mContext.getString(R.string.pref_key_server_address),
                "");
    }
}
