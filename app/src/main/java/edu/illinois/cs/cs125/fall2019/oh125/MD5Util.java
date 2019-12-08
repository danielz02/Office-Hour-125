package edu.illinois.cs.cs125.fall2019.oh125;

import android.util.Log;

import java.util.*;
import java.io.*;
import java.security.*;

/**
 * The MD5 utility for fetching gravatar
 */
public abstract class MD5Util {
    /**
     *
     * @param array a array of MD5 value
     * @return a String of MD5 value in hexadecimal
     */
    public static String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    /**
     * Calculate MD5 value from a String
     * @param message the String to calculate MD5
     * @return the MD5 value, in hexadecimal, of the input String
     */
    public static String md5Hex (String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex (md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException e) {
            Log.w("Failed to calculate MD5", e);
        } catch (UnsupportedEncodingException e) {
            Log.w("Encoding is not supported", e);
        }
        return null;
    }
}
