package com.example.ticknardif.hotspot;

import java.util.List;
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
                        @Field("longitude") double longitude,@Field("latitude") double latitude,
                        @Field("session_id") String session_id, Callback<ChatRoomCreationResponse> res);


    @FormUrlEncoded
    @POST("/api/gcm")
    void regGCM(@Field("session_id") String session_id,@Field("reg_id") String reg_id, Callback<GCMResponse> res);

    @GET("/api/chatroomusers/user_id/{session_id}")
    void getJoinedChatrooms(@Path("session_id") String session_id, Callback<List<ChatroomUserResponse>> res);

    @GET("/api/chatroom/{session_id}")
    void getChatrooms(@Path("session_id") String session_id, Callback<List<ChatroomResponse>> res);


    @FormUrlEncoded
    @POST("/api/messages")
    void sendMessage(@Field("session_id") String session_id,@Field("room_id") int room_id, @Field("message") String message, Callback<Message> res);

    @GET("/api/messages/room_id/{room_id}/{session_id}")
    void getMessages(@Path("room_id") int room_id,@Path("session_id") String session_id, Callback<List<Message>> res);

    @GET("/api/messages/room_id/{room_id}/{timestamp}/{session_id}")
    void getLatestMessages(@Path("room_id") int room_id,@Path("timestamp") String timestamp,@Path("session_id") String session_id, Callback<List<Message>> res);

    @FormUrlEncoded
    @POST("/api/chatroomusers/")
    void joinChatroom(@Field("room_id") int roomId, @Field("session_id") String sessionId, Callback<JoinChatroomResponse> res);

    @GET("/api/users/{session_id}")
    void getUser(@Path("session_id") String session_id, Callback<UserResponse> res);

    @FormUrlEncoded
    @POST("/api/logout/")
    void logout(@Field("email_id") String email_id, Callback<LogoutResponse> res);


}
