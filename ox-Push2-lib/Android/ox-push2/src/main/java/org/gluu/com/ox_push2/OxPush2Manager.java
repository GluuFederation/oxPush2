package org.gluu.com.ox_push2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.gluu.com.ox_push2.listener.OxPush2ManagerListener;
import org.gluu.com.ox_push2.listener.OxPush2RequestListener;
import org.gluu.com.ox_push2.model.OxPush2Request;
import org.gluu.com.ox_push2.store.AndroidKeyDataStore;
import org.gluu.com.ox_push2.u2f.v2.ProcessManager;
import org.gluu.com.ox_push2.u2f.v2.SoftwareDevice;
import org.gluu.com.ox_push2.u2f.v2.exception.U2FException;
import org.gluu.com.ox_push2.u2f.v2.model.TokenResponse;
import org.gluu.com.ox_push2.u2f.v2.store.DataStore;
import org.gluu.com.ox_push2.util.Utils;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by nazaryavornytskyy on 8/11/16.
 */
public class OxPush2Manager implements OxPush2RequestListener {

    private static final String TAG = "oxPush2_manager";

    private SoftwareDevice u2f;

    private AndroidKeyDataStore dataStore;

    private OxPush2ManagerListener oxPush2ManagerListener;

    private OxPush2Request oxPush2Request;

    private ProcessManager processManager;

    private Activity activity;

    public OxPush2Manager(Activity activity, OxPush2Request oxPush2Request, OxPush2ManagerListener oxPush2ManagerListener){
        this.oxPush2Request = oxPush2Request;
        this.oxPush2ManagerListener = oxPush2ManagerListener;
        this.activity = activity;
        this.dataStore = new AndroidKeyDataStore(activity.getApplicationContext());
        this.u2f = new SoftwareDevice(activity.getApplicationContext(), dataStore);
    }

    public void doQrRequest(Boolean isApprove) {
        if (!validateOxPush2Request()) {
            return;
        }
        if (processManager == null){
            processManager = createProcessManager(oxPush2Request);
        }
        processManager.onOxPushRequest(!isApprove);
    }

    private boolean validateOxPush2Request() {
        boolean result = true;
        try {
            boolean isOneStep = Utils.isEmpty(oxPush2Request.getUserName());
            boolean isTwoStep = Utils.areAllNotEmpty(oxPush2Request.getUserName(), oxPush2Request.getIssuer(), oxPush2Request.getApp(),
                    oxPush2Request.getState(), oxPush2Request.getMethod());

//            if (BuildConfig.DEBUG) Log.d(TAG, "isOneStep: " + isOneStep + " isTwoStep: " + isTwoStep);

            if (isOneStep || isTwoStep) {
                // Valid authentication method should be used
                if (isTwoStep && !(Utils.equals(oxPush2Request.getMethod(), "authenticate") || Utils.equals(oxPush2Request.getMethod(), "enroll"))) {
                    result = false;
                }
            } else {
                // All fields must be not empty
                result = false;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to parse QR code");
            result = false;
        }

        if (!result) {
            oxPush2ManagerListener.oxPushResult("QR code contains invalid oxPush2 request", false);
//            Toast.makeText(getContext(), "QR code contains invalid oxPush2 request", Toast.LENGTH_LONG).show();//R.string.invalid_qr_code
        }

        return result;
    }

    private ProcessManager createProcessManager(OxPush2Request oxPush2Request){
        ProcessManager processManager = new ProcessManager();
        processManager.setOxPush2Request(oxPush2Request);
        processManager.setDataStore(dataStore);
        processManager.setOxPush2ManagerListener(oxPush2ManagerListener);
        processManager.setActivity(activity);
        processManager.setOxPush2RequestListener(new OxPush2RequestListener() {
            @Override
            public void onQrRequest(OxPush2Request oxPush2Request) {
                //skip code there
            }

            @Override
            public TokenResponse onSign(String jsonRequest, String origin, Boolean isDeny) throws JSONException, IOException, U2FException {
                return u2f.sign(jsonRequest, origin, isDeny);
            }

            @Override
            public TokenResponse onEnroll(String jsonRequest, OxPush2Request oxPush2Request, Boolean isDeny) throws JSONException, IOException, U2FException {
                return u2f.enroll(jsonRequest, oxPush2Request, isDeny);
            }

            @Override
            public DataStore onGetDataStore() {
                return dataStore;
            }
        });

        return processManager;
    }

    @Override
    public void onQrRequest(OxPush2Request oxPush2Request) {
        doQrRequest(true);
    }

    @Override
    public TokenResponse onSign(String jsonRequest, String origin, Boolean isDeny) throws JSONException, IOException, U2FException {
        return u2f.sign(jsonRequest, origin, isDeny);
    }

    @Override
    public TokenResponse onEnroll(String jsonRequest, OxPush2Request oxPush2Request, Boolean isDeny) throws JSONException, IOException, U2FException {
        return u2f.enroll(jsonRequest, oxPush2Request, isDeny);
    }

    @Override
    public DataStore onGetDataStore() {
        return dataStore;
    }
}

