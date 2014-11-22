package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.ticknardif.hotspot.RESTresponses.UserResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.List;

public class ChatroomActivity extends Activity {
    private MessageListAdapter messageAdapter;
    private RestAdapter restAdapter;
    private  WebService webService;
    private String name;

    private Callback<List<Message>> messageResponseCallback =  new Callback<List<Message>>() {
        @Override
        public void success(List<Message> chatroomList, Response response) {
            Log.d("Debug", "Loaded " + chatroomList.size() + " messages!");
            for(Message message : chatroomList) {
                messageAdapter.add(message);
            }
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };

    private Callback<UserResponse> userResponseCallback =  new Callback<UserResponse>() {
        @Override
        public void success(UserResponse user, Response response) {
            Log.d("Debug User", user.toString());
            name = user.getDisplayName();
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };

    private Callback<Message> sendMessageCallback =  new Callback<Message>() {
        @Override
        public void success(Message chatroomList, Response response) {
            chatroomList.setDisplayName(name);
            messageAdapter.add(chatroomList);
            Log.d("Debug", "Message was sent successfully");
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        Bundle bundle = getIntent().getExtras();
        final int roomId = bundle.getInt("roomId");

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        final String session = sharedPref.getString(getString(R.string.shared_pref_session_id), "No SessionID Set");

        // Create a converter for JSON timestamps to java.util.Date
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

        restAdapter = new RestAdapter.Builder()
                .setServer("http://54.172.35.180:8080")
                .setConverter(new GsonConverter(gson))
                .build();

        webService = restAdapter.create(WebService.class);

        webService.getUser(session,userResponseCallback );

        messageAdapter = new MessageListAdapter(getBaseContext(), R.layout.message_list_item);
        ListView messageListView = (ListView) findViewById(R.id.chat_message_list);
        messageListView.setAdapter(messageAdapter);

        webService.getMessages(roomId, session, messageResponseCallback);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.message_edit_text);
                String message = et.getText().toString();
                webService.sendMessage(session, roomId, message, sendMessageCallback);
            }
        });
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
