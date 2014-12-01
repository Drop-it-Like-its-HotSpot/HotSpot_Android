package com.ticknardif.hotspot.RESTresponses;

import android.util.Log;

import java.sql.Date;
import java.util.UUID;

public class LoginResponse {
    public UUID session_id;
    public int user_id;
    public boolean success;
    public LoginResponse(String session_id, boolean success, int user_id)
    {
        this.session_id = UUID.fromString(session_id);
        this.success = success;
        this.user_id = user_id;
    }
    public String toString()
    {
        return  "Sucess: " + success + " User ID: "+ user_id + " Session ID: " + session_id;
    }
}
