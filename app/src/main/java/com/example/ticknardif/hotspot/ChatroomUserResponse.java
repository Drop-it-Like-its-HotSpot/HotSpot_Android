package com.example.ticknardif.hotspot;

/**
 * Created by Vatsal on 11/8/2014.
 */
public class ChatroomUserResponse {
    int chat_id;
    int Room_Admin;
    double latitude;
    double longitude;
    String Chat_title;
    String Chat_Dscrpn;
    public ChatroomUserResponse(int chat_id, int room_admin, double latitude, double longitude,
                            String chat_title, String chat_Dscrpn)
    {
        this.chat_id = chat_id;
        this.Room_Admin = room_admin;
        this.latitude = latitude;
        this.longitude = longitude;
        this.Chat_title = chat_title;
        this.Chat_Dscrpn = chat_Dscrpn;
    }
    public String toString(){
        return "Chat ID: " + chat_id + " Chat Title: " + Chat_title +" Room Admin: " + " Chat Description: " + Chat_Dscrpn +
                Room_Admin + " Latitude: " + latitude + " Longitude: " + longitude;
    }
}
