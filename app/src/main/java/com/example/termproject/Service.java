package com.example.termproject;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("weather?") //날씨 api 요청
    Call<JsonObject> currentWeather( //반환값은 JsonObject형식으로 받음
            @Query("q") String q, //q라는 이름의 쿼리 파라미터(도시에 해당) url에 포함되는 내용
            @Query("appid") String appid  //appid라는 이름의 쿼리 파라미터(api key에 해당) url에 포함되는 내용
    );
}
