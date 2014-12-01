package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ticknardif.hotspot.RESTresponses.ChatroomResponse;
import com.example.ticknardif.hotspot.RESTresponses.ChatroomUserResponse;
import com.example.ticknardif.hotspot.RESTresponses.CreateChatroomResponse;
import com.example.ticknardif.hotspot.RESTresponses.JoinChatroomResponse;
import com.example.ticknardif.hotspot.RESTresponses.LogoutResponse;
import com.example.ticknardif.hotspot.RESTresponses.UpdateLocationResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends Activity implements ChatroomOverlay.OnFragmentInteractionListener, CreateChatroomFragment.OnFragmentInteractionListener{

    //Variables for registering with GCM
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "GCM";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String SENDER_ID = "77761935104";

    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private SharedPreferences prefs;
    private Context context;
    private String regid;
    private RestAdapter restAdapter;
    private WebService webService;

    private GoogleMap map;
    private LatLng myLatLng;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private String session;
    private String email;

    private SharedPreferences sharedPref;

    private ChatroomListAdapter chatroomAdapter;

    private List<Chatroom> joinedChatrooms;
    private List<Chatroom> nearbyChatrooms;

    private boolean showingNearby;
    private boolean showingJoined;

    private int activeBackgroundColor;
    private int inactiveBackgroundColor;

    private Callback<List<ChatroomUserResponse>> getJoinedChatroomCallback =  new Callback<List<ChatroomUserResponse>>() {
        @Override
        public void success(List<ChatroomUserResponse> chatroomList, Response response) {
            Log.d("Debug", "Successful finding joined chatrooms");

            for(ChatroomUserResponse item: chatroomList) {
                Log.d("Debug", "Item is: " + item.toString());

                Chatroom chatroom = responseToChatroom(item);
                chatroom.setJoined(true);

                // Add the item to our ListView adapter
                chatroomAdapter.add(chatroom);

                // Keep track of this chatroom in our local joinedChatroom list
                joinedChatrooms.add(chatroom);
                addChatroomToMap(chatroom, true);
            }

            // After we get the joined chatrooms, we can get all the other chatrooms
            webService.getChatrooms(session, chatroomResponseCallback);
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

    private Callback<List<ChatroomResponse>> chatroomResponseCallback =  new Callback<List<ChatroomResponse>>() {
        @Override
        public void success(List<ChatroomResponse> chatroomList, Response response) {
            Log.d("Debug", "Successful finding nearby chatrooms");

            for(ChatroomResponse item: chatroomList) {
                Log.d("Debug", "Item is: " + item.toString());

                Chatroom chatroom = responseToChatroom(item);

                // Only add the chatroom if it doesn't already exist in the list
                // Don't add it if the user is already a part of it
                if(chatroomAdapter.getPosition(chatroom) < 0) {
                    Log.d("Pins", "Adding a chatroom that the user is not a part of");

                    // Add the item to our ListView adapter
                    chatroomAdapter.add(chatroom);

                    // Keep track of this chatroom in our local nearbyChatroom list
                    nearbyChatrooms.add(chatroom);
                    addChatroomToMap(chatroom, false);
                }
            }
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
            Log.d("Debug", "Failure finding nearby chatrooms");
        }

        private Chatroom responseToChatroom(ChatroomResponse response) {
            Log.d("Debug", "Response is: " + response.toString());
            return new Chatroom(response.chat_id, response.Room_Admin, response.Latitude, response.Longitude, response.Chat_title, response.Chat_Dscrpn);
        }
    };

    private Callback<JoinChatroomResponse> joinChatroomResponseCallback =  new Callback<JoinChatroomResponse>() {
        @Override
        public void success(JoinChatroomResponse joinChatroomResponse, Response response) {
            Log.d("Debug", "Request Data: " + joinChatroomResponse.toString());
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };

    private Callback<UpdateLocationResponse> updateLocationCallback = new Callback<UpdateLocationResponse>() {
        @Override
        public void success(UpdateLocationResponse updateLocationResponse, Response response) {
            if(updateLocationResponse.success) {
                Log.d("Debug", "Location was updated on the server successfully!");
            }
            else {
                Log.d("Debug", "Location update failed on the server.");
            }

        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };

    private Callback<CreateChatroomResponse> createChatroomCallback = new Callback<CreateChatroomResponse>() {
        @Override
        public void success(CreateChatroomResponse createChatroomResponse, Response response) {
            if(createChatroomResponse.isSuccess()) {
                Log.d("Debug", "Chatroom was created successfully");

                // Go to the ChatroomActivity
                enterChatroom(createChatroomResponse.getChat_id(), createChatroomResponse.getChat_title());
            }
            else {
                String chatroomErrorString = "Chatroom was not able to be created";
                Log.d("Debug", chatroomErrorString);

                // Toast the user saying the chatroom was not able to be created
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, chatroomErrorString, duration);
                toast.show();
            }

        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };

    AdapterView.OnItemClickListener chatroomClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Chatroom chatroom = (Chatroom) adapterView.getItemAtPosition(i);

            enterChatroom(chatroom.chat_id, chatroom.title);
        }
    };

    public void enterChatroom(int chatId, String title) {
        webService.joinChatroom(chatId, session, joinChatroomResponseCallback);

        Intent intent = new Intent(getBaseContext(), ChatroomActivity.class);
        intent.putExtra("roomId", chatId);
        intent.putExtra("chatroomName", title);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a converter for JSON timestamps to java.util.Date
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

        restAdapter = new RestAdapter.Builder()
                .setServer("http://54.172.35.180:8080")
                .setConverter(new GsonConverter(gson))
                .build();

        webService = restAdapter.create(WebService.class);

        context = getApplicationContext();
        Bundle bundle = this.getIntent().getExtras();

        // GCM Stuff
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            Log.i("reg_id Pref",regid);
            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        showingNearby = true;
        showingJoined = true;

        activeBackgroundColor = 0xFF69C48E;
        inactiveBackgroundColor = 0xFFBCBCBC;
    }

    public void setLocation() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
    }

    private boolean getLocationFromPreferences() {
        boolean success = false;
        Long lat = sharedPref.getLong("Latitude", 0);
        Long lng = sharedPref.getLong("Longitude", 0);
        if(lat != 0 && lng !=0) {
            Log.d("Debug", "Retrieved GPS coordinates from SharedPreferences");
            myLatLng = new LatLng(Double.longBitsToDouble(lat), Double.longBitsToDouble(lng));
            success = true;
        }

        return success;
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.main_map)).getMap();
        map.clear();
        chatroomAdapter = new ChatroomListAdapter(getBaseContext(), R.layout.chatroom_list_item);

        // Create the chatroom overlay fragment
        Fragment overlayFragment = getFragmentManager().findFragmentById(R.id.chatroom_overlay_fragment);

        // Connect our adapter with the overlay ListView
        ListView listView = (ListView) findViewById(R.id.chatroom_list);
        listView.setAdapter(chatroomAdapter);

        // Set the chatroom onClickItemListener
        listView.setOnItemClickListener(chatroomClickListener);

        // Instantiate our local list of chatrooms
        joinedChatrooms = new ArrayList<Chatroom>();
        nearbyChatrooms = new ArrayList<Chatroom>();

        // Get our shared Preferences
        sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);

        email = sharedPref.getString(getString(R.string.shared_pref_email), "No Email Set");
        String password = sharedPref.getString(getString(R.string.shared_pref_password), "No Password Set");

        session = sharedPref.getString(getString(R.string.shared_pref_session_id), "No SessionID Set");

        // If there is a location stored in the preferences, center the map on it
        if(getLocationFromPreferences()) {
            setLocation();
        }

        // Establish our location listener
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Set Location on the Google Map
                setLocation();

                // Update our location in the DB
                webService.updateLocation(location.getLatitude(), location.getLongitude(), session, updateLocationCallback);

                // Save the location in our SharedPreferences
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong("Latitude", Double.doubleToRawLongBits(location.getLatitude()));
                editor.putLong("Longitude", Double.doubleToRawLongBits(location.getLongitude()));
                editor.apply();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Ask for a location update in Activity onCreate
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);

        Log.d("Debug", "SessionID is: " + session);
        webService.getJoinedChatrooms(session, getJoinedChatroomCallback);
    }

    public void setLocation(Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
    }

    private void addChatroomToMap(Chatroom chatroom, boolean alreadyJoined) {
        LatLng location = new LatLng(chatroom.getLat(), chatroom.getLng());
        String title = chatroom.getTitle();

        int pindrop = alreadyJoined ? R.drawable.green_pindrop : R.drawable.pindrop;
        Log.d("Pins", "Adding pin to map. Already Joined: " + alreadyJoined + ", Location: " + location.toString() + ", Title: " + title + ", Pindrop: " + pindrop);

        map.addMarker(new MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromResource(pindrop)));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("Debug", "Calling onFragmentInteraction");
        Log.d("Debug", "URI is :" + uri.toString());
    }

    public void toggleNearby(View view) {
        if(!showingNearby) {
            for(Chatroom chatroom : nearbyChatrooms) {
                chatroomAdapter.add(chatroom);
                addChatroomToMap(chatroom, false);
            }
        }
        else {
            chatroomAdapter.clear();
            map.clear();

            if (showingJoined) {
                for (Chatroom chatroom : joinedChatrooms) {
                    chatroomAdapter.add(chatroom);
                    addChatroomToMap(chatroom, true);
                }
            }
        }

        if(!showingNearby) {
            setActiveBackgroundColor(view);
        }
        else {
            setInactiveBackgroundColor(view);
        }

        showingNearby = !showingNearby;
    }

    public void toggleJoined(View view) {
        if(showingJoined) {
            chatroomAdapter.clear();
            map.clear();
        }

        else {
            for(Chatroom chatroom : joinedChatrooms) {
                chatroomAdapter.add(chatroom);
                addChatroomToMap(chatroom, true);
            }
        }

        if(!showingJoined && showingNearby) {
            for(Chatroom chatroom : joinedChatrooms) {
                chatroomAdapter.add(chatroom);
                addChatroomToMap(chatroom, true);
            }
        }
        if(showingJoined && showingNearby) {
            for(Chatroom chatroom : nearbyChatrooms) {
                chatroomAdapter.add(chatroom);
                addChatroomToMap(chatroom, false);
            }
        }

        if(!showingJoined) {
            setActiveBackgroundColor(view);
        }
        else {
            setInactiveBackgroundColor(view);
        }

        showingJoined = !showingJoined;
    }

    private void setActiveBackgroundColor(View view) {
        view.setBackgroundColor(activeBackgroundColor);
    }

    private void setInactiveBackgroundColor(View view) {
        view.setBackgroundColor(inactiveBackgroundColor);
    }

    private void showCreateChatroomFragment() {
        Fragment chatroomCreationFragment = new CreateChatroomFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(android.R.id.content, chatroomCreationFragment).addToBackStack("CreateChatroom").commit();
    }

    public void createChatroom(View view) {
        int roomAdmin = sharedPref.getInt(getString(R.string.shared_pref_user_id), 0);
        double latitude = Double.longBitsToDouble(sharedPref.getLong("Latitude", 0));
        double longitude = Double.longBitsToDouble(sharedPref.getLong("Longitude", 0));
        String title = ((EditText) findViewById(R.id.create_chatroom_title)).getText().toString();
        String description = ((EditText) findViewById(R.id.create_chatroom_description)).getText().toString();

        webService.createChatroom(roomAdmin, latitude, longitude, title, description, session, createChatroomCallback);

        hideCreateChatroomFragment(view);
    }

    public void hideCreateChatroomFragment(View view) {
        getFragmentManager().popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_settings:
                LogoutResponse.logout(this, webService);
                break;
            case R.id.create_chatroom:
                showCreateChatroomFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    public SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        Log.e("Session",session);
        Log.e("Registered: ",regid);
        webService.regGCM(session, regid, new Callback<GCMResponse>() {
            @Override
            public void success(GCMResponse gcmResponse, Response response) {
                Log.d("GCM Success: ", gcmResponse.toString());
            }

            @Override
            public void failure(RetrofitError error)  {

                Log.e("HTTP Error", error.toString());
            }
        });
    }
    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected String doInBackground(Object... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    Log.i("reg_id",regid);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

        }.execute(null, null, null);

    }
}

