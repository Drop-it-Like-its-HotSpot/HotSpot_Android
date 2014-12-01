package com.ticknardif.hotspot;

/**
 * Created by Vatsal on 11/8/2014.
 */
public class GCMResponse {


    boolean success;
    String reg_id;
    int user_id;

    public GCMResponse(boolean success, String reg_id, int user_id) {
        this.success = success;
        this.reg_id = reg_id;
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "GCMResponse{" +
                "success=" + success +
                ", reg_id='" + reg_id + '\'' +
                ", user_id=" + user_id +
                '}';
    }
}
