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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("Debug", "AppStart: The User's location changed");
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);

        setContentView(R.layout.activity_app_start);

        restAdapter = new RestAdapter.Builder()
                .setServer("http://54.172.35.180:8080")
                .build();
        webService = restAdapter.create(WebService.class);

        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        String email = sharedPref.getString(getString(R.string.shared_pref_email), "");
        String password = sharedPref.getString(getString(R.string.shared_pref_password), "");

        // If email and password are saved in the preferences, automatically log in
        if (email != null && password != null) {
            // Trying to automatically log in
            checkPassword(email, password, true);
        } else {
            startLoginActivity();
        }
    }

    private void checkPassword(final String email, final String password, final boolean usingPreferences) {
        //Login User and get Session
        webService.login(email, password, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                if (loginResponse.success) {
                    Log.d("Debug", "Logging in with user preferences");

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
        });
    }

    //Start Main Activity with Session Information
    public void startMainActivity(UUID session)
    {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle data = new Bundle();
        data.putString("session",session.toString());
        data.putDouble("Latitude", myLatLng.latitude);
        data.putDouble("Longitude", myLatLng.longitude);
        intent.putExtras(data);

        startActivity(intent);

        // Remove this activity from the Activity stack so the user cannot go back to this
        finish();
    }

    //Start Main Activity with Session Information
    public void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        // Remove this activity from the Activity stack so the user cannot go back to this
        finish();
    }
}
