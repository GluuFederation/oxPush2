/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.gluu.com.ox_push2.BuildConfig;
import org.gluu.com.ox_push2.util.CertUtils;
import org.gluu.com.ox_push2.util.Utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Network communication service
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class CommunicationService {

    private static final String TAG = "communication-service";

    public static String get(String baseUrl, Map<String, String> params) throws IOException {
        if (BuildConfig.DEBUG) Log.d(TAG, "Attempting to execute get with parameters: " + params);

        HttpURLConnection connection = null;
        try {
            String urlParameters = getEncodedUrlParameters(params);
            URL url;
            if (urlParameters == null) {
                url = new URL(baseUrl);
            } else {
                url = new URL(baseUrl + '?' + urlParameters);
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);

            //Get Response
            InputStream is = connection.getInputStream();

            return readStream(is);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String post(String baseUrl, Map<String, String> params) throws IOException {
        if (BuildConfig.DEBUG) Log.d(TAG, "Attempting to execute post with parameters: " + params);

        HttpURLConnection connection = null;
        try {
            String urlParameters = getEncodedUrlParameters(params);

            URL url = new URL(baseUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // Read Response
            InputStream is = connection.getInputStream();
            return readStream(is);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getEncodedUrlParameters(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null) {
            return null;
        }

        StringBuilder urlParametersBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();

            String key = param.getKey();
            String value = param.getValue();

            if (BuildConfig.DEBUG) Log.d(TAG, key + " = " + value);
            if (value == null) {
                if (BuildConfig.DEBUG) Log.w(TAG, "Key '" + key + "' value is null");
                continue;
            }

            urlParametersBuilder.append(key).append('=').append(URLEncoder.encode(value, "UTF-8"));
            if (iterator.hasNext()) {
                urlParametersBuilder.append('&');
            }
        }

        return urlParametersBuilder.toString();
    }

    private static String readStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();

        return result.toString();
    }

    /**
     * Checking for all possible internet providers
     */
    public boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
            if (networkInfos == null) {
                return false;
            }

            for (NetworkInfo networkInfo : networkInfos) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void initTrustAllTrustManager() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Exception ex) {
            Log.e(TAG, "Failed to install Trust All TrustManager", ex);
        }
    }

    public static void initTrustCertTrustManager(String certificate, boolean skipHostnameVerification) {
        // Load certificate
        X509Certificate cert = CertUtils.loadCertificate(certificate);

        if (cert == null) {
            Log.e(TAG, "Failed to load certificate");
        } else {
            initTrustCertTrustManager(cert, skipHostnameVerification);
        }
    }

    public static void initTrustCertTrustManager(X509Certificate cert, boolean skipHostnameVerification) {
            try {
            String alias = cert.getSubjectX500Principal().getName();

            // Create trust store
            KeyStore trustStore = null;
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, cert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(trustStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            // Install the trust-cert trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Exception ex) {
            Log.e(TAG, "Failed to install Trust Cert TrustManager", ex);
        }

        if (skipHostnameVerification) {
            setTrustAllHostnameVerifier();
        }
    }

    private static void setTrustAllHostnameVerifier() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
    }

    public static void init() {
        if (BuildConfig.DEBUG) {
            // Init trust all manager
            if (BuildConfig.TRUST_ALL_CERT) {
                initTrustAllTrustManager();
                return;
            }

            // Init trust manager to trust only specific server and skip hostname verifiaction
            if (Utils.isNotEmpty(BuildConfig.OX_SERVER_CERT)) {
                initTrustCertTrustManager(BuildConfig.OX_SERVER_CERT, true);
            }
        } else {
            // Init trust manager to trust only specific server
            if (Utils.isNotEmpty(BuildConfig.OX_SERVER_CERT)) {
                initTrustCertTrustManager(BuildConfig.OX_SERVER_CERT, false);
            }
        }
    }
}
