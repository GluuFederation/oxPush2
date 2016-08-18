/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.util;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Provides methods to load and parse certificates
 *
 * Created by Yuriy Movchan on 01/26/2016.
 */
public class CertUtils {

    private static final String TAG = "cert-utils";

    public static X509Certificate loadHexEncodedCertificate(String encodedCertificateHex) {
        try {
            return loadCertificate(Utils.decodeHexString(encodedCertificateHex));
        } catch (Exception ex) {
            Log.e(TAG, "Failed to parce ceritifcate", ex);
        }

        return null;
    }

    public static X509Certificate loadCertificate(String certificate) {
        try {
            return loadCertificate(certificate.getBytes());
        } catch (Exception ex) {
            Log.e(TAG, "Failed to parse certificate", ex);
        }

        return null;
    }

    public static X509Certificate loadCertificate(byte[] encodedDerCertificate) throws CertificateException {
        X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(
                new ByteArrayInputStream(encodedDerCertificate));

        return certificate;
    }

}
