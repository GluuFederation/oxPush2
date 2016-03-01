/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.u2f.v2.model;

/**
 * oxAuth Fido U2F token entry
 *
 * Created by Yuriy Movchan on 01/13/2016.
 */
public class TokenEntry{

    private String keyPair;
    private String application;
    private String issuer;
    private String userName;
    private String pairingDate;
    private String authenticationType;
    private String authenticationMode;
    private byte[] keyHandle;

    public TokenEntry() {
    }

    public TokenEntry(String keyPair, String application, String issuer) {
        this.keyPair = keyPair;
        this.application = application;
        this.issuer = issuer;
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

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getPairingDate() {
        return pairingDate;
    }

    public void setPairingDate(String pairingDate) {
        this.pairingDate = pairingDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
