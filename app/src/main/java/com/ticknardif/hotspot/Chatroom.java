package com.ticknardif.hotspot;

public class Chatroom {
    int chat_id;
    int adminId;
    double lat;
    double lng;
    String title;
    String description;
    boolean joined;

    /**
     * {@code Chatroom}
     * @param chat_id
     * @param adminId
     * @param lat
     * @param lng
     * @param title
     * @param description
     */
    public Chatroom(int chat_id, int adminId, double lat, double lng, String title, String description) {
        this.chat_id = chat_id;
        this.adminId = adminId;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.joined = false;
    }

    public int getChat_id() {
        return chat_id;
    }

    public int getAdminId() {
        return adminId;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

}
