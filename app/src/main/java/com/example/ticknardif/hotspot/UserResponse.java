package com.example.ticknardif.hotspot;

/**
 * Created by Vatsal on 11/2/2014.
 */
public class UserResponse {
    int User_id;
    String DisplayName;
    String Email_id;
    double radius;
    double latitude;
    double longitude;
    boolean success;

    public UserResponse(int userId, String displayName, String emailId, double radius, double latitude, double longitude, boolean success){
        this.User_id = userId;
        this.DisplayName = displayName;
        this.Email_id = emailId;
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        this.success = success;
    }

    public String toString()
    {
        return "User ID: " + User_id + " Display Name: " + DisplayName + " Email ID: " + Email_id +
        " Radius: " + radius + " Latitude: " + latitude + " Longitude: " + longitude;
    }
}
