package com.example.ticknardif.hotspot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

            TextView memberTV = (TextView) v.findViewById(R.id.chatroom_member_status);

            if(memberTV != null) {
                String memberString = chatroom.isJoined() ? "Member" : "Not Member";
                memberTV.setText(memberString);
            }

            TextView descTV = (TextView) v.findViewById(R.id.chatroom_description);

            if(descTV != null) {
                descTV.setText(chatroom.getDescription());
            }

            Button button = (Button) v.findViewById(R.id.chatroom_enter_button);
            String buttonText = chatroom.isJoined() ? "Enter" : "Join";
            button.setText(buttonText);

            button.setTag(chatroom);
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
