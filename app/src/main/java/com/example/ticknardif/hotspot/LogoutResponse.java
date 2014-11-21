package com.example.ticknardif.hotspot;

/**
 * Created by Vatsal on 11/21/2014.
 */
public class LogoutResponse {

    private String message;
    private boolean success;


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
