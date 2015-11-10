package com.garrytrue.workwithwebsocket.a.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 07.11.15.
 */
public final class Validator {
    public static final String IP_REGEXP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";

    public static boolean isAddressValid(String param){
        Pattern pattern = Pattern.compile(IP_REGEXP);
        Matcher matcher = pattern.matcher(param);
        return matcher.matches();
    }

}
