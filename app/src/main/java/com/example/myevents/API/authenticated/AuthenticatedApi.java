package com.example.myevents.API.authenticated;

import com.example.myevents.models.Event;
import com.example.myevents.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author fouad
 */
public interface AuthenticatedApi {

    @GET("profile")
    Call<User> getProfile();

    @POST("events")
    Call<List<Event>> createNewEvent(@Body Event event);

    @GET("events")
    Call<List<Event>> getEvents();
}
