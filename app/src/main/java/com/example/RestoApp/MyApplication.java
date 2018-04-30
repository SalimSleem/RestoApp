package com.example.RestoApp;

import android.app.Application;

import com.example.RestoApp.models.dao.DaoMaster;
import com.example.RestoApp.models.dao.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * @author Salim
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
