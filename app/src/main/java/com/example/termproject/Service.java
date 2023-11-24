package com.example.termproject;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("weather?")
    Call<JsonObject> currentWeather(
            @Query("q") String q,
            @Query("appid") String appid
    );
}
