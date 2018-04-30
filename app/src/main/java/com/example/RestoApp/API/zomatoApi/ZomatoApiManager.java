package com.example.RestoApp.API.zomatoApi;

import com.example.RestoApp.models.RestoItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author salim
 */

public class ZomatoApiManager {

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private ZomatoApi zomatoApi;

    private final String ZOMATO_BASE_URL = "https://developers.zomato.com/api/v2.1/";

    public ZomatoApiManager() {
        Gson gson = new GsonBuilder().create();
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new RestoInterceptor())
                .build();
        retrofit = new Retrofit
                .Builder()
                .client(okHttpClient)
                .baseUrl(ZOMATO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        zomatoApi = retrofit.create(ZomatoApi.class);
    }

    public ZomatoApiManager newInstance()   {
        return new ZomatoApiManager();
    }

    public Call<RestoItem> getInfo(int id)  {
        return zomatoApi.getInfo(id);
    }

    private static class RestoInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
           Request authorization = chain.request().newBuilder().addHeader("user-key", "a228fa4722a196b4f539d57ef1cd75dd").build();
           return chain.proceed(authorization);
        }
    }
}
