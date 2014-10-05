package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        boolean correctLogin = checkPassword(email, password);

        if(correctLogin) {
            // Go to MainActivity, map or list based on local settings
        }
        else {
            // Make a toast saying that the login information was not correct
            Context context = getApplicationContext();
            CharSequence text = "Email + Password combination was not correct, please try again";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private boolean checkPassword(String email, String password) {
        //TODO: Change this when database is functional
        return false;
    }

    public void goToCreateAccount(View view) {
        Log.d("debug", "Going to Create Account!");
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }
}
