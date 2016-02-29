/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.gluu.oxpush2.util.Utils;

/**
 * Handles push messages recieved from server
 *
 * Created by Yuriy Movchan on 02/19/2016.
 */
public class PushNotificationService extends GcmListenerService {

    private static final String TAG = "main-activity";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String message = data.getString("message");
        if (Utils.isEmpty(title) || Utils.isEmpty(message)) {
            Log.e(TAG, "Get unknown push notification message: " + data.toString());
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.QR_CODE_PUSH_NOTIFICATION_MESSAGE, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        sendNotification(intent, "oxPush2 authentication request", null);

        startActivity(intent);
    }

    private void sendNotification(Intent intent, String title, String body) {
        Context context = getBaseContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setAutoCancel(true);

        if (Utils.isNotEmpty(body)) {
            builder.setContentText(body);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        builder.setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MainActivity.MESSAGE_NOTIFICATION_ID, builder.build());
    }}
