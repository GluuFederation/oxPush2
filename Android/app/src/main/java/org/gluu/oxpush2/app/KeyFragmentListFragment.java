package org.gluu.oxpush2.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import org.gluu.oxpush2.store.AndroidKeyDataStore;
import org.gluu.oxpush2.u2f.v2.model.TokenEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nazaryavornytskyy on 3/1/16.
 */
public class KeyFragmentListFragment extends Fragment {

    private KeyFragmentListAdapter listAdapter;
    private OnListFragmentInteractionListener mListener;
    private AndroidKeyDataStore dataStore;
    private List<TokenEntry> listToken;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_key_list, container, false);

        listToken = getListToken(rootView);
        ListView lv = (ListView) rootView.findViewById(R.id.keyHandleListView);
        listAdapter = new KeyFragmentListAdapter(getActivity(), listToken, mListener);
        lv.setAdapter(listAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkTokenList();
    }

    void checkTokenList(){
        if (listToken.size() == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(R.string.info);
            alertDialog.setMessage(R.string.no_keyhandles);
            alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().onBackPressed();
                }
            });
            alertDialog.show();
        }
    }

    List<TokenEntry> getListToken(View view){
        Context context = view.getContext();
        dataStore = new AndroidKeyDataStore(context);
        List<String> tokensString = dataStore.getTokenEntries();
        List<TokenEntry> tokens = new ArrayList<TokenEntry>();
        for (String tokenString : tokensString){
            TokenEntry token = new Gson().fromJson(tokenString, TokenEntry.class);
            tokens.add(token);
        }

        return tokens;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String tokenString);
    }
}
