package com.example.myevents.API.authenticated;

import android.content.Context;

import com.example.myevents.API.Constants;
import com.example.myevents.data.local.LocalStorageManager;
import com.example.myevents.models.Event;
import com.example.myevents.models.User;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author fouad
 */
public class AuthenticatedApiManager {

    private LocalStorageManager localStorageManager;
    private OkHttpClient client;
    private Retrofit retrofit;
    private AuthenticatedApi authenticationApi;
    private String token;
    private static AuthenticatedApiManager authenticationApiManager;

    private AuthenticatedApiManager(Context context) {

        localStorageManager = LocalStorageManager.getInstance(context);
        User user = localStorageManager.getUser();
        token = user.getToken();

        client = new OkHttpClient
                .Builder()
                .addNetworkInterceptor(new AuthInterceptor())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authenticationApi = retrofit.create(AuthenticatedApi.class);
    }

    public Call<User> getProfile() {
        return authenticationApi.getProfile();

    }

    public Call<List<Event>> createEvent(Event event) {
        return authenticationApi.createNewEvent(event);
    }

    public static AuthenticatedApiManager getInstance(Context context) {
        if (authenticationApiManager == null) {
            authenticationApiManager = new AuthenticatedApiManager(context);
        }
        return authenticationApiManager;
    }

    public Call<List<Event>> getEvents() {
        return authenticationApi.getEvents();
    }

    private class AuthInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request authorization = chain.request().newBuilder().addHeader("Authorization", token).build();
            return chain.proceed(authorization);


        }
    }
}
