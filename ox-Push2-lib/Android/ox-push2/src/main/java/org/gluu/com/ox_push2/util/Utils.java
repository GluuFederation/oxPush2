/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.util;

import android.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

/**
 * String utility methods
 *
 * Created by Yuriy Movchan on 12/07/2015.
 */
public class Utils {

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    public static String base64UrlEncode(byte[] arg) {
        return Base64.encodeToString(arg, 0, arg.length, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    public static byte[] base64UrlDecode(String arg) {
        return Base64.decode(arg, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static String encodeHexString(byte[] arg) {
        return new String(Hex.encodeHex(arg));
    }

    public static byte[] decodeHexString(String arg) throws DecoderException {
        return Hex.decodeHex(arg.toCharArray());
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isAnyEmpty(final CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return true;
        }
        for (final CharSequence cs : css){
            if (isEmpty(cs)) {
                return true;
            }
        }

        return false;
    }

    public static boolean areAllNotEmpty(final CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return false;
        }
        for (final CharSequence cs : css){
            if (isEmpty(cs)) {
                return false;
            }
        }

        return true;
    }

}
