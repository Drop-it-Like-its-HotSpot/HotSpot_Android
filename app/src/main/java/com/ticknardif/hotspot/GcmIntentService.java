package com.ticknardif.hotspot;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticknardif.hotspot.RESTresponses.ChatroomUserResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by Vatsal on 11/8/2014.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private RestAdapter restAdapter;
    private WebService webService;
    private int chatID;
    private String session;
    private String ChatRoomName;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    private Callback<List<ChatroomUserResponse>> getJoinedChatroomCallback =  new Callback<List<ChatroomUserResponse>>() {
        @Override
        public void success(List<ChatroomUserResponse> chatroomList, Response response) {
            Log.d("Debug GCM 2", "Successful finding joined chatrooms");

            for(ChatroomUserResponse item: chatroomList) {
                Log.d("Debug GCM", "Item is: " + item.toString());

                Chatroom chatroom = responseToChatroom(item);
                if(chatroom.chat_id == chatID)
                {
                    ChatRoomName = chatroom.getTitle();
                    Log.e("ChatRoomName", ChatRoomName);
                }

            }
            sendNotification("New Message in " + ChatRoomName);
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
            Log.d("Debug", "Failure finding nearby chatrooms");
        }

        private Chatroom responseToChatroom(ChatroomUserResponse response) {
            Log.d("Debug" , "Response is: " + response.toString());
            return new Chatroom(response.chat_id, response.Room_Admin, response.Latitude, response.Longitude, response.Chat_title, response.Chat_Dscrpn);
        }
    };



    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // Create a converter for JSON timestamps to java.util.Date
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        restAdapter = new RestAdapter.Builder()
                .setServer("http://54.172.35.180:8080")
                .setConverter(new GsonConverter(gson))
                .build();

        webService = restAdapter.create(WebService.class);
        //Get Chatroom Name for ChatRoom Id
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        session = sharedPref.getString(getString(R.string.shared_pref_session_id), "No SessionID Set");
        Log.e("Session GCM", session);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    Log.e("GCM:","Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e("GCM:", "Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                // Post notification of received message.
                //Extract Chatroom Number: extras.getInt("Room_id")
                Log.e("Extras",extras.toString());
                chatID = Integer.parseInt(extras.getString("Room_id"));
                webService.getJoinedChatrooms(session,getJoinedChatroomCallback);
                Log.d("GCM Chat ID", chatID+"");
                Log.i("GCM Received: ", "Received: " + extras.toString());
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        Bundle data = new Bundle();
        data.putInt("roomId",chatID);
        data.putString("chatroomName",ChatRoomName);
        Log.d("Notification Data",data.toString());

        //Check if session is still active


        //Get ChatRoom name
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent chat = new Intent(this, ChatroomActivity.class);
        chat.putExtras(data);
        chat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                chat, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_bw)
                        .setContentTitle("New Message")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
