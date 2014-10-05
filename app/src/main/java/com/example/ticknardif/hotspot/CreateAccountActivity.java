package com.example.ticknardif.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.example.ticknardif.hotspot.R;

public class CreateAccountActivity extends Activity implements View.OnFocusChangeListener {
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

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
        boolean emailExists = emailExistsInDatabase(email);

        if(emailExists) {
            adapter.clear();
            addErrorToList(getString(R.string.create_account_email_exists_in_db));
        }
    }

    // Check if the email exists in the database or not
    private boolean emailExistsInDatabase(String email) {
       //TODO: Check if this email already exists in the database or not
       return true;
    }

    public void createAccount(View view) {
        // Get all the user information from the view
        String email = ((EditText)findViewById(R.id.create_account_email)).getText().toString();
        String username = ((EditText)findViewById(R.id.create_account_username)).getText().toString();
        String password = ((EditText)findViewById(R.id.create_account_password)).getText().toString();
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

        // If the email already exists on the server, do nothing
        if(emailExistsInDatabase(email)) {
            addErrorToList(getString(R.string.create_account_email_exists_in_db));
            errorOccurred = true;
        }

        // Don't try to create the account if an error occurred
        if (errorOccurred) return;

        // If all the conditions above are correct, try and make the account in the DB
        boolean creationSuccessful = createAccountInDatabase(email, password, username);

        // If the account was created successfully, go to the Main activity
        if(creationSuccessful) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // If the account was not created, alert the user with a toast that it failed
        else {
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.create_account_creation_server_failure);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    // Add the string parameter to the error list
    private void addErrorToList(String message) {
        adapter.add(message);
    }

    // Create the account on the server. Returns true if creation is successful
    private boolean createAccountInDatabase(String email, String password, String username) {
        //TODO: Contact the server to create the account
        return true;
    }
}
