package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.ticknardif.hotspot.RESTresponses.LoginResponse;
import com.example.ticknardif.hotspot.util.SystemUiHider;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class AppStartActivity extends Activity {
    private RestAdapter restAdapter;
    private  WebService webService;
    private SharedPreferences sharedPref;

    // Create the AppStart login callback
    private Callback<LoginResponse> loginResponseCallback = new Callback<LoginResponse>() {
        @Override
        public void success(LoginResponse loginResponse, Response response) {
            if (loginResponse.success) {
                Log.d("Debug", "AppStart: Logged in with user preferences");
                Context context = getBaseContext();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                // Put the session ID in the shared preferences
                editor.putString(getString(R.string.shared_pref_session_id), loginResponse.session_id.toString());
                editor.commit();

                startMainActivity();
            }
            else {
                Log.d("Debug", "User preferences were not found or didn't work");
                startLoginActivity();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("HTTP Error", error.toString());
            startLoginActivity();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

        // Get the user information and last known location from SharedPreferences
        Context context = getBaseContext();
        sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        String email = sharedPref.getString(getString(R.string.shared_pref_email), "");
        String password = sharedPref.getString(getString(R.string.shared_pref_password), "");

        // Instantiate the LocationManager and Location listener
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("Debug", "AppStart: The User's location changed");

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong("Latitude", Double.doubleToRawLongBits(location.getLatitude()));
                editor.putLong("Longitude", Double.doubleToRawLongBits(location.getLongitude()));
                editor.apply();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Get the current GPS coordinates of the user
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);

        // Instantiate our REST API
        restAdapter = new RestAdapter.Builder()
                .setServer("http://54.172.35.180:8080")
                .build();
        webService = restAdapter.create(WebService.class);

        // If email and password are saved in the preferences, automatically log in
        // If we have the user email and password, try to log in
        if (email != null && password != null) {
            login(email, password);
        } else { // Else, open the LoginActivity
            startLoginActivity();
        }
    }

    // Call the API to try and log the user in
    private void login(final String email, final String password) {
        webService.login(email, password, loginResponseCallback);
    }

    //Start Main Activity with Session + Location Information
    public void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

        // Remove this activity from the Activity stack so the user cannot go back to this
        finish();
    }

    //Start Login Activity
    public void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);

        // Remove this activity from the Activity stack so the user cannot go back to this
        finish();
    }
}
