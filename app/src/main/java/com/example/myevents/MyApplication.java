package com.example.myevents;

import android.app.Application;

import com.example.myevents.models.dao.DaoMaster;
import com.example.myevents.models.dao.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * @author fouad
 */
public class MyApplication extends Application{
    private DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "events-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
