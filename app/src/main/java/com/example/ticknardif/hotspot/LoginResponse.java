package com.example.ticknardif.hotspot;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by Vatsal on 11/2/2014.
 */
public class LoginResponse {
    UUID session_id;
    boolean success;
    public LoginResponse(String session_id, boolean success)
    {
        this.session_id = UUID.fromString(session_id);
        this.success = success;
    }
    public String toString()
    {
        return  "Sucess: " + success + " Session_id: " + session_id;
    }
}
