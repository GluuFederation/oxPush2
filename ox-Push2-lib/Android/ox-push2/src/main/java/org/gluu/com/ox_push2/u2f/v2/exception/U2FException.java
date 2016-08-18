/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.exception;

/**
 * Fido U2F exception
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class U2FException extends Exception {

    public U2FException(String message) {
        super(message);
    }

    public U2FException(String message, Throwable cause) {
        super(message, cause);
    }

}
