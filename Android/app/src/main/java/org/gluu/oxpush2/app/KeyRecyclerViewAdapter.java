/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.oxpush2.app;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.gluu.oxpush2.app.KeyFragment.OnListFragmentInteractionListener;
import org.gluu.oxpush2.app.model.KeyContent.KeyItem;
import org.gluu.oxpush2.u2f.v2.model.TokenEntry;

import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link KeyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class KeyRecyclerViewAdapter extends RecyclerView.Adapter<KeyRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final OnListFragmentInteractionListener mListener;

    private static final String U2F_TOKEN_ENTITY = "u2f_token_entity";
    private static final String U2F_KEY_HANDLE_ENTITY = "u2f_key_handle_entity";

    public KeyRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_key, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        final String keyHandle = mValues.get(position);
        String deviceName = android.os.Build.MODEL;
        String prefixKeyHandle = holder.mView.getContext().getString(R.string.keyHandleCell);
        String keyHandleTitle = prefixKeyHandle + " " + deviceName;
        holder.mContentView.setText(keyHandleTitle);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    showGluuToast(v, holder, keyHandle);
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIdView;
        public final TextView mContentView;
        public KeyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (ImageView) view.findViewById(R.id.imageView);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private void showGluuToast(View view, ViewHolder holder, String keyHandle){
        final Context context = view.getContext();
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.gluu_toast,
                (ViewGroup) holder.mView.findViewById(R.id.toast_layout));
        TextView text = (TextView) layout.findViewById(R.id.textView);
        text.setText(keyHandle);
        toast.setView(layout);
        toast.show();
    }
}
