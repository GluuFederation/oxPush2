/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.cert;

import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.gluu.com.ox_push2.BuildConfig;
import org.gluu.com.ox_push2.u2f.v2.exception.U2FException;
import org.gluu.com.ox_push2.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * Service to work generate key pair  and other crypto methods
 *
 * Created by Yuriy Movchan on 12/07/2015.
 */
public class KeyPairGeneratorImpl implements org.gluu.com.ox_push2.u2f.v2.cert.KeyPairGenerator {

    private static final String TAG = KeyPairGeneratorImpl.class.getName();

    private static BouncyCastleProvider bouncyCastleProvider;

    public static final BouncyCastleProvider BOUNCY_CASTLE_PROVIDER = new BouncyCastleProvider();

    static {
        bouncyCastleProvider = BOUNCY_CASTLE_PROVIDER;
    }

    @Override
    public KeyPair generateKeyPair() throws U2FException {
        // generate ECC key
        SecureRandom random = new SecureRandom();

        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        try {
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", bouncyCastleProvider);
            g.initialize(ecSpec, random);
            KeyPair keyPair = g.generateKeyPair();

            return keyPair;
        } catch (NoSuchAlgorithmException ex) {
            throw new U2FException("Failed to generate key pair", ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new U2FException("Failed to generate key pair", ex);
        }
    }

    @Override
    public byte[] sign(byte[] signedData, PrivateKey privateKey) throws U2FException {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA", bouncyCastleProvider);
            signature.initSign(privateKey);
            signature.update(signedData);
            return signature.sign();
        } catch (NoSuchAlgorithmException ex) {
            throw new U2FException("Error when signing", ex);
        } catch (SignatureException ex) {
            throw new U2FException("Error when signing", ex);
        } catch (InvalidKeyException ex) {
            throw new U2FException("Error when signing", ex);
        }
    }

    @Override
    public byte[] generateKeyHandle() {
        SecureRandom random = new SecureRandom();
        byte[] keyHandle = new byte[64];
        random.nextBytes(keyHandle);

        return keyHandle;
    }

    @Override
    public byte[] encodePublicKey(PublicKey publicKey) {
        byte[] encodedWithPadding = publicKey.getEncoded();
        byte[] encoded = new byte[65];
        System.arraycopy(encodedWithPadding, 26, encoded, 0, encoded.length);

        if (BuildConfig.DEBUG) Log.d(TAG, "Encoded public key: " + Utils.encodeHexString(encoded));

        return encoded;
    }

    @Override
    public PrivateKey loadPrivateKey(String privateKeyD) throws U2FException {
        try {
            KeyFactory fac = KeyFactory.getInstance("ECDSA", BOUNCY_CASTLE_PROVIDER);
            ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
            ECPrivateKeySpec keySpec = new ECPrivateKeySpec(
                    new BigInteger(privateKeyD, 16),
                    ecSpec);
            return fac.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException ex) {
            throw new U2FException("Failed to load private key", ex);
        } catch (InvalidKeySpecException ex) {
            throw new U2FException("Failed to load private key", ex);
        }
    }

    public String keyPairToJson(KeyPair keyPair) throws U2FException {
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

        BigInteger x = publicKey.getQ().getAffineXCoord().toBigInteger();
        BigInteger y = publicKey.getQ().getAffineYCoord().toBigInteger();
        BigInteger d = privateKey.getD();

        try {
            JSONObject jsonPrivateKey = new JSONObject();
            jsonPrivateKey.put("d", Utils.encodeHexString(d.toByteArray()));

            JSONObject jsonPublicKey = new JSONObject();
            jsonPublicKey.put("x", Utils.encodeHexString(x.toByteArray()));
            jsonPublicKey.put("y", Utils.encodeHexString(y.toByteArray()));

            JSONObject jsonKeyPair = new JSONObject();
            jsonKeyPair.put("privateKey", jsonPrivateKey);
            jsonKeyPair.put("publicKey", jsonPublicKey);

            String keyPairJson = jsonKeyPair.toString();

            if (BuildConfig.DEBUG) Log.d(TAG, "JSON key pair: " + keyPairJson);

            return keyPairJson;
        } catch (JSONException ex) {
            throw new U2FException("Failed to serialize key pair to JSON", ex);
        }
    }

    public KeyPair keyPairFromJson(String keyPairJson) throws U2FException {
        BigInteger x = null;
        BigInteger y = null;
        BigInteger d = null;

        try {
            JSONObject jsonKeyPair = (JSONObject) new JSONTokener(keyPairJson).nextValue();

            JSONObject jsonPrivateKey = jsonKeyPair.getJSONObject("privateKey");
            d = new BigInteger(Utils.decodeHexString(jsonPrivateKey.getString("d")));

            JSONObject jsonPublicKey = jsonKeyPair.getJSONObject("publicKey");
            x = new BigInteger(Utils.decodeHexString(jsonPublicKey.getString("x")));
            y = new BigInteger(Utils.decodeHexString(jsonPublicKey.getString("y")));
        } catch (JSONException ex) {
            throw new U2FException("Failed to deserialize key pair from JSON", ex);
        } catch (DecoderException ex) {
            throw new U2FException("Failed to deserialize key pair from JSON", ex);
        }

        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");

        ECCurve curve = ecSpec.getCurve();
        ECPoint validatePoint = curve.validatePoint(x, y);

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(validatePoint, ecSpec);
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(d, ecSpec);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("ECDSA", BOUNCY_CASTLE_PROVIDER);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            return new KeyPair(publicKey, privateKey);
        } catch (NoSuchAlgorithmException ex) {
            throw new U2FException("Failed to deserialize key pair from JSON", ex);
        } catch (InvalidKeySpecException ex) {
            throw new U2FException("Failed to deserialize key pair from JSON", ex);
        }
    }

}
