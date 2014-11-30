package com.example.ticknardif.hotspot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatroomListAdapter extends ArrayAdapter<Chatroom> {

    List<Chatroom> chatrooms;
    boolean showNearby = true;
    boolean showJoined = true;

    public ChatroomListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.chatrooms = new ArrayList<Chatroom>();
    }

    public ChatroomListAdapter(Context context, int resource, List<Chatroom> chatrooms) {
        super(context, resource, chatrooms);
        this.chatrooms = chatrooms;
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

        for(int i = 0; i < parent.getChildCount(); i++) {
            parent.getChildAt(i).setVisibility(View.INVISIBLE);

            if(showJoined) {
                if (getItem(i).isJoined()) {
                    parent.getChildAt(i).setVisibility(View.VISIBLE);
                }

            }
            if(showNearby) {
                if (!getItem(i).isJoined()) {
                    parent.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
        }

        return v;
    }

    public void toggleJoined() {
        showJoined = !showJoined;
        notifyDataSetChanged();
        Log.d("Debug", "Joined: " + showJoined + ", Nearby: " + showNearby);
    }

    public void toggleNearby() {
        showNearby = !showNearby;
        notifyDataSetChanged();
        Log.d("Debug", "Joined: " + showJoined + ", Nearby: " + showNearby);
    }
}
