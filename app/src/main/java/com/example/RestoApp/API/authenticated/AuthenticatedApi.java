package com.example.RestoApp.API.authenticated;

import com.example.RestoApp.models.Event;
import com.example.RestoApp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author Salim
 */
public interface AuthenticatedApi {

    @GET("profile")
    Call<User> getProfile();

    @POST("events")
    Call<List<Event>> createNewEvent(@Body Event event);

    @GET("events")
    Call<List<Event>> getEvents();
}
