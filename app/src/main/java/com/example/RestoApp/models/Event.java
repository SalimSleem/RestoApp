package com.example.RestoApp.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Salim
 */
public class Event {

    @SerializedName("_id")
    private String id;

    private String name;
    private String location;

    public Event(String name, String location) {
        this.name = name;
        this.location = location;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
