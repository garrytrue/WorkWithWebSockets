package com.garrytrue.workwithwebsocket.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Utils {
    private static final String IP_REGEXP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1," +
            "5})";

    private Utils() {
        throw new AssertionError();
    }

    public static boolean isAddressValid(String param) {
        return Pattern.compile(IP_REGEXP).matcher(param).matches();
    }

    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

}
