/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.model;

import com.google.gson.annotations.SerializedName;

/**
 * oxAuth Fido U2F metadata
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class U2fMetaData {

    private String version;

    private String issuer;

    @SerializedName("registration_endpoint")
    private String registrationEndpoint;

    @SerializedName("authentication_endpoint")
    private String authenticationEndpoint;

    public U2fMetaData() {
    }

    public U2fMetaData(String version, String issuer, String registrationEndpoint, String authenticationEndpoint) {
        this.version = version;
        this.issuer = issuer;
        this.registrationEndpoint = registrationEndpoint;
        this.authenticationEndpoint = authenticationEndpoint;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }

    public void setRegistrationEndpoint(String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
    }

    public String getAuthenticationEndpoint() {
        return authenticationEndpoint;
    }

    public void setAuthenticationEndpoint(String authenticationEndpoint) {
        this.authenticationEndpoint = authenticationEndpoint;
    }

}
