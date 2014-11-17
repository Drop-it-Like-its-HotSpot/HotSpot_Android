package com.example.ticknardif.hotspot;

public class Message {
    private String content;
    private int senderID;
    private String senderName;

    public Message(String content, int senderID, String senderName) {
        this.content = content;
        this.senderID = senderID;
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public int getSenderID() {
        return senderID;
    }

    public String getSenderName() {
        return senderName;
    }
}
