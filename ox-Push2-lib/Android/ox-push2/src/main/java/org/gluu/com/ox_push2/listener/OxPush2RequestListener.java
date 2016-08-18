/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.listener;

import org.gluu.com.ox_push2.model.OxPush2Request;
import org.gluu.com.ox_push2.u2f.v2.exception.U2FException;
import org.gluu.com.ox_push2.u2f.v2.model.TokenResponse;
import org.gluu.com.ox_push2.u2f.v2.store.DataStore;
import org.json.JSONException;

import java.io.IOException;

/**
 * OxPush2 listener
 *
 * Created by Yuriy Movchan on 01/07/2016.
 */
public interface OxPush2RequestListener {

    void onQrRequest(OxPush2Request oxPush2Request);

    TokenResponse onSign(String jsonRequest, String origin, Boolean isDeny) throws JSONException, IOException, U2FException;

    TokenResponse onEnroll(String jsonRequest, OxPush2Request oxPush2Request, Boolean isDeny) throws JSONException, IOException, U2FException;

    DataStore onGetDataStore();

}
