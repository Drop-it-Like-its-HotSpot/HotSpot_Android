package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticknardif.hotspot.RESTresponses.LoginResponse;
import com.example.ticknardif.hotspot.RESTresponses.UserResponse;

import java.util.UUID;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateAccountActivity extends Activity implements View.OnFocusChangeListener {
    private ArrayAdapter adapter;
    private RestAdapter restAdapter;
    private  WebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        TextView tv = (TextView) findViewById(R.id.create_account_text);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Book.otf");
        tv.setTypeface(tf);

        restAdapter = new RestAdapter.Builder()
                .setServer("http://54.172.35.180:8080")
                .build();
        webService = restAdapter.create(WebService.class);

        EditText editText = (EditText) findViewById(R.id.create_account_email);
        editText.setOnFocusChangeListener(this);

        ListView errorList = (ListView) findViewById(R.id.create_account_error_list);
        adapter = new ArrayAdapter(this, R.layout.create_account_error_layout);
        errorList.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_account, menu);
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

    // Method used to check if the email is valid after typing it in
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus) { // It currently has focus, so ignore this case
            return;
        }

        String email = ((EditText)findViewById(R.id.create_account_email)).getText().toString();
    }

    public void createAccount(View view) {
        // Get all the user information from the view
        final String email = ((EditText)findViewById(R.id.create_account_email)).getText().toString();
        String username = ((EditText)findViewById(R.id.create_account_username)).getText().toString();
        final String password = ((EditText)findViewById(R.id.create_account_password)).getText().toString();
        String passwordRepeat = ((EditText)findViewById(R.id.create_account_password_repeat)).getText().toString();

        // Clear the error list
        adapter.clear();

        boolean errorOccurred = false;

        // If the email is null, do nothing
        if(email.equals("")) {
            addErrorToList(getString(R.string.create_account_email_invalid));
            errorOccurred = true;
        }

        // If the username is null, do nothing
        if(username.equals("")) {
            addErrorToList(getString(R.string.create_account_username_invalid));
            errorOccurred = true;
        }

        // If either of the passwords are null, do nothing
        if(password.equals("") || passwordRepeat.equals("")) {
            addErrorToList(getString(R.string.create_account_password_invalid));
            errorOccurred = true;
        }

        // If the passwords are not the same, do nothing
        if(!password.equals(passwordRepeat)) {
            addErrorToList(getString(R.string.create_account_password_not_equal));
            errorOccurred = true;
        }

        // Don't try to create the account if an error occurred
        if (errorOccurred) return;

       //TODO:Use actual location for Longitude and Latitude

        //Create a user using Web Server and RetroFit
        webService.createUser(email, password, username, 5.0 , 29.0 , -82.2, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse user, Response response) {
                if(user.success)
                {
                    Log.d("User Created",user.toString());
                    login(email,password);
                }
                else
                {
                    Log.e("User", "User not created!! Please enter different Email ID");
                    Log.e("Debug", response.toString());
                    addErrorToList(getString(R.string.create_account_email_invalid));
                    return;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                                                   Log.e("HTTP Error", error.toString());
                                                                                                                        }
        });
    }

    //Log User in to get session
    public void login(final String email, final String password){
        webService.login(email,password, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                if(loginResponse.success)
                {
                    Log.d("Login Response",loginResponse.toString());

                    // Save the submitted user information in SharedPreferences
                    Context context = getBaseContext();
                    SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.shared_pref_email), email);
                    editor.putString(getString(R.string.shared_pref_session_id), loginResponse.session_id.toString());
                    editor.putString(getString(R.string.shared_pref_password), password);
                    editor.putInt(getString(R.string.shared_pref_user_id), loginResponse.user_id);
                    editor.apply();

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

    //Start Main Activity with Session Information
    public void startMain(UUID session)
    {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle data = new Bundle();
        data.putString("session",session.toString());
        intent.putExtras(data);
        startActivity(intent);
    }

    // Add the string parameter to the error list
    private void addErrorToList(String message) {
        adapter.add(message);
    }
}
