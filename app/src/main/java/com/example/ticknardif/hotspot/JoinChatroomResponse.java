package com.example.ticknardif.hotspot;

import java.util.Date;

public class JoinChatroomResponse {
    int chatId;
    Date joinedDateTime;

    public JoinChatroomResponse(int userId, int chatId, Date joinedDateTime) {
        this.chatId = chatId;
        this.joinedDateTime = joinedDateTime;
    }

    @Override
    public String toString() {
        return "JoinChatroomResponse{" +
                ", chatId=" + chatId +
                ", joinedDateTime=" + joinedDateTime +
                '}';
    }
}
