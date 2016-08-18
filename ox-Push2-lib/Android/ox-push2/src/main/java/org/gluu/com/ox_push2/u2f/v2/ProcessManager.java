/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import org.gluu.com.ox_push2.BuildConfig;
import org.gluu.com.ox_push2.listener.OxPush2ManagerListener;
import org.gluu.com.ox_push2.listener.OxPush2RequestListener;
import org.gluu.com.ox_push2.model.LogInfo;
import org.gluu.com.ox_push2.model.OxPush2Request;
import org.gluu.com.ox_push2.model.U2fMetaData;
import org.gluu.com.ox_push2.model.U2fOperationResult;
import org.gluu.com.ox_push2.net.CommunicationService;
import org.gluu.com.ox_push2.u2f.v2.exception.U2FException;
import org.gluu.com.ox_push2.u2f.v2.model.TokenResponse;
import org.gluu.com.ox_push2.u2f.v2.store.DataStore;
import org.gluu.com.ox_push2.util.LogState;
import org.gluu.com.ox_push2.util.StringsUtil;
import org.gluu.com.ox_push2.util.Utils;
import org.json.JSONException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Process Fido U2F request fragment
 *
 * Created by Yuriy Movchan on 01/07/2016.
 */
public class ProcessManager {//extends Fragment implements View.OnClickListener {

    private static final String TAG = "process-fragment";

    private OxPush2Request oxPush2Request;

    private OxPush2RequestListener oxPush2RequestListener;

    private OxPush2ManagerListener oxPush2ManagerListener;

    private Activity activity;

    private DataStore dataStore;

    public ProcessManager() {}

    public ProcessManager(Activity activity, OxPush2Request oxPush2Request, OxPush2RequestListener oxPush2RequestListener) {
        this.activity = activity;
        this.oxPush2Request = oxPush2Request;
        this.oxPush2RequestListener = oxPush2RequestListener;
    }

