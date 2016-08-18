/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.codec;

import org.gluu.com.ox_push2.u2f.v2.exception.U2FException;
import org.gluu.com.ox_push2.u2f.v2.model.AuthenticateResponse;
import org.gluu.com.ox_push2.u2f.v2.model.EnrollmentResponse;

import java.nio.ByteBuffer;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * Fido U2F RAW data format service methods
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class RawMessageCodecImpl implements RawMessageCodec {

    private static final byte REGISTRATION_RESERVED_BYTE_VALUE = (byte) 0x05;
    private static final byte REGISTRATION_SIGNED_RESERVED_BYTE_VALUE = (byte) 0x00;

    @Override
    public byte[] encodeRegisterResponse(EnrollmentResponse enrollmentResponse)
            throws U2FException {
        byte[] userPublicKey = enrollmentResponse.getUserPublicKey();
        byte[] keyHandle = enrollmentResponse.getKeyHandle();
        byte[] signature = enrollmentResponse.getSignature();

        X509Certificate attestationCertificate = enrollmentResponse.getAttestationCertificate();

        byte[] attestationCertificateBytes;
        try {
            attestationCertificateBytes = attestationCertificate.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new U2FException("Error when encoding attestation certificate.", e);
        }

        if (keyHandle.length > 255) {
            throw new U2FException("keyHandle length cannot be longer than 255 bytes!");
        }

        byte[] result = new byte[1 + userPublicKey.length + 1 + keyHandle.length
                + attestationCertificateBytes.length + signature.length];
        ByteBuffer.wrap(result)
                .put(REGISTRATION_RESERVED_BYTE_VALUE)
                .put(userPublicKey)
                .put((byte) keyHandle.length)
                .put(keyHandle)
                .put(attestationCertificateBytes)
                .put(signature);
        return result;
    }

    @Override
    public byte[] encodeAuthenticateResponse(AuthenticateResponse authenticateResponse)
            throws U2FException {
        byte userPresence = authenticateResponse.getUserPresence();
        int counter = authenticateResponse.getCounter();
        byte[] signature = authenticateResponse.getSignature();

        byte[] result = new byte[1 + 4 + signature.length];
        ByteBuffer.wrap(result)
                .put(userPresence)
                .putInt(counter)
                .put(signature);
        return result;
    }

    @Override
    public byte[] encodeRegistrationSignedBytes(byte[] applicationSha256,
                                                byte[] challengeSha256, byte[] keyHandle, byte[] userPublicKey) {
        byte[] signedData = new byte[1 + applicationSha256.length + challengeSha256.length
                + keyHandle.length + userPublicKey.length];
        ByteBuffer.wrap(signedData)
                .put(REGISTRATION_SIGNED_RESERVED_BYTE_VALUE) // RFU
                .put(applicationSha256)
                .put(challengeSha256)
                .put(keyHandle)
                .put(userPublicKey);
        return signedData;
    }

    @Override
    public byte[] encodeAuthenticateSignedBytes(byte[] applicationSha256, byte userPresence,
                                                int counter, byte[] challengeSha256) {
        byte[] signedData = new byte[applicationSha256.length + 1 + 4 + challengeSha256.length];
        ByteBuffer.wrap(signedData)
                .put(applicationSha256)
                .put(userPresence)
                .putInt(counter)
                .put(challengeSha256);
        return signedData;
    }

}
