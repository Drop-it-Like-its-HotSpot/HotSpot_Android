package com.example.ticknardif.hotspot;

import java.util.UUID;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;

/**
 * Created by Vatsal on 11/2/2014.
 */
public interface WebService {
    @FormUrlEncoded
    @POST("/api/login")
    void login(@Field("email_id") String email, @Field("password") String password, Callback<LoginResponse> res);

    @FormUrlEncoded
    @POST("/api/users")
    void createUser(@Field("email_id") String email, @Field("password") String password,
                    @Field("displayname") String name, @Field("radius") double radius,
                    @Field("longitude") double longitude,@Field("latitude") double latitude, Callback<UserResponse> res);
}
