package com.vinay.kotlindemo.api;

import android.net.Uri;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class APICallBack<T> implements Callback<T> {

    private OnApiResponseListener<T> listener;
    private int requestCode;

    public APICallBack(OnApiResponseListener<T> listener, int requestCode) {
        this.listener = listener;
        this.requestCode = requestCode;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (isSuccess(response)) {
            try {
                if (response.body() == null) {
                    Log.d("Tag", "posting-> " + response.code() + response.headers());
                    if (listener != null) {
                        listener.onResponseComplete((T) Integer.valueOf(response.code()), requestCode);
                    }
                } else {
                    Log.d("Tag", "posting-> " + response.body().getClass().getSimpleName());
                    if (listener != null) {
                        listener.onResponseComplete(response.body(), requestCode);
                    }
                }
            } catch (Exception e) {
                Log.e("Tag", "posting-> try catch");
                try {
                    if (listener != null)
                        listener.onResponseError(e.getLocalizedMessage(), requestCode, response.code());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            Log.e("Tag", "isSuccess false");
        }
    }

    private boolean isHostOffline(String msg) {
        Uri url = Uri.parse(APIClient.BASE_API);
        return msg.contains("Failed to connect to " + url.getHost());
    }

    @Override
    public void onFailure(Call call, Throwable throwable) {
        Log.e("Tag", "onFailure-> (" + requestCode + ") " + throwable.getMessage());
        try {
            if (throwable instanceof JsonSyntaxException) {
                if (listener != null)
                    listener.onResponseError("Server Response Changed : " + throwable.getMessage(), requestCode, 0);
            } else if (throwable instanceof MalformedJsonException) {
                if (listener != null)
                    listener.onResponseError("some character are malformed in JSON : " + throwable.getMessage(), requestCode, 0);
            } else if (throwable instanceof IllegalStateException) {
                if (listener != null)
                    listener.onResponseError("" + throwable.getMessage(), requestCode, 0);
            } else if (throwable instanceof SocketTimeoutException) {
                if (listener != null)
                    listener.onResponseError("Server Time out. Please try again.", requestCode, 0);
            } else if (throwable instanceof UnknownHostException) {
                if (listener != null)
                    listener.onResponseError("Server down. Please try again.", requestCode, 0);
            } else if (throwable.getMessage() != null && throwable.getMessage().contains("No address associated with hostname")) {
                if (listener != null)
                    listener.onResponseError("Internet Connection seems to be offline", requestCode, 0);
            } else if (throwable instanceof ConnectException && throwable.getMessage() != null && isHostOffline(throwable.getMessage())) {
                if (listener != null)
                    listener.onResponseError("Internet Connection seems to be offline", requestCode, 0);
            } else {
                if (listener != null)
                    listener.onResponseError("Exception : " + throwable.getMessage(), requestCode, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSuccess(Response<T> response) {
        if (!response.isSuccessful()) {
            try {
                try {
                    InputStream i = response.errorBody().byteStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(i));
                    StringBuilder errorResult = new StringBuilder();
                    String line;
                    try {
                        while ((line = r.readLine()) != null) {
                            errorResult.append(line).append('\n');
                        }
                        JsonObject obj = new Gson().fromJson(errorResult.toString(), JsonObject.class);
                        if (obj.get("error") != null) {
                            if (obj.get("error") instanceof JsonObject) {
                                String error = obj.get("error").getAsJsonObject().get("status").getAsString();
                                errorResult = new StringBuilder();
                                errorResult.append(error);
                            } else {
                                String error = obj.get("error").getAsString();
                                errorResult = new StringBuilder();
                                errorResult.append(error);
                            }
                        } else if (obj.get("message") != null) {
                            String error = obj.get("message").getAsString();
                            errorResult = new StringBuilder();
                            errorResult.append(error);
                        }
                        String error = obj.get("error").getAsString() + "\n" + obj.get("message").getAsString();
                        errorResult.append(error);
                        errorResult = new StringBuilder();
                        errorResult.append(error);
                    } catch (Exception e) {
                    }

                    String errormsg = response.code() + " ";
                    Log.e("Tag", "response (" + requestCode + ") " + errormsg);
                    if (listener != null)
                        listener.onResponseError(errormsg, requestCode, response.code());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
}