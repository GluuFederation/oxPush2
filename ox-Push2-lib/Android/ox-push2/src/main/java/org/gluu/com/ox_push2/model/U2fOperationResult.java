/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.model;

/**
 * oxAuth Fido U2F status
 *
 * Created by Yuriy Movchan on 01/07/2016.
 */
public class U2fOperationResult {

    private String status;

    private String challenge;

    public U2fOperationResult() {
    }

    public U2fOperationResult(String status, String challenge) {
        this.status = status;
        this.challenge = challenge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
}

