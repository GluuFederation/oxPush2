/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.app;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.gluu.oxpush2.app.listener.OxPush2RequestListener;
import org.gluu.oxpush2.app.listener.PushNotificationRegistrationListener;
import org.gluu.oxpush2.app.model.KeyContent;
import org.gluu.oxpush2.model.OxPush2Request;
import org.gluu.oxpush2.net.CommunicationService;
import org.gluu.oxpush2.push.PushNotificationManager;
import org.gluu.oxpush2.store.AndroidKeyDataStore;
import org.gluu.oxpush2.u2f.v2.SoftwareDevice;
import org.gluu.oxpush2.u2f.v2.exception.U2FException;
import org.gluu.oxpush2.u2f.v2.model.TokenResponse;
import org.gluu.oxpush2.u2f.v2.store.DataStore;
import org.gluu.oxpush2.device.DeviceUuidManager;
import org.gluu.oxpush2.util.Utils;
import org.json.JSONException;

import java.io.IOException;

/**
 * Main activity
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class MainActivity extends AppCompatActivity implements OxPush2RequestListener, KeyFragment.OnListFragmentInteractionListener, PushNotificationRegistrationListener {

    private static final String TAG = "main-activity";

    private SoftwareDevice u2f;
    private AndroidKeyDataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init network layer
        CommunicationService.init();

        // Init device UUID service
        DeviceUuidManager deviceUuidFactory = new DeviceUuidManager();
        deviceUuidFactory.init(this);

        // Init GCM service
        PushNotificationManager pushNotificationManager = new PushNotificationManager(BuildConfig.PROJECT_NUMBER);
        pushNotificationManager.registerIfNeeded(this, this);

        Context context = getApplicationContext();
        this.dataStore = new AndroidKeyDataStore(context);
        this.u2f = new SoftwareDevice(this, dataStore);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainActivityFragment).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.oxpush2_into_text), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_manage_keys) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = KeyFragment.newInstance(1);

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQrRequest(String requestJson) {
        if (!validateOxPush2Request(requestJson)) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ProcessFragment.newInstance(requestJson);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public TokenResponse onSign(String jsonRequest, String origin) throws JSONException, IOException, U2FException {
        return u2f.sign(jsonRequest, origin);
    }

    @Override
    public TokenResponse onEnroll(String jsonRequest, String origin) throws JSONException, IOException, U2FException {
        return u2f.enroll(jsonRequest, origin);
    }

    @Override
    public DataStore onGetDataStore() {
        return dataStore;
    }

    private boolean validateOxPush2Request(String requestJson) {
        boolean result = true;
        try {
            // Try to parse JSON
            OxPush2Request oxPush2Request = new Gson().fromJson(requestJson, OxPush2Request.class);

            boolean isOneStep = Utils.isEmpty(oxPush2Request.getUserName());
            boolean isTwoStep = Utils.areAllNotEmpty(oxPush2Request.getUserName(), oxPush2Request.getIssuer(), oxPush2Request.getApp(),
                    oxPush2Request.getState(), oxPush2Request.getMethod());

            if (BuildConfig.DEBUG) Log.d(TAG, "isOneStep: " + isOneStep + " isTwoStep: " + isTwoStep);

            if (isOneStep || isTwoStep) {
                // Valid authentication method should be used
                if (isTwoStep && !(Utils.equals(oxPush2Request.getMethod(), "authenticate") || Utils.equals(oxPush2Request.getMethod(), "enroll"))) {
                    result = false;
                }
            } else {
                // All fields must be not empty
                result = false;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to parse QR code");
            result = false;
        }

        if (!result) {
            Toast.makeText(getApplicationContext(), R.string.invalid_qr_code, Toast.LENGTH_LONG).show();
        }

        return result;
    }

    @Override
    public void onListFragmentInteraction(KeyContent.KeyItem item) {}

    @Override
    public void onPushRegistrationSuccess(String registrationId, boolean isNewRegistration) {
    }

    @Override
    public void onPushRegistrationFailure(Exception ex) {
        Toast.makeText(getApplicationContext(), R.string.failed_subscribe_push_notification, Toast.LENGTH_LONG).show();
    }
}
