package com.example.myevents.API.authentication;

import android.content.Context;

import com.example.myevents.API.Constants;
import com.example.myevents.models.User;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author fouad
 */

public class AuthenticationApiManager {

    private Retrofit retrofit;
    private AuthenticationApi authenticationApi;

    private static AuthenticationApiManager authenticationApiManager;

    private AuthenticationApiManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authenticationApi = retrofit.create(AuthenticationApi.class);
    }

    public static AuthenticationApiManager getInstance() {
        if (authenticationApiManager == null) {
            authenticationApiManager = new AuthenticationApiManager();
        }
        return authenticationApiManager;
    }

    public Call<User> login(User user) {
        return authenticationApi.login(user);
    }

    public Call<User> register(User user) {
        return authenticationApi.register(user);
    }
}
