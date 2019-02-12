package com.vinay.kotlindemo.api;

import com.google.gson.JsonElement;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    public static final String BASE_API = "https://reqres.in/api/";
    public static final String END_POINT = "users";

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        return new Retrofit.Builder()
                .baseUrl(BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static ApiInterface getAPI() {
        return APIClient.getClient().create(ApiInterface.class);
    }

    public static void getUserList(int page, OnApiResponseListener<JsonElement> listener) {
        Call<JsonElement> call = getAPI().getList(page);
        call.enqueue(new APICallBack<>(listener, 1));
    }



}
