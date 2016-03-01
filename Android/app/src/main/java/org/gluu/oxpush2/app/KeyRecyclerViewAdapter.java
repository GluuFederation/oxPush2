/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.app;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.gluu.oxpush2.app.model.KeyContent.KeyItem;
import org.gluu.oxpush2.u2f.v2.model.TokenEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link KeyItem} and makes a call to the
 * specified {@link KeyFragmentListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class KeyRecyclerViewAdapter extends ArrayAdapter {

    final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    final SimpleDateFormat userDateTimeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private final Activity mActivity;
    private final List mValues;
//    private final OnListFragmentInteractionListener mListener;

    public KeyRecyclerViewAdapter(Activity activity, List items) {
        super(activity, R.layout.fragment_key, items);
        mActivity = activity;
        mValues = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            view = inflater.inflate(R.layout.fragment_key, null);
        }
        TokenEntry token = (TokenEntry) mValues.get(position);
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

        return view;
    }

//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
////        holder.mItem = mValues.get(position);
////        holder.mIdView.setText(mValues.get(position).id);
//        currentPosition = position;
//        final String tokenEntryString = mValues.get(currentPosition);
//        String deviceName = android.os.Build.MODEL;
//        String prefixKeyHandle = holder.mView.getContext().getString(R.string.keyHandleCell);
//        String keyHandleTitle = prefixKeyHandle + "("+ position + ") " + deviceName;
//        holder.setItem(keyHandleTitle, tokenEntryString);
////        holder.mContentView.setText(keyHandleTitle);
//        LocalBroadcastManager.getInstance(holder.mView.getContext()).registerReceiver(mMessageReceiver,
//                new IntentFilter(String.valueOf(R.string.deleted_keyhandle_event)));
//
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    int itemPosition = this.getChildPosition(v);
//                    mListener.onListFragmentInteraction(tokenEntryString);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mValues.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        public final View mView;
//        public final ImageView mIdView;
//        public final TextView mContentView;
//        public String mItem;
//
//        public ViewHolder(View view) {
//            super(view);
//            mView = view;
//            mIdView = (ImageView) view.findViewById(R.id.imageView);
//            mContentView = (TextView) view.findViewById(R.id.content);
//            view.setOnClickListener(this);
//        }
//
//        public void setItem(String item, String tokenEntityString) {
//            mContentView.setText(item);
//            mItem = tokenEntityString;
//        }
//
//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
//
//        @Override
//        public void onClick(View v) {
//            mListener.onListFragmentInteraction(mItem);
//        }
//    }
//
//    private void updateList(List<String> data) {
//        this.mValues = data;
//        notifyDataSetChanged();
//    }

}
