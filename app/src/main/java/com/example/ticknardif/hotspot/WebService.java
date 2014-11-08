package com.example.ticknardif.hotspot;

import java.util.UUID;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Path;

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

    @FormUrlEncoded
    @POST("/api/chatroom")
    void createChatroom(@Field("room_admin") String email, @Field("chat_title") String password,
                        @Field("displayname") String name, @Field("chat_dscrpn") double radius,
                        @Field("longitude") double longitude,@Field("latitude") double latitude, Callback<ChatRoomCreationResponse> res);

    @GET("/api/chatroom/{chatroom_id}/{session_id}")
    void getUsersInChatroom(@Path("chatroom_id") int chatroom_id, @Path("session_id") String session_id, Callback<ChatroomUserResponse> res);

    @GET("/api/chatroom/user_id/{session_id}")
    void getUsersInChatroom(@Path("session_id") String session_id, Callback<ChatroomUserResponse> res);


}
