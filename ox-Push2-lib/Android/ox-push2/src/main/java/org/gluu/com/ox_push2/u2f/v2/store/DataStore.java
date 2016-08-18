/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.store;

import org.gluu.com.ox_push2.model.LogInfo;
import org.gluu.com.ox_push2.u2f.v2.model.TokenEntry;

import java.util.List;

/**
 * Service to work with key pair store
 *
 * Created by Yuriy Movchan on 12/07/2015.
 */
public interface DataStore {

    void storeTokenEntry(byte[] keyHandle, TokenEntry tokenEntry);

    TokenEntry getTokenEntry(byte[] keyHandle);

    int incrementCounter(byte[] keyHandle);

    List<byte[]> getKeyHandlesByIssuerAndAppId(String application, String issuer);

    List<byte[]> getAllKeyHandles();

    List<String> getTokenEntries();

    void deleteTokenEntry(byte[] keyHandle);

    //Methods for logs (save, get, delete, ....)

    void saveLog(LogInfo logText);
    List<LogInfo>getLogs();
    void deleteLogs();
    void changeKeyHandleName(String keyHandleID, String newName);

}
