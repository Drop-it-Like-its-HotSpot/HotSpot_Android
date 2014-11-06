package com.example.ticknardif.hotspot;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by Vatsal on 11/2/2014.
 */
public class LoginResponse {
    UUID session_id;
    int user_id;
    boolean success;
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
