package com.example.ticknardif.hotspot.RESTresponses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ticknardif.hotspot.LoginActivity;
import com.example.ticknardif.hotspot.MainActivity;
import com.example.ticknardif.hotspot.R;
import com.example.ticknardif.hotspot.WebService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vatsal on 11/21/2014.
 */
public class LogoutResponse {

    public String message;
    public boolean success;
    public static Callback<LogoutResponse> logoutResponseCallback = new Callback<LogoutResponse>(){

        @Override
        public void success(LogoutResponse logoutResponse, Response response) {
            Log.d("Debug Server Logout: ", logoutResponse.toString());
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e("Debug", error.toString());
        }
    };

    public LogoutResponse(String message) {
        this.message = message;

    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }

    public static void logout(Activity activity, WebService service) {

        Context context = activity.getBaseContext();

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        String email = sharedPref.getString(context.getString(R.string.shared_pref_email), "");

        service.logout(email, LogoutResponse.logoutResponseCallback);
        final SharedPreferences prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();

        SharedPreferences.Editor gcmEditor = prefs.edit();
        gcmEditor.clear();
        gcmEditor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // Remove this activity from the Activity stack so the user cannot go back to this
        activity.finish();
    }
}

