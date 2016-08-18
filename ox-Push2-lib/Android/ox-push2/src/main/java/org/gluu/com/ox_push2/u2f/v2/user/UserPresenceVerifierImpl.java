/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.com.ox_push2.u2f.v2.user;

/**
 * User presence checker
 *
 * Created by Yuriy Movchan on 12/07/2015.
 */
public class UserPresenceVerifierImpl implements UserPresenceVerifier {

    @Override
    public byte verifyUserPresence() {
        return 1;
    }

}
