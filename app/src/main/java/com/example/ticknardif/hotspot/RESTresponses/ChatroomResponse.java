package com.example.ticknardif.hotspot.RESTresponses;

/**
 * Created by Vatsal on 11/8/2014.
 */
public class ChatroomResponse {
    public int chat_id;
    public int Room_Admin;
    public double Longitude;
    public double Latitude;
    public String Chat_title;
    public String Chat_Dscrpn;
    public String displayName;

    public ChatroomResponse(int chat_id, int room_Admin, double longitude, double latitude, String chat_title, String chat_Dscrpn, String DisplayName) {
        this.chat_id = chat_id;
        Room_Admin = room_Admin;
        Longitude = longitude;
        Latitude = latitude;
        Chat_title = chat_title;
        Chat_Dscrpn = chat_Dscrpn;
        displayName = DisplayName;
    }

    @Override
    public String toString() {
        return "ChatroomResponse{" +
                "chat_id=" + chat_id +
                ", Room_Admin=" + Room_Admin +
                ", Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", Chat_title='" + Chat_title + '\'' +
                ", Chat_Dscrpn='" + Chat_Dscrpn + '\'' +
                '}';
    }
}
