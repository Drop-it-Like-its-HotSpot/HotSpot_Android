package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.ticknardif.hotspot.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

public class MainActivity extends Activity {
    private GoogleMap map;
    private LatLng myLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = this.getIntent().getExtras();
        String session = bundle.getString("session");
        if(isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.d("Available: ", "Worked!!");
        }
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("Debug", "The User's location changed");
                setLocation(location);
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.d("Debug", "Requesting location update");
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.main_map)).getMap();

        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        String email = sharedPref.getString(getString(R.string.shared_pref_email), "No Email Set");
        String password = sharedPref.getString(getString(R.string.shared_pref_password), "No Password Set");
        String sessionID = sharedPref.getString(getString(R.string.shared_pref_session_id), "No SessionID Set");

        Log.d("Debug", "Saved email :" + email);
        Log.d("Debug", "Saved password :" + password);
        Log.d("Debug", "Saved sessionID :" + sessionID);
    }

    public void setLocation(Location location) {
        LatLng[] testChatrooms = new LatLng[3];
        testChatrooms[0] = new LatLng(29.647089f, -82.345898f);
        testChatrooms[1] = new LatLng(29.642409f, -82.345190f);
        testChatrooms[2] = new LatLng(29.645374f, -82.340899f);

        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));

        int index = 1;
        for (LatLng pos : testChatrooms){
            map.addMarker(new MarkerOptions().position(pos).title("Test Chatroom" + Integer.toString(index++)));
        }
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
