package com.garrytrue.workwithwebsocket.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import static com.garrytrue.workwithwebsocket.utils.Constants.PREF_KEY_DOWNLOADED_IMAGE_URI;
import static com.garrytrue.workwithwebsocket.utils.Constants.PREF_KEY_SERVER_ADDRESS;


public class PreferencesManager {
    private final SharedPreferences mCustomPreferences;
    private final SharedPreferences.Editor mSettingsEditor;

    @SuppressLint("CommitPrefEdits")
    public PreferencesManager(Context context) {
        mCustomPreferences = android.preference.PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        mSettingsEditor = mCustomPreferences.edit();
    }

    public void putServerAddress(String address) {
        mSettingsEditor.putString(PREF_KEY_SERVER_ADDRESS, address)
                .apply();
    }

    public String getServerAddress() {
        return mCustomPreferences.getString(PREF_KEY_SERVER_ADDRESS,
                "");
    }

    public void putDownloadedImageUri(Uri uri) {
        mSettingsEditor.putString(PREF_KEY_DOWNLOADED_IMAGE_URI, uri.toString())
                .apply();
    }

    public Uri getDownLoadedImageUri() {
        return Uri.parse(mCustomPreferences.getString(PREF_KEY_DOWNLOADED_IMAGE_URI,
                ""));
    }
}
