package com.example.ticknardif.hotspot.RESTresponses;

/**
 * Created by Vatsal on 11/8/2014.
 */
public class ChatroomUserResponse {
    public int chat_id;
    public int Room_Admin;
    public double Latitude;
    public double Longitude;
    public String Chat_title;
    public String Chat_Dscrpn;
    public ChatroomUserResponse(int chat_id, int room_admin, double Latitude, double Longitude,
                            String chat_title, String chat_Dscrpn, String DisplayName)
    {
        this.chat_id = chat_id;
        this.Room_Admin = room_admin;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Chat_title = chat_title;
        this.Chat_Dscrpn = chat_Dscrpn;
    }
    public String toString(){
        return "Chat ID: " + chat_id + " Chat Title: " + Chat_title +" Room Admin: " + " Chat Description: " + Chat_Dscrpn +
                Room_Admin + " Latitude: " + Latitude + " Longitude: " + Longitude;
    }
}
