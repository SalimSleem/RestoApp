package com.example.RestoApp.API.zomatoApi;

import com.example.RestoApp.models.RestoItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author salim
 */

public interface ZomatoApi {

    @GET("restaurant")
    Call<RestoItem> getInfo(@Query("res_id") int id);

}

