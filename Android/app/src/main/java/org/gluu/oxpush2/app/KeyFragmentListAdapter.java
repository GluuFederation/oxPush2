package org.gluu.oxpush2.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import org.gluu.oxpush2.u2f.v2.model.TokenEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nazaryavornytskyy on 3/1/16.
 */
public class KeyFragmentListAdapter extends BaseAdapter {

    final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    final SimpleDateFormat userDateTimeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private List<TokenEntry> list;
    private KeyFragmentListFragment.OnListFragmentInteractionListener mListener;
    private LayoutInflater mInflater;

    public KeyFragmentListAdapter(Activity activity, List<TokenEntry> listContact, KeyFragmentListFragment.OnListFragmentInteractionListener listener) {
        list = listContact;
        mInflater = LayoutInflater.from(activity);
        mListener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = mInflater;
            view = inflater.inflate(R.layout.fragment_key, null);
        }
        final TokenEntry token = (TokenEntry) list.get(position);
        if (token != null) {
            TextView contentView = (TextView) view.findViewById(R.id.content);

            if (contentView != null) {
                String deviceName = android.os.Build.MODEL;
                String prefixKeyHandle = view.getContext().getString(R.string.keyHandleCell);
                String keyHandleTitle = prefixKeyHandle + " ("+ position + ") " + deviceName;
                contentView.setText(keyHandleTitle);
            }
            TextView createdDate = (TextView) view.findViewById(R.id.created_date);

            if (createdDate != null && token.getPairingDate() != null) {
                Date date = null;
                try {
                    date = isoDateTimeFormat.parse(token.getPairingDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date != null) {
                    String pairingDate = userDateTimeFormat.format(date);
                    createdDate.setText(pairingDate);
                }
            } else {
                createdDate.setText(R.string.no_date);
            }
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(new Gson().toJson(token));
                }
            }
        });

        return view;
    }
}
