/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.push;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.gluu.oxpush2.app.BuildConfig;
import org.gluu.oxpush2.app.listener.PushNotificationRegistrationListener;
import org.gluu.oxpush2.util.Utils;

import java.io.IOException;

/**
 * Provides GCM registration service
 *
 * Created by Yuriy Movchan on 02/22/2016.
 */
public class PushNotificationManager {

    private static final String TAG = "push-service";

    private static final String GCM_PREFS_FILE = "gcm_id.conf";

    // Constants
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "app_version";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String projectNumber;

    public PushNotificationManager(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException ex) {
            throw new RuntimeException("Could not get package name: " + ex);
        }
    }

    /**
     * Register if needed or fetch from local store
     */
    public void registerIfNeeded(final Activity activity, final PushNotificationRegistrationListener listener) {
        if (checkPlayServices(activity)) {
            String regid = getRegistrationId(activity);
            if (Utils.isEmpty(regid)) {
                registerInBackground(activity, listener);
            } else {
                if (BuildConfig.DEBUG) Log.d(TAG, "Already got GCM token: " + regid);
                listener.onPushRegistrationSuccess(regid, false);
            }
        } else {
            Log.e(TAG, "No valid Google Play Services APK found");
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     *
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final Context context, final PushNotificationRegistrationListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String regId = null;
                try {
                    InstanceID instanceID = InstanceID.getInstance(context);
                    regId = instanceID.getToken(projectNumber, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    if (BuildConfig.DEBUG) Log.d(TAG, "Successfully get GCM token: " + regId);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);

                    listener.onPushRegistrationSuccess(regId, true);
                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    Log.e(TAG, "Failed to get GCM token", ex);
                    listener.onPushRegistrationFailure(ex);
                }
            }
        }).start();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     *
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    public static String getRegistrationId(final Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, null);
        if (Utils.isEmpty(registrationId)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Registration not found");
            return null;
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            if (BuildConfig.DEBUG) Log.d(TAG, "App version changed");
            return null;
        }

        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(final Context context, final String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);

        int appVersion = getAppVersion(context);
        if (BuildConfig.DEBUG) Log.d(TAG, "Saving regId on app version " + appVersion);

        prefs.edit().putString(PROPERTY_REG_ID, regId).putInt(PROPERTY_APP_VERSION, appVersion).commit();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(final Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported");
            }

            return false;
        }

        return true;
    }

    private static SharedPreferences getGCMPreferences(final Context context) {
        return context.getSharedPreferences(GCM_PREFS_FILE, Context.MODE_PRIVATE);
    }

}
