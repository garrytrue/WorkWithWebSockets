package com.garrytrue.workwithwebsocket.a.utils;

import android.util.Log;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 12.11.15.
 */
public final class DecoderEncoderUtils {

    private static final String ALGORITHM = "AES";
    private static final String TAG = "DecoderEncoderUtils";

    public static byte[] encodeByteArray(byte[] array) {
        Key key = generateKey();
        try {
            Cipher chiper = Cipher.getInstance(ALGORITHM);
            chiper.init(Cipher.ENCRYPT_MODE, key);
            return chiper.doFinal(array);
        }catch (Exception ex){
            Log.e(TAG, "encodeByteArray: ", ex);
            return null;
        }

    }

    public static Key generateKey() {
        SecretKey key = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(128);
            key = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "generateKey: ", e);
        }
        return key;
    }
    public static byte [] decodeByteArray (byte[] array, String pass) {
        Key key = generateKey();
        try {
            Cipher chiper = Cipher.getInstance(ALGORITHM);
            chiper.init(Cipher.DECRYPT_MODE, key);
            return chiper.doFinal(array);
        }catch (Exception ex){
            Log.e(TAG, "DecodeByteArray: ", ex);
            return null;
        }


    }

}
