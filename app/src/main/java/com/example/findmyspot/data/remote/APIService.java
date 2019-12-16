package com.example.findmyspot.data.remote;

import com.example.findmyspot.data.model.Post;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {


    @POST("/posts")
    @FormUrlEncoded
    Call<Post> savePost(@Field("MACAddress") String MACAddress,
                        @Field("latitude") String latitude,
                        @Field("longitude") String longitude,
                        @Field("encodedImage") String encodedImage,
                        @Field("timestamp") String timestamp);




}
