/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.StringUtils;
import org.gluu.oxpush2.app.BuildConfig;
import org.gluu.oxpush2.u2f.v2.model.TokenEntry;
import org.gluu.oxpush2.u2f.v2.store.DataStore;
import org.gluu.oxpush2.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to store key pair in application preferences
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class AndroidKeyDataStore implements DataStore {

    private static final String U2F_KEY_PAIR_FILE = "u2f_key_pairs";
    private static final String U2F_KEY_COUNT_FILE = "u2f_key_counts";
    private static final String U2F_TOKEN_ENTITY = "u2f_token_entity";
    private static final String U2F_KEY_HANDLE_ENTITY = "u2f_key_handle_entity";

    private static final String TAG = "key-data-store";
    private final Context context;

    public AndroidKeyDataStore(Context context) {
        this.context = context;

        // Prepare empty U2F key pair store
        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);
        if (keySettings.getAll().size() == 0) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Creating empty U2K key pair store");
            keySettings.edit().commit();
        }

        // Prepare empty U2F key counter store
        final SharedPreferences keyCounts = context.getSharedPreferences(U2F_KEY_COUNT_FILE, Context.MODE_PRIVATE);
        if (keyCounts.getAll().size() == 0) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Creating empty U2K key counter store");
            keyCounts.edit().commit();
        }
    }

    @Override
    public void storeTokenEntry(byte[] keyHandle, TokenEntry tokenEntry) {
        String keyHandleKey = keyHandleToKey(keyHandle);

        final String tokenEntryString = new Gson().toJson(tokenEntry);
        if (BuildConfig.DEBUG) Log.d(TAG, "Storing new keyHandle: " + keyHandleKey + " with tokenEntry: " + tokenEntryString);

        final SharedPreferences keySettings = context.getSharedPreferences(U2F_KEY_PAIR_FILE, Context.MODE_PRIVATE);

        keySettings.edit().putString(keyHandleKey, tokenEntryString).commit();

        final SharedPreferences keyCounts = context.getSharedPreferences(U2F_KEY_COUNT_FILE, Context.MODE_PRIVATE);
        keyCounts.edit().putInt(keyHandleKey, 0).commit();
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

        keyCounts.edit().putInt(keyHandleKey, currentCounter).commit();

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

            if (((issuer == null) || StringUtils.equals(issuer, tokenEntry.getIssuer()))
                    && ((application == null) || StringUtils.equals(application, tokenEntry.getApplication()))) {
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

    private String keyHandleToKey(byte[] keyHandle) {
        return Utils.encodeHexString(keyHandle);
    }

    public byte[] keyToKeyHandle(String key) throws DecoderException {
        return Utils.decodeHexString(key);
    }

}