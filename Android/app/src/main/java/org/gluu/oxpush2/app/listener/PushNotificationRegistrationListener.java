/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.app.listener;

/**
 * Push notification listener
 *
 * Created by Yuriy Movchan on 02/22/2016.
 */
public interface PushNotificationRegistrationListener {

    void onPushRegistrationSuccess(String registrationId, boolean isNewRegistration);

    void onPushRegistrationFailure(Exception ex);

}
