/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.codec;


import org.gluu.com.ox_push2.u2f.v2.exception.U2FException;
import org.gluu.com.ox_push2.u2f.v2.model.AuthenticateResponse;
import org.gluu.com.ox_push2.u2f.v2.model.EnrollmentResponse;

/**
 * Fido U2F RAW data format service methods
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public interface RawMessageCodec {

    byte[] encodeRegisterResponse(EnrollmentResponse enrollmentResponse)
            throws U2FException;

    byte[] encodeAuthenticateResponse(AuthenticateResponse authenticateResponse)
            throws U2FException;

    byte[] encodeRegistrationSignedBytes(byte[] applicationSha256,
                                         byte[] challengeSha256, byte[] keyHandle, byte[] userPublicKey);

    byte[] encodeAuthenticateSignedBytes(byte[] applicationSha256, byte userPresence,
                                         int counter, byte[] challengeSha256);

}
