/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.model;

/**
 * Authentication response
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class AuthenticateResponse {

    private final byte userPresence;
    private final int counter;
    private final byte[] signature;

    public AuthenticateResponse(byte userPresence, int counter, byte[] signature) {
        this.userPresence = userPresence;
        this.counter = counter;
        this.signature = signature;
    }

    /**
     * Bit 0 is set to 1, which means that user presence was verified. (This
     * version of the protocol doesn't specify a way to request authentication
     * responses without requiring user presence.) A different value of Bit 0, as
     * well as Bits 1 through 7, are reserved for future use. The values of Bit 1
     * through 7 SHOULD be 0
     */
    public byte getUserPresence() {
        return userPresence;
    }

    /**
     * This is the big-endian representation of a counter value that the U2F token
     * increments every time it performs an authentication operation.
     */
    public int getCounter() {
        return counter;
    }

    /**
     * This is a ECDSA signature (on P-256)
     */
    public byte[] getSignature() {
        return signature;
    }

}
