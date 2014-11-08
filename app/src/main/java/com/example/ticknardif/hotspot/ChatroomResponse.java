package com.example.ticknardif.hotspot;

/**
 * Created by Vatsal on 11/8/2014.
 */
public class ChatroomResponse {
    int chat_id;
    int Room_Admin;
    double Longitude;
    double Latitude;
    String Chat_title;
    String Chat_Dscrpn;

    public ChatroomResponse(int chat_id, int room_Admin, double longitude, double latitude, String chat_title, String chat_Dscrpn) {
        this.chat_id = chat_id;
        Room_Admin = room_Admin;
        Longitude = longitude;
        Latitude = latitude;
        Chat_title = chat_title;
        Chat_Dscrpn = chat_Dscrpn;
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
