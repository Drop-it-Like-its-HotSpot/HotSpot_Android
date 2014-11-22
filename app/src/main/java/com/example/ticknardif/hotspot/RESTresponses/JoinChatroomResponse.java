package com.example.ticknardif.hotspot.RESTresponses;

import java.util.Date;

public class JoinChatroomResponse {
    public int Room_id;
    public int User_id;
    public Date joined;

    public JoinChatroomResponse(int userId, int chatId, Date joined) {
        this.Room_id = chatId;
        this.joined = joined;
        this.User_id = userId;
    }

    @Override
    public String toString() {
        return "JoinChatroomResponse{" +
                ", Room_id=" + Room_id +
                ", joined=" + joined +
                ", User_id=" + User_id +
                '}';
    }
}
