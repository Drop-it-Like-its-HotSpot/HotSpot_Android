package com.example.ticknardif.hotspot;

/**
 * Created by Vatsal on 11/18/2014.
 */
public class MessageResponse {
    boolean success;
    public MessageResponse(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "success=" + success +
                '}';
    }


}
