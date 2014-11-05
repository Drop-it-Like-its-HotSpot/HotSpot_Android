package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

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

public class MainActivity extends Activity {
    private GoogleMap map;
    private LatLng myLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = this.getIntent().getExtras();
        String session = bundle.getString("session");

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                setLocation(location);
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.main_map)).getMap();

        LatLng cr_latlng = new LatLng(29.64726, -82.33524);
        int cr_admin = 1;
        String cr_title = "Nick's Chatroom";
        String cr_desc = "Katy Perry, Programming, Deep Fried Foods, Beer";

        List<NameValuePair> payload = new ArrayList<NameValuePair>(5);
        payload.add(new BasicNameValuePair("latitude", Double.toString(cr_latlng.latitude)));
        payload.add(new BasicNameValuePair("longitude", Double.toString(cr_latlng.longitude)));
        payload.add(new BasicNameValuePair("room_admin", Integer.toString(cr_admin)));
        payload.add(new BasicNameValuePair("chat_title", cr_title));
        payload.add(new BasicNameValuePair("chat_dscrpn", cr_desc));
        //new HotSpotAPI(payload, "post").execute("http://54.172.35.180/api/chatroom");


        HotSpotAPI API = new HotSpotAPI(payload, "post");
        try {
            String result = API.execute("http://54.172.35.180/api/chatroom").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void setLocation(Location location) {
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
    }

    private class HotSpotAPI extends AsyncTask<String, Void, String> {

        List<NameValuePair> payload;
        String type;

        protected HotSpotAPI(List<NameValuePair> payload, String type) {
            this.payload = payload;
            this.type = type;
        }

        protected String doInBackground(String... urls) {
            HttpClient httpClient = new DefaultHttpClient();
            String result = "No Action Taken";

            // Handle GETs
            if(type == "get" || type == "GET") {
                try {
                    HttpGet get = new HttpGet(urls[0]);

                    HttpResponse httpResponse = httpClient.execute(get);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    result = EntityUtils.toString(httpEntity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Handle POSTs
            if(type == "post" || type == "POST") {
                HttpPost httpPost = new HttpPost(urls[0]);
                result = "Chatroom creation was a failure";
                try {

                    httpPost.setEntity(new UrlEncodedFormEntity(payload));

                    HttpResponse response = httpClient.execute(httpPost);
                    Log.d("Debug", "Got here 8");

                    result = "Creating chatroom was a success";

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            Log.d("Debug", result);
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
