package com.example.ticknardif.hotspot;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import java.util.List;

public class MessageListAdapter extends ArrayAdapter<Message> {

    public MessageListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public MessageListAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.message_list_item, null);
        }

        Message message = getItem(position);

        if (message != null) {
            TextView contentTV = (TextView) view.findViewById(R.id.message_content);

            // Set the message content in the TextView
            if(contentTV != null) {
                contentTV.setText(message.getMessage());
            }

            // Set the message sender name in the TextView
            TextView sender_nameTV = (TextView) view.findViewById(R.id.message_sender_name);

            if(sender_nameTV != null) {
                //Need Server to send User NickName
                sender_nameTV.setText(message.getDisplayName());
            }
        }

        return view;
    }
}
