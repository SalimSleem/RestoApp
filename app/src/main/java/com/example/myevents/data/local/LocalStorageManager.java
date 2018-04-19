package com.example.myevents.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.myevents.MyApplication;
import com.example.myevents.models.Event;
import com.example.myevents.models.User;
import com.example.myevents.models.dao.DaoSession;
import com.example.myevents.models.dao.EventDAO;
import com.example.myevents.models.dao.EventDAODao;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fouad
 */
public class LocalStorageManager {

    private Gson gson;
    private final String USER_KEY = "PROFILE";
    private static LocalStorageManager localStorageManager;
    private SharedPreferences sharedPreferences;

    private LocalStorageManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public static LocalStorageManager getInstance(Context context) {
        if (localStorageManager == null) {
            localStorageManager = new LocalStorageManager(context);
        }
        return localStorageManager;
    }

    public User getUser() {
        String userJson = sharedPreferences.getString(USER_KEY, null);
        if (userJson == null) {
            return null;
        }
        try {
            return gson.fromJson(userJson, User.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        sharedPreferences
                .edit()
                .putString(USER_KEY, userJson)
                .commit();
    }

    public void deleteUser() {
        sharedPreferences
                .edit()
                .putString(USER_KEY, null)
                .commit();
    }

    public void saveEventsInLocalDatabase(DaoSession daoSession, List<Event> events) {
        EventDAODao eventDao = daoSession.getEventDAODao();
        for (int i = 0; i < events.size(); i++) {
            EventDAO eventdao = new EventDAO();
            eventdao.setId(events.get(i).getId());
            eventdao.setName(events.get(i).getName());
            eventdao.setLocation(events.get(i).getLocation());
            eventDao.insertOrReplace(eventdao);
        }
    }

    public List<Event> getEventsInLocalDatabase(DaoSession daoSession) {
        EventDAODao eventDao = daoSession.getEventDAODao();
        List<EventDAO> eventDAOS = eventDao.loadAll();
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < eventDAOS.size(); i++) {
            events.add(new Event(eventDAOS.get(i).getName(), eventDAOS.get(i).getLocation()));
        }
        return events;
    }


}
