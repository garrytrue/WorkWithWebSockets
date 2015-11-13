package com.garrytrue.workwithwebsocket.a.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public class Utils {
    private Utils() {
        new AssertionError();
    }

    public static final String IP_REGEXP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";

    public static boolean isAddressValid(String param) {
        Pattern pattern = Pattern.compile(IP_REGEXP);
        Matcher matcher = pattern.matcher(param);
        return matcher.matches();
    }

    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

}
