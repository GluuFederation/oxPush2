/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.app;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Handles push messages recieved from server
 *
 * Created by Yuriy Movchan on 02/19/2016.
 */
public class PushNotificationService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        //createNotification(mTitle, push_msg);
    }
}
