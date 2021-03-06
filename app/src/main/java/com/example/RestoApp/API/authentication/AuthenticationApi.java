package com.example.RestoApp.API.authentication;

import com.example.RestoApp.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author fouad
 */

public interface AuthenticationApi {

    @POST("register")
    Call<User> register(@Body User user);

    @POST("login")
    Call<User> login(@Body User user);

}
