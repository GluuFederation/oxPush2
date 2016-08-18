/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.model;

import org.gluu.com.ox_push2.u2f.v2.entry.Entry;

/**
 * oxAuth Fido U2F token entry
 *
 * Created by Yuriy Movchan on 01/13/2016.
 */
public class TokenEntry extends Entry{

    private String keyPair;
    private String application;
    private String authenticationType;
    private String authenticationMode;
    private byte[] keyHandle;
    private String keyName;

    public TokenEntry() {
    }

    public TokenEntry(String keyPair, String application, String issuer) {
        this.keyPair = keyPair;
        this.application = application;
        setIssuer(issuer);
    }

    public String getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationMode() {
        return authenticationMode;
    }

    public void setAuthenticationMode(String authenticationMode) {
        this.authenticationMode = authenticationMode;
    }

    public byte[] getKeyHandle() {
        return keyHandle;
    }

    public void setKeyHandle(byte[] keyHandle) {
        this.keyHandle = keyHandle;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
