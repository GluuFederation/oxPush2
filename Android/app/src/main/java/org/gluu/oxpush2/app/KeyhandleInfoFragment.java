package org.gluu.oxpush2.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.gluu.oxpush2.u2f.v2.model.TokenEntry;

/**
 * Created by nazaryavornytskyy on 3/1/16.
 */
public class KeyHandleInfoFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "tokenEntry";
    private TokenEntry tokenEntry;
    private OnDeleteKeyHandleListener mDeleteListener;

    public static KeyHandleInfoFragment newInstance(String tokenEntity) {
        KeyHandleInfoFragment fragment = new KeyHandleInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tokenEntity);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String tokenString = getArguments().getString(ARG_PARAM1);
            tokenEntry = new Gson().fromJson(tokenString, TokenEntry.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_keyhandle_info, container, false);
        updateKeyHandleDetails(rootView);
        rootView.findViewById(R.id.button_delete).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeleteKeyHandleListener) {
            mDeleteListener = (OnDeleteKeyHandleListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    private void updateKeyHandleDetails(View view) {
        ((TextView) view.findViewById(R.id.keyHandle_application_value)).setText(tokenEntry.getApplication());
        ((TextView) view.findViewById(R.id.keyHandle_issuer_value)).setText(tokenEntry.getIssuer());
        ((TextView) view.findViewById(R.id.keyHandle_created_value)).setText(tokenEntry.getPairingDate());
        ((TextView) view.findViewById(R.id.keyHandle_authentication_type_value)).setText(tokenEntry.getAuthenticationType());
        ((TextView) view.findViewById(R.id.keyHandle_authentication_method_value)).setText(tokenEntry.getAuthenticationMode());
        ((TextView) view.findViewById(R.id.keyHandle_user_name_label_value)).setText(tokenEntry.getUserName());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_delete){
            showAlertView();
        }
    }

    void showAlertView(){
        final Context context = getContext();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.confirm_delete);
        alertDialog.setMessage(R.string.approve_delete);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteListener.onDeleteKeyHandle(tokenEntry.getKeyHandle());
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Ignore event
            }
        });
        alertDialog.show();
    }

    public interface OnDeleteKeyHandleListener {
        void onDeleteKeyHandle(byte[] keyHandle);
    }
}
