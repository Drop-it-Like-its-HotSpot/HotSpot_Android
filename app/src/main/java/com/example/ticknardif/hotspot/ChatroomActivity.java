package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.ticknardif.hotspot.R;

public class ChatroomActivity extends Activity {
    private MessageListAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        messageAdapter = new MessageListAdapter(getBaseContext(), R.layout.message_list_item);
        ListView messageListView = (ListView) findViewById(R.id.chat_message_list);
        messageListView.setAdapter(messageAdapter);

        Message message = new Message("Hi there nick", 1, "Josh");
        addMessage(message);
        message = new Message("Yo yo fuckface", 2, "Nick");
        addMessage(message);
        message = new Message("...", 1, "Josh");
        addMessage(message);
    }

    private void addMessage(Message message) {
        messageAdapter.add(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chatroom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
