/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.model;

/**
 * Authentication request
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class AuthenticateRequest {

    public static final byte USER_PRESENT_FLAG = (byte) 0x01;
    public static final byte USER_PRESENCE_SIGN = 0x03;
    public static final byte CHECK_ONLY = 0x07;

    private final String version;
    private final byte control;
    private final String challenge;
    private final String application;
    private final byte[] keyHandle;

    public AuthenticateRequest(String version, byte control, String challenge, String application,
                               byte[] keyHandle) {
        this.version = version;
        this.control = control;
        this.challenge = challenge;
        this.application = application;
        this.keyHandle = keyHandle;
    }

    public String getVersion() {
        return version;
    }

    /**
     * The FIDO Client will set the control byte to one of the following values:
     * 0x07 ("check-only")
     * 0x03 ("enforce-user-presence-and-sign")
     */
    public byte getControl() {
        return control;
    }

    /**
     * The challenge parameter
     */
    public String getChallenge() {
        return challenge;
    }

    /**
     * The application parameter is the application identity of the application requesting the registration
     */
    public String getApplication() {
        return application;
    }

    /**
     * The key handle obtained during registration
     */
    public byte[] getKeyHandle() {
        return keyHandle;
    }

}
