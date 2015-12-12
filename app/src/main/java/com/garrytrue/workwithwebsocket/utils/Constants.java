package com.garrytrue.workwithwebsocket.utils;


public class Constants {
    private Constants() {
        throw new AssertionError();
    }

    public static final String WEB_SOCKET_PREFIX = "ws://";

    //    Preference Keys
    public static final String PREF_KEY_SERVER_ADDRESS = "PREF_KEY_SERVER_ADDRESS";
    public static final String PREF_KEY_DOWNLOADED_IMAGE_URI = "PREF_KEY_DOWNLOADED_IMAGE_URI";

    //    Bundle Keys
    public static final String BUNDLE_KEY_CURRENT_FRAGMENT_TAG = "BUNDLE_KEY_CURRENT_FRAGMENT_TAG";
    public static final String BUNDLE_KEY_DEVICE_IP = "BUNDLE_KEY_DEVICE_IP";
    public static final String BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI";
    public static final String BUNDLE_KEY_TEMP_IMAGE_URI = "BUNDLE_KEY_TEMP_IMAGE_URI";

    //    Fragments tags
    public static final String TAG_FRAGMENT_SELECT_WORK_MODE = "TAG_FRAGMENT_SELECT_WORK_MODE";
    public static final String TAG_FRAGMENT_CLIENT_MODE = "TAG_FRAGMENT_CLIENT_MODE";
    public static final String TAG_FRAGMENT_SERVER_MODE = "TAG_FRAGMENT_SERVER_MODE";

}
