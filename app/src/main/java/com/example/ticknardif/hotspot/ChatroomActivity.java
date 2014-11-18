package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.ticknardif.hotspot.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class ChatroomActivity extends Activity {
    private MessageListAdapter messageAdapter;
    private RestAdapter restAdapter;
    private  WebService webService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        // Create a converter for JSON timestamps to java.util.Date
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

        restAdapter = new RestAdapter.Builder()
                .setServer("http://54.172.35.180:8080")
                .setConverter(new GsonConverter(gson))
                .build();

        webService = restAdapter.create(WebService.class);

        messageAdapter = new MessageListAdapter(getBaseContext(), R.layout.message_list_item);
        ListView messageListView = (ListView) findViewById(R.id.chat_message_list);
        messageListView.setAdapter(messageAdapter);

        //addMessage(message);
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
