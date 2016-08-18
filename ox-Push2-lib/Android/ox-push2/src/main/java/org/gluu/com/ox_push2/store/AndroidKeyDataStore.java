/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.codec.DecoderException;
import org.gluu.com.ox_push2.BuildConfig;
import org.gluu.com.ox_push2.model.LogInfo;
import org.gluu.com.ox_push2.u2f.v2.model.TokenEntry;
import org.gluu.com.ox_push2.u2f.v2.store.DataStore;
import org.gluu.com.ox_push2.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides methods to store key pair in application preferences
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class AndroidKeyDataStore implements DataStore {

    private static final String U2F_KEY_PAIR_FILE = "u2f_key_pairs";
    private static final String U2F_KEY_COUNT_FILE = "u2f_key_counts";
    private static final String LOGS_STORE = "logs_store";
    private static final String LOGS_KEY = "logs_key";

    private static final String TAG = "key-data-store";
    private final Context context;

    public AndroidKeyDataStore(Context context) {
        this.context = context;

        // Prepare empty U2F key pair store
        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
        if (keySettings.getAll().size() == 0) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Creating empty U2K key pair store");
            keySettings.edit().apply();//commit();
        }

        // Prepare empty U2F key counter store
        final SharedPreferences keyCounts = context.getSharedPreferences(U2F_KEY_COUNT_FILE, Context.MODE_PRIVATE);
        if (keyCounts.getAll().size() == 0) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Creating empty U2K key counter store");
            keyCounts.edit().apply();//commit();
        }
    }

    @Override
    public void storeTokenEntry(byte[] keyHandle, TokenEntry tokenEntry) {
        Boolean isSave = true;
        List<String> tokens = getTokenEntries();
        for (String tokenStr : tokens){
            TokenEntry token = new Gson().fromJson(tokenStr, TokenEntry.class);
            if (token.getIssuer().equalsIgnoreCase(tokenEntry.getIssuer())){
                isSave = false;
            }
        }
        if (isSave) {
            String keyHandleKey = keyHandleToKey(keyHandle);

            final String tokenEntryString = new Gson().toJson(tokenEntry);
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Storing new keyHandle: " + keyHandleKey + " with tokenEntry: " + tokenEntryString);

            final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);

            keySettings.edit().putString(keyHandleKey, tokenEntryString).apply();//commit();

            final SharedPreferences keyCounts = context.getSharedPreferences(U2F_KEY_COUNT_FILE, Context.MODE_PRIVATE);
            keyCounts.edit().putInt(keyHandleKey, 0).apply();//commit();
        }
    }

    @Override
    public TokenEntry getTokenEntry(byte[] keyHandle) {
        String keyHandleKey = keyHandleToKey(keyHandle);

        if (BuildConfig.DEBUG) Log.d(TAG, "Getting keyPair by keyHandle: " + keyHandleKey);

        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
        final String tokenEntryString = keySettings.getString(keyHandleKey, null);

        if (BuildConfig.DEBUG) Log.d(TAG, "Found tokenEntry " + tokenEntryString + " by keyHandle: " + keyHandleKey);

        final TokenEntry tokenEntry = new Gson().fromJson(tokenEntryString, TokenEntry.class);

        return tokenEntry;
    }

    @Override
    public int incrementCounter(byte[] keyHandle) {
        String keyHandleKey = keyHandleToKey(keyHandle);

        if (BuildConfig.DEBUG) Log.d(TAG, "Incrementing keyHandle: " + keyHandleKey + " counter");

        final SharedPreferences keyCounts = context.getSharedPreferences(U2F_KEY_COUNT_FILE, Context.MODE_PRIVATE);

        int currentCounter = keyCounts.getInt(keyHandleKey, -1);
        currentCounter++;

        keyCounts.edit().putInt(keyHandleKey, currentCounter).apply();//commit();

        if (BuildConfig.DEBUG) Log.d(TAG, "Counter is " + currentCounter + " for keyHandle: " + keyHandleKey + " counter");

        return currentCounter;
    }

    @Override
    public List<byte[]> getKeyHandlesByIssuerAndAppId(String issuer, String application) {
        List<byte[]> result = new ArrayList<byte[]>();

        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
        Map<String, String> keyTokens = (Map<String, String>) keySettings.getAll();
        for (Map.Entry<String, String> keyToken : keyTokens.entrySet()) {
            String tokenEntryString = keyToken.getValue();

            TokenEntry tokenEntry = new Gson().fromJson(tokenEntryString, TokenEntry.class);

            if (((issuer == null) || issuer.equals(tokenEntry.getIssuer()))
                    && ((application == null) || application.equals(tokenEntry.getApplication()))) {
                String keyHandleKey = keyToken.getKey();
                try {
                    byte[] keyHandle = keyToKeyHandle(keyHandleKey);
                    result.add(keyHandle);
                } catch (DecoderException ex) {
                    Log.e(TAG, "Invalid keyHandle: " + keyHandleKey, ex);
                }
            }
        }
        return result;
    }

    @Override
    public List<byte[]> getAllKeyHandles() {
        return getKeyHandlesByIssuerAndAppId(null, null);
    }

    @Override
    public List<String> getTokenEntries() {
        List<String> result = new ArrayList<String>();

        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
        Map<String, String> keyTokens = (Map<String, String>) keySettings.getAll();
        for (Map.Entry<String, String> keyToken : keyTokens.entrySet()) {
            String tokenEntryString = keyToken.getValue();
            TokenEntry tokenEntry = new Gson().fromJson(tokenEntryString, TokenEntry.class);
            String keyHandleKey = keyToken.getKey();
            try {
                byte[] keyHandle = keyToKeyHandle(keyHandleKey);
                tokenEntry.setKeyHandle(keyHandle);
            } catch (DecoderException e) {
                Log.e(TAG, "Decoder exception: ", e);
            }
            tokenEntryString = new Gson().toJson(tokenEntry);
            result.add(tokenEntryString);
        }
        return result;
    }

    @Override
    public void deleteTokenEntry(byte[] keyHandle) {
        String keyHandleKey = keyHandleToKey(keyHandle);
        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
        keySettings.edit().remove(keyHandleKey).apply();//commit();
    }


    //Methods for logs

    @Override
    public void saveLog(LogInfo logInfo) {
        final String logInfoString = new Gson().toJson(logInfo);
        final SharedPreferences logSettings = context.getSharedPreferences(LOGS_STORE, Context.MODE_PRIVATE);
        logSettings.edit().putString(UUID.randomUUID().toString(), logInfoString).apply();//commit();
    }

    @Override
    public List<LogInfo> getLogs() {
        final SharedPreferences logSettings = context.getSharedPreferences(LOGS_STORE, Context.MODE_PRIVATE);
        Map<String, String> logsMap = (Map<String, String>) logSettings.getAll();
        List<LogInfo> logs = new ArrayList<LogInfo>();
        for (Map.Entry<String, String> log : logsMap.entrySet()) {
            logs.add(new Gson().fromJson(log.getValue(), LogInfo.class));
        }
        return logs;
    }

    @Override
    public void deleteLogs() {
        final SharedPreferences logSettings = context.getSharedPreferences(LOGS_STORE, Context.MODE_PRIVATE);
        logSettings.edit().clear().apply();//commit();
    }

    @Override
    public void changeKeyHandleName(String keyHandleID, String newName) {

        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
        Map<String, String> keyTokens = (Map<String, String>) keySettings.getAll();
        for (Map.Entry<String, String> keyToken : keyTokens.entrySet()) {
            String tokenEntryString = keyToken.getValue();

            TokenEntry tokenEntry = new Gson().fromJson(tokenEntryString, TokenEntry.class);

            if (keyHandleID != null && keyHandleID.equals(tokenEntry.getIssuer())){
                tokenEntry.setKeyName(newName);
                SharedPreferences tokenSettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
                String tokenEntryStr = new Gson().toJson(tokenEntry);
                String keyHandleKey = keyHandleToKey(tokenEntry.getKeyHandle());
                tokenSettings.edit().putString(keyHandleKey, tokenEntryStr).apply();//commit();
                return;
            }
        }
    }

    private String keyHandleToKey(byte[] keyHandle) {
        return Utils.encodeHexString(keyHandle);
    }

    public byte[] keyToKeyHandle(String key) throws DecoderException {
        return Utils.decodeHexString(key);
    }

}