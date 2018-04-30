package com.example.RestoApp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pc on 4/30/2018.
 */

public class RestoItem {

    @SerializedName("name")
    private String name;

    @SerializedName("average_cost_for_two")
    private String average_cost_for_two;

    @SerializedName("featured_image")
    private String featured_image;

    @SerializedName("location")
    private RestoInfo restoInfo;


    public String getName() {
        return name;
    }

    public String getAverage_cost_for_two() {
        return average_cost_for_two;
    }

    public String getFeatured_image() {
        return featured_image;
    }

    public RestoInfo getRestoInfo() {
        return restoInfo;
    }
}
