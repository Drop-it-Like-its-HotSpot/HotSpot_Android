package com.example.ticknardif.hotspot.RESTresponses;

/**
 * Created by Vatsal on 11/21/2014.
 */
public class LogoutResponse {

    public String message;
    public boolean success;

    public LogoutResponse(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
