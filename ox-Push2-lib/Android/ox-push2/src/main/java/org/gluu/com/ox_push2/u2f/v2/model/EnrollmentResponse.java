/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.model;

import java.security.cert.X509Certificate;

/**
 * Enrollment response
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class EnrollmentResponse {

    private final byte[] userPublicKey;
    private final byte[] keyHandle;
    private final X509Certificate attestationCertificate;
    private final byte[] signature;

    public EnrollmentResponse(byte[] userPublicKey, byte[] keyHandle,
                              X509Certificate attestationCertificate, byte[] signature) {
        this.userPublicKey = userPublicKey;
        this.keyHandle = keyHandle;
        this.attestationCertificate = attestationCertificate;
        this.signature = signature;
    }

    /**
     * This is the (uncompressed) x,y-representation of a curve point on the P-256
     * NIST elliptic curve.
     */
    public byte[] getUserPublicKey() {
        return userPublicKey;
    }

    /**
     * This a handle that allows the U2F token to identify the generated key pair.
     */
    public byte[] getKeyHandle() {
        return keyHandle;
    }

    /**
     * This is a X.509 certificate.
     */
    public X509Certificate getAttestationCertificate() {
        return attestationCertificate;
    }

    /**
     * This is a ECDSA signature (on P-256)
     */
    public byte[] getSignature() {
        return signature;
    }

}
