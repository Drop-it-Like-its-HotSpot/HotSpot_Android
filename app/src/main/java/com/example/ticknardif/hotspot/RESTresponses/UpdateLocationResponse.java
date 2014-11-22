package com.example.ticknardif.hotspot.RESTresponses;

public class UpdateLocationResponse {
    public int userId;
    public double longitude;
    public double latitude;
    public boolean success;

    public UpdateLocationResponse(int userId, double longitude, double latitude, boolean success) {
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.success = success;
    }

    @Override
    public String toString() {
        return "UpdateLocationResponse{" +
                "userId=" + userId +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", success=" + success +
                '}';
    }
}
