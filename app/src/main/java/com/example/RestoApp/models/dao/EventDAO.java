package com.example.RestoApp.models.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * @author Salim
 */
@Entity()
public class EventDAO {
    @Id
    private String id;

    @NotNull
    private String name;
    @NotNull
    private String location;

    @Generated(hash = 1184884408)
    public EventDAO(String id, @NotNull String name, @NotNull String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    @Generated(hash = 1645973274)
    public EventDAO() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
