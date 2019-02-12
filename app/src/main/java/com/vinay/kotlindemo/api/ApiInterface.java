package com.vinay.kotlindemo.api;


import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.http.*;


public interface ApiInterface {

    @GET(APIClient.END_POINT)
    Call<JsonElement> getList(@Query("page") int page);

}
