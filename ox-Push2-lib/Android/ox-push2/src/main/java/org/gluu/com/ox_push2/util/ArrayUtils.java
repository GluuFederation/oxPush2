/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.util;

import java.lang.reflect.Array;

/**
 * Array utility methods
 *
 * Created by Yuriy Movchan on 01/08/2016.
 */
public class ArrayUtils {

    public static boolean isEmpty(final Object[] array) {
        return getLength(array) == 0;
    }

    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }

        return Array.getLength(array);
    }

}
