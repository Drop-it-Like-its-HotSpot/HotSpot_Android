package com.example.ticknardif.hotspot;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatroomListAdapter extends ArrayAdapter<Chatroom> {

    public ChatroomListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ChatroomListAdapter(Context context, int resource, List<Chatroom> chatrooms) {
        super(context, resource, chatrooms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.chatroom_list_item, null);
        }

        Chatroom chatroom = getItem(position);

        if (chatroom != null) {
            TextView titleTV = (TextView) v.findViewById(R.id.chatroom_title);

            if(titleTV != null) {
                titleTV.setText(chatroom.getTitle());
            }
        }

        return v;
    }
}
