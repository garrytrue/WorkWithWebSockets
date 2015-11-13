package com.garrytrue.workwithwebsocket.a.utils;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 12.11.15.
 */
public class DecoderEncoderUtils {
    private DecoderEncoderUtils() {
        new AssertionError();
    }

    private static final String ALGORITHM = "AES";

    public static byte[] encodeByteArray(byte[] array, SecretKey key) throws Exception {
        Cipher chiper = Cipher.getInstance(ALGORITHM);
        chiper.init(Cipher.ENCRYPT_MODE, key);
        return chiper.doFinal(array);
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static byte[] decodeByteArray(byte[] array, SecretKey key) throws Exception {
        Cipher chiper = Cipher.getInstance(ALGORITHM);
        chiper.init(Cipher.DECRYPT_MODE, key);
        return chiper.doFinal(array);
    }

    public static SecretKey keyFromString(String str) {
        byte[] encodedKey = Base64.decode(str, Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public static String keyToString(SecretKey key) {
        String stringKey = "";
        if (key != null) {
            stringKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        }
        return stringKey;
    }

}
