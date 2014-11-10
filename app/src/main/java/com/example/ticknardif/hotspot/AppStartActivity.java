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

import com.example.ticknardif.hotspot.util.SystemUiHider;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

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
    private LatLng myLatLng;

    // Create the AppStart login callback
    private Callback<LoginResponse> loginResponseCallback = new Callback<LoginResponse>() {
        @Override
        public void success(LoginResponse loginResponse, Response response) {
            if (loginResponse.success) {
                Log.d("Debug", "AppStart: Logged in with user preferences");
                startMainActivity(loginResponse.session_id);
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

        // Instantiate the LocationManager and Location listener
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("Debug", "AppStart: The User's location changed");
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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

        // Get the user information and last known location from SharedPreferences
        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        String email = sharedPref.getString(getString(R.string.shared_pref_email), "");
        String password = sharedPref.getString(getString(R.string.shared_pref_password), "");
        //TODO: Get last known location

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
    public void startMainActivity(UUID session)
    {
        Intent intent = new Intent(this, MainActivity.class);

        // Put the Session and Location information into the bundle
        Bundle data = new Bundle();
        data.putString("session",session.toString());
        data.putDouble("Latitude", myLatLng.latitude);
        data.putDouble("Longitude", myLatLng.longitude);
        intent.putExtras(data);

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
