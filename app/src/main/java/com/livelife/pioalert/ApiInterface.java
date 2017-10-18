package com.livelife.pioalert;

import com.google.gson.JsonElement;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by Maroof Ahmed Siddique on 16/10/17.
 */

public interface ApiInterface {

    @POST("?method=login")
    @FormUrlEncoded
    Call<JsonElement> postLogin(@Field("user_name") String user_name,
                                @Field("password") String password,
                                @Field("method") String method);

    @GET("?method=categories_all")
    Call<JsonElement> getAllCategories();

    @GET("?method=setRating")
    Call<JsonElement> setRating(
            @Query("uid") String uid,
            @Query("rating") float rating,
            @Query("element_type") String element_type,
            @Query("element_id") String element_id,
            @Query("comment") String comment

    );
    @GET("?method=getRatings")
    Call<JsonElement> getRatings(
            @Query("element_type") String element_type,
            @Query("element_id") String element_id,
            @Query("page") int mPageNumber

    );

    @GET("?method=companies")
    Call<JsonElement> getCompaniesWithLocation(
            @Query("uid") String uid,
            @Query("device_token") String device_token,
            @Query("rec") int rec,
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("page") int mPageNumber,
            @Query("idcom") int idcom

    );

    @POST("?method=createAd")
    @Multipart
    Call<JsonElement> postCreateAd(@PartMap() Map<String, RequestBody> partMap);
}
