package com.example.ticknardif.hotspot;

import java.util.Date;

public class JoinChatroomResponse {
    int Room_id;
    int User_id;
    Date joined;

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