    private void runOnUiThread(Runnable runnable) {
        if (activity != null) {
            activity.runOnUiThread(runnable);
        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "Activity is null!");
        }
    }

    private void setFinalStatus(String statusMessage) {
        oxPush2ManagerListener.oxPushResult(statusMessage, false);
    }

    public void onOxPushRequest(final Boolean isDeny) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (isDeny){
//                    setFinalStatus(R.string.process_deny_start);
//                } else {
//                    setFinalStatus(R.string.process_authentication_start);
//                }
//            }
//        });

        final boolean oneStep = Utils.isEmpty(oxPush2Request.getUserName());

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("application", oxPush2Request.getApp());
        parameters.put("session_state", oxPush2Request.getState());
        if (!oneStep) {
            parameters.put("username", oxPush2Request.getUserName());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final U2fMetaData u2fMetaData = getU2fMetaData();

//                    dataStore = oxPush2RequestListener.onGetDataStore();
                    final List<byte[]> keyHandles = dataStore.getKeyHandlesByIssuerAndAppId(oxPush2Request.getIssuer(), oxPush2Request.getApp());

                    final boolean isEnroll = (keyHandles.size() == 0) || oxPush2Request.getMethod().equals("enroll");
                    final String u2fEndpoint;
                    if (isEnroll) {
                        u2fEndpoint = u2fMetaData.getRegistrationEndpoint();
                        if (BuildConfig.DEBUG) Log.i(TAG, "Authentication method: enroll");
                    } else {
                        u2fEndpoint = u2fMetaData.getAuthenticationEndpoint();
                        if (BuildConfig.DEBUG) Log.i(TAG, "Authentication method: authenticate");
                    }

                    final String challengeJsonResponse;
                    if (oneStep && (keyHandles.size() > 0)) {
                        // Try to get challenge using all keyHandles associated with issuer and application

                        String validChallengeJsonResponse = null;
                        for (byte[] keyHandle : keyHandles) {
                            parameters.put("keyhandle", Utils.base64UrlEncode(keyHandle));
                            try {
                                validChallengeJsonResponse = CommunicationService.get(u2fEndpoint, parameters);
                                break;
                            } catch (FileNotFoundException ex) {
                                Log.i(TAG, "Found invalid keyHandle: " + Utils.base64UrlEncode(keyHandle));
                            }
                        }

                        challengeJsonResponse = validChallengeJsonResponse;
                        if (BuildConfig.DEBUG) Log.d(TAG, "Get U2F JSON response: " + challengeJsonResponse);

                    } else {
                        challengeJsonResponse = CommunicationService.get(u2fEndpoint, parameters);
                        if (BuildConfig.DEBUG) Log.d(TAG, "Get U2F JSON response: " + challengeJsonResponse);
                    }

                    if (Utils.isEmpty(challengeJsonResponse)) {
                        setFinalStatus(StringsUtil.NO_VALID_KEY_HANDLES);
                    } else {
                        try {
                            onChallengeReceived(isEnroll, u2fMetaData, u2fEndpoint, challengeJsonResponse, isDeny);
                        } catch (Exception ex) {
                            Log.e(TAG, "Failed to process challengeJsonResponse: " + challengeJsonResponse, ex);
                            setFinalStatus(StringsUtil.FAILED_PROCESS_CHALLENGE);
                        }
                    }
                } catch (final Exception ex) {
                    Log.e(TAG, "Failed to get Fido U2F metadata", ex);
                    if (ex.getCause().getMessage() != null){
                        setFinalStatus(ex.getCause().getMessage());
                    } else {
                        setFinalStatus(StringsUtil.WRONG_U2F_METADATA);
                    }
                }
            }
        }).start();
    }

    private U2fMetaData getU2fMetaData() throws IOException {
        // Request U2f meta data
        String discoveryUrl = oxPush2Request.getIssuer();
//        if (BuildConfig.DEBUG && discoveryUrl.contains(":8443")) {
//            discoveryUrl += "/oxauth/seam/resource/restv1/oxauth/fido-u2f-configuration";
//        } else {
            discoveryUrl += "/.well-known/fido-u2f-configuration";
//        }

        if (BuildConfig.DEBUG) Log.i(TAG, "Attempting to load U2F metadata from: " + discoveryUrl);

        final String discoveryJson = CommunicationService.get(discoveryUrl, null);
        final U2fMetaData u2fMetaData = new Gson().fromJson(discoveryJson, U2fMetaData.class);

        if (BuildConfig.DEBUG) Log.i(TAG, "Loaded U2f metadata: " + u2fMetaData);

        return u2fMetaData;
    }

    private void onChallengeReceived(boolean isEnroll, final U2fMetaData u2fMetaData, final String u2fEndpoint, final String challengeJson, final Boolean isDeny) throws IOException, JSONException, U2FException {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ((TextView) getView().findViewById(R.id.status_text)).setText(R.string.process_u2f_request);
//            }
//        });

        final TokenResponse tokenResponse;
        if (isEnroll) {
            tokenResponse = oxPush2RequestListener.onEnroll(challengeJson, oxPush2Request, isDeny);
        } else {
            tokenResponse = oxPush2RequestListener.onSign(challengeJson, u2fMetaData.getIssuer(), isDeny);
        }

        if (tokenResponse == null) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Token response is empty");
            setFinalStatus(StringsUtil.WRONG_TOKEN_RESPONSE);
            return;
        }

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", oxPush2Request.getUserName());
        parameters.put("tokenResponse", tokenResponse.getResponse());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String resultJsonResponse = CommunicationService.post(u2fEndpoint, parameters);
                    if (BuildConfig.DEBUG) Log.i(TAG, "Get U2F JSON result response: " + resultJsonResponse);
                     try {
                         final U2fOperationResult u2fOperationResult = new Gson().fromJson(resultJsonResponse, U2fOperationResult.class);
                         if (BuildConfig.DEBUG) Log.i(TAG, "Get U2f operation result: " + u2fOperationResult);
                                handleResult(isDeny, tokenResponse, u2fOperationResult);
                     } catch (Exception ex) {
                         Log.e(TAG, "Failed to process resultJsonResponse: " + resultJsonResponse, ex);
                         setFinalStatus(StringsUtil.FAILED_PROCESS_STATUS);
                     }
                } catch (final Exception ex) {
                    Log.e(TAG, "Failed to send Fido U2F response", ex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFinalStatus(StringsUtil.FAILED_PROCESS_RESPONSE);
                        }
                    });
                }
            }
        }).start();
    }

    private void handleResult(Boolean isDeny, TokenResponse tokenResponse, U2fOperationResult u2fOperationResult) {
        if (!tokenResponse.getChallenge().equals(u2fOperationResult.getChallenge())) {
            setFinalStatus(StringsUtil.Challenge_DOESNT_MATCH);
        }

        if ("success".equals(u2fOperationResult.getStatus())) {
            LogInfo log = new LogInfo();
            log.setIssuer(oxPush2Request.getIssuer());
            log.setUserName(oxPush2Request.getUserName());
            log.setLocationIP(oxPush2Request.getLocationIP());
            log.setLocationAddress(oxPush2Request.getLocationCity());
            log.setCreatedDate(String.valueOf(System.currentTimeMillis()));
            log.setMethod(oxPush2Request.getMethod());
            if (isDeny){
                setFinalStatus(StringsUtil.DENY_RESULT_SUCCESS);
                log.setLogState(LogState.LOGIN_DECLINED);
            } else {
                setFinalStatus(StringsUtil.AUTH_RESULT_SUCCESS);
                log.setLogState(LogState.LOGIN_SUCCESS);
            }
            dataStore.saveLog(log);
        } else {
            LogInfo log = new LogInfo();
            log.setIssuer(oxPush2Request.getIssuer());
            log.setUserName(oxPush2Request.getUserName());
            log.setLocationIP(oxPush2Request.getLocationIP());
            log.setLocationAddress(oxPush2Request.getLocationCity());
            log.setCreatedDate(String.valueOf(System.currentTimeMillis()));
            log.setMethod(oxPush2Request.getMethod());
            if (isDeny){
                setFinalStatus(StringsUtil.DENY_RESULT_FAILED);
                log.setLogState(LogState.LOGIN_DECLINED);
            } else {
                setFinalStatus(StringsUtil.AUTH_RESULT_FAILED);
                log.setLogState(LogState.LOGIN_FAILED);
            }

            dataStore.saveLog(log);
        }
        setIsButtonVisible(true);

    }

    public void setIsButtonVisible(Boolean isVsible){
        SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences("CleanLogsSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCleanButtonVisible", isVsible);
        editor.apply();//commit();
    }

    public void setOxPush2Request(OxPush2Request oxPush2Request) {
        this.oxPush2Request = oxPush2Request;
    }

    public void setOxPush2RequestListener(OxPush2RequestListener oxPush2RequestListener) {
        this.oxPush2RequestListener = oxPush2RequestListener;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setOxPush2ManagerListener(OxPush2ManagerListener oxPush2ManagerListener) {
        this.oxPush2ManagerListener = oxPush2ManagerListener;
    }
}
