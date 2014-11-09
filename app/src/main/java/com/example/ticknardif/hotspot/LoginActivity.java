package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends Activity {

    private RestAdapter restAdapter;
    private  WebService webService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        restAdapter = new RestAdapter.Builder()
                    .setServer("http://54.172.35.180:8080")
                    .build();
        webService = restAdapter.create(WebService.class);

        // Set the 'Done' key in password EditText keyboard input to call logIn method
        final EditText passwordEdit = (EditText) findViewById(R.id.login_password);
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    // It doesn't matter what view we pass into logIn()
                    logIn(passwordEdit);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    public void logIn(View view) {
        // Can get the email and password stuff from the view
        String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.login_password)).getText().toString();

        checkPassword(email, password, false);
    }

    //Start Main Activity with Session Information
    public void startMain(UUID session)
    {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle data = new Bundle();
        data.putString("session",session.toString());
        intent.putExtras(data);
        startActivity(intent);

        // Remove this activity from the Activity stack so the user cannot go back to this
        finish();
    }

    private void checkPassword(final String email, final String password, final boolean usingPreferences) {
        //Login User and get Session
        webService.login(email,password, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                if(loginResponse.success)
                {
                    Log.d("Login Response",loginResponse.toString());

                    // If we are logging in with user input, save it in the preferences
                    if(!usingPreferences) {
                        // Save the user data in SharedPreferences
                        Context context = getBaseContext();
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.shared_pref_email), email);
                        editor.putString(getString(R.string.shared_pref_password), password);
                        editor.putString(getString(R.string.shared_pref_session_id), loginResponse.session_id.toString());
                        editor.apply();
                    }

                    startMain(loginResponse.session_id);
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.login_failure);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("HTTP Error", error.toString());
            }
        });
    }


    public void goToCreateAccount(View view) {
        Log.d("debug", "Changing activities to CreateAccount!");
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }


}
