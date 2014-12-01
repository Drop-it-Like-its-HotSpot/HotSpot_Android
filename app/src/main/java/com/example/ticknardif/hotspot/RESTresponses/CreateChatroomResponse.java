package com.example.ticknardif.hotspot.RESTresponses;

/**
 * Created by ticknardif on 11/30/14.
 */
public class CreateChatroomResponse {
    int chat_id;
    int Room_Admin;
    double Longitude;
    double Latitude;
    String Chat_title;
    String Chat_Dscrpn;
    boolean success;

    public CreateChatroomResponse(int chat_id, int room_Admin, double longitude, double latitude, String chat_title, String chat_Dscrpn, boolean success) {
        this.chat_id = chat_id;
        Room_Admin = room_Admin;
        Longitude = longitude;
        Latitude = latitude;
        Chat_title = chat_title;
        Chat_Dscrpn = chat_Dscrpn;
        this.success = success;
    }

    public int getChat_id() {
        return chat_id;
    }

    public int getRoom_Admin() {
        return Room_Admin;
    }

    public double getLongitude() {
        return Longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public String getChat_title() {
        return Chat_title;
    }

    public String getChat_Dscrpn() {
        return Chat_Dscrpn;
    }

    public boolean isSuccess() {
        return success;
    }
}
