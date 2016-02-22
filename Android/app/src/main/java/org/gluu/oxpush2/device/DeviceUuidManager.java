/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.gluu.oxpush2.app.BuildConfig;
import org.gluu.oxpush2.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Simple method to determine device UUID
 *
 * Created by Yuriy Movchan on 12/08/2015.
 */
public class DeviceUuidManager {

    private static final String TAG = "device-uuid";

    private static final String PREFS_FILE = "device_id.conf";
    private static final String PROPERTY_DEVICE_ID = "device_id";

    public DeviceUuidManager(){}

    public void init(Context context) {
        UUID deviceUuid = getDeviceUuid(context);
        if (deviceUuid != null) {
            return;
        }

        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        // Use the Android ID unless it's broken, in which case fallback on deviceId,
        // unless it's not available, then fallback on a random number which we store
        // to a prefs file
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                deviceUuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                deviceUuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        // Write the value out to the prefs file
        final SharedPreferences prefs = getUuidPreferences(context);
        prefs.edit().putString(PROPERTY_DEVICE_ID, deviceUuid.toString()).commit();
    }

    public static UUID getDeviceUuid(final Context context) {
        final SharedPreferences prefs = getUuidPreferences(context);
        String deviceUuid = prefs.getString(PROPERTY_DEVICE_ID, null);
        if (Utils.isEmpty(deviceUuid)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Device UUID not found");
            return null;
        }

        return UUID.fromString(deviceUuid);
    }

    private static SharedPreferences getUuidPreferences(final Context context) {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

}