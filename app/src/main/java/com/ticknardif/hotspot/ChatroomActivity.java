package com.ticknardif.hotspot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticknardif.hotspot.RESTresponses.LeaveChatroomResponse;
import com.ticknardif.hotspot.RESTresponses.LogoutResponse;
import com.ticknardif.hotspot.RESTresponses.UserResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class ChatroomActivity extends Activity {
    private MessageListAdapter messageAdapter;
    private RestAdapter restAdapter;
    private WebService webService;
    private String name;
    private SharedPreferences sharedPref;
    private int userId;
    private int roomId;
    private String session;
    private ChatroomGCMBroadcastReciever msgReciever;

    private ScrollView sv;

    private Callback<List<Message>> messageResponseCallback =  new Callback<List<Message>>() {
        @Override
        public void success(List<Message> chatroomList, Response response) {
            Log.d("Debug", "Loaded " + chatroomList.size() + " messages!");
            for(Message message : chatroomList) {
                boolean myMessage = false;

                // Set a flag if this message came from the current user
                if(message.getUser_id() == userId) message.setOwned(true);

                messageAdapter.add(message);
            }

        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };
    private Callback<LeaveChatroomResponse> leaveChatroomResponse =  new Callback<LeaveChatroomResponse>() {
        @Override
        public void success(LeaveChatroomResponse leaveChatroomResponse, Response response) {
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
        public void success(Message message, Response response) {
            message.setDisplayName(name);
            message.setOwned(true);
            messageAdapter.add(message);
            Log.d("Debug", "Message was sent successfully");

            // Hide the input keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            EditText editTextView = (EditText) findViewById(R.id.message_edit_text);
            editTextView.setText("");
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };

    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        Context context = getApplicationContext();
        IntentFilter msgintent = new IntentFilter();
        msgintent.addAction("com.google.android.c2dm.intent.RECEIVE");
        msgintent.addCategory("com.ticknardif.hotspot");
        msgReciever = new ChatroomGCMBroadcastReciever();
        context.registerReceiver(msgReciever,msgintent);
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        context.unregisterReceiver(msgReciever);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        TextView tv = (TextView) findViewById(R.id.send_button);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.otf");
        tv.setTypeface(tf);

        Bundle bundle = getIntent().getExtras();
        roomId = bundle.getInt("roomId");
        final String chatroomName = bundle.getString("chatroomName");

        getActionBar().setTitle(chatroomName);

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        session = sharedPref.getString(getString(R.string.shared_pref_session_id), "No SessionID Set");

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
        messageListView.setDivider(null);
        messageListView.setDividerHeight(0);

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

        sharedPref = getBaseContext().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);

        userId = sharedPref.getInt(getString(R.string.shared_pref_user_id), 0);
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
        if (id == R.id.leave_chatroom) {
            webService.leaveChatroom(roomId, session, leaveChatroomResponse);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            // Remove this activity from the Activity stack so the user cannot go back to a left chatroom
            finish();
        }
        if (id == R.id.action_settings) {
            LogoutResponse.logout(this, webService);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ChatroomGCMBroadcastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if(!extras.isEmpty()) {
                Log.e("Chatroom GCM", extras.toString());
                SimpleDateFormat postgres = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date timestamp = null;

                try {
                    timestamp = postgres.parse(extras.getString("TimeStamp"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Message msg = new Message(
                        extras.getString("DisplayName"),
                        Integer.parseInt(extras.getString("m_id")),
                        Integer.parseInt(extras.getString("Room_id")),
                        Integer.parseInt(extras.getString("User_id")),
                        timestamp,
                        extras.getString("Message"),
                        true
                );
                if(roomId == Integer.parseInt(extras.getString("Room_id"))) {
                    addMessage(msg);
                }
                setResultCode(Activity.RESULT_OK);
            }
            else
            {
                Log.e("Chatroom GCM", "Empty");
            }
        }
    }

}
