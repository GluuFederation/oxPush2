/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.net;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
//import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * GCM Push notification service
 *
 * Created by Yuriy Movchan on 02/19/2016.
 */
public class PushNotificationIntentService {

        private static final String TAG = "push-notification-service";

//    /**
//     * Registers the application with GCM servers asynchronously.
//     * <p>
//     * Stores the registration ID and app versionCode in the application's
//     * shared preferences.
//     */
//    private void registerInBackground(final RegistrationCompletedHandler handler) {
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(getContext());
//                    }
//                    InstanceID instanceID = InstanceID.getInstance(getContext());
//                    regid = instanceID.getToken(projectNumber, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//                    Log.i(TAG, regid);
//                    // Persist the regID - no need to register again.
//                    storeRegistrationId(getContext(), regid);
//                } catch (IOException ex) {
//                    // If there is an error, don't just keep trying to register.
//                    // Require the user to click a button again, or perform
//                    // exponential back-off.
//                    handler.onFailure("Error :" + ex.getMessage());
//                }
//                return regid;
//            }
//            @Override
//            protected void onPostExecute(String regId) {
//                if (regId != null) {
//                    handler.onSuccess(regId, true);
//                }
//            }
//        }.execute(null, null, null);
//    }
//    private Controller aController = null;
//
//        public GCMIntentService() {
//            // Call extended class Constructor GCMBaseIntentService
//            super(Config.GOOGLE_SENDER_ID);
//        }
//
//        /**
//         * Method called on device registered
//         **/
//        @Override
//        protected void onRegistered(Context context, String registrationId) {
//
//            //Get Global Controller Class object (see application tag in AndroidManifest.xml)
//            if(aController == null)
//                aController = (Controller) getApplicationContext();
//
//            Log.i(TAG, "Device registered: regId = " + registrationId);
//            aController.displayMessageOnScreen(context,
//                    "Your device registred with GCM");
//            Log.d("NAME", MainActivity.name);
//            aController.register(context, MainActivity.name,
//                    MainActivity.email, registrationId);
//        }
//
//        /**
//         * Method called on device unregistred
//         * */
//        @Override
//        protected void onUnregistered(Context context, String registrationId) {
//            if(aController == null)
//                aController = (Controller) getApplicationContext();
//            Log.i(TAG, "Device unregistered");
//            aController.displayMessageOnScreen(context,
//                    getString(R.string.gcm_unregistered));
//            aController.unregister(context, registrationId);
//        }
//
//        /**
//         * Method called on Receiving a new message from GCM server
//         * */
//        @Override
//        protected void onMessage(Context context, Intent intent) {
//
//            if(aController == null)
//                aController = (Controller) getApplicationContext();
//
//            Log.i(TAG, "Received message");
//            String message = intent.getExtras().getString("price");
//
//            aController.displayMessageOnScreen(context, message);
//            // notifies user
//            generateNotification(context, message);
//        }
//
//        /**
//         * Method called on receiving a deleted message
//         * */
//        @Override
//        protected void onDeletedMessages(Context context, int total) {
//
//            if(aController == null)
//                aController = (Controller) getApplicationContext();
//
//            Log.i(TAG, "Received deleted messages notification");
//            String message = getString(R.string.gcm_deleted, total);
//            aController.displayMessageOnScreen(context, message);
//            // notifies user
//            generateNotification(context, message);
//        }
//
//        /**
//         * Method called on Error
//         * */
//        @Override
//        public void onError(Context context, String errorId) {
//
//            if(aController == null)
//                aController = (Controller) getApplicationContext();
//
//            Log.i(TAG, "Received error: " + errorId);
//            aController.displayMessageOnScreen(context,
//                    getString(R.string.gcm_error, errorId));
//        }
//
//        @Override
//        protected boolean onRecoverableError(Context context, String errorId) {
//
//            if(aController == null)
//                aController = (Controller) getApplicationContext();
//
//            // log message
//            Log.i(TAG, "Received recoverable error: " + errorId);
//            aController.displayMessageOnScreen(context,
//                    getString(R.string.gcm_recoverable_error,
//                            errorId));
//            return super.onRecoverableError(context, errorId);
//        }
//
//        /**
//         * Create a notification to inform the user that server has sent a message.
//         */
//        private static void generateNotification(Context context, String message) {
//
//            int icon = R.drawable.ic_launcher;
//            long when = System.currentTimeMillis();
//
//            NotificationManager notificationManager = (NotificationManager)
//                    context.getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = new Notification(icon, message, when);
//
//            String title = context.getString(R.string.app_name);
//
//            Intent notificationIntent = new Intent(context, MainActivity.class);
//            // set intent so it does not start a new activity
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            PendingIntent intent =
//                    PendingIntent.getActivity(context, 0, notificationIntent, 0);
//            notification.setLatestEventInfo(context, title, message, intent);
//            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//            // Play default notification sound
//            notification.defaults |= Notification.DEFAULT_SOUND;
//
//            //notification.sound = Uri.parse(
//            "android.resource://"
//                    + context.getPackageName()
//                    + "your_sound_file_name.mp3");
//
//            // Vibrate if vibrate is enabled
//            notification.defaults |= Notification.DEFAULT_VIBRATE;
//            notificationManager.notify(0, notification);
//
//        }

}
