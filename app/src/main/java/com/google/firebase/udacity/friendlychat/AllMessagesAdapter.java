package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by farmaker1 on 10/06/2017.
 */

public class AllMessagesAdapter extends ArrayAdapter<UserNameMessage> {

    public AllMessagesAdapter(Context context, int resource, List<UserNameMessage> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_all_message, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.allMessageTextView);
        TextView dummyTextView = (TextView) convertView.findViewById(R.id.allNameTextView);

        UserNameMessage messages = getItem(position);

        dummyTextView.setText("User Messages");
        messageTextView.setText(messages.getnameToName());

        return convertView;


    }

}
