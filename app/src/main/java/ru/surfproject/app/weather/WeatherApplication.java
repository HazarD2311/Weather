package ru.surfproject.app.weather;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import ru.surfproject.app.weather.database.DBHelper;

/**
 * Created by devel on 01.03.2017.
 */

public class WeatherApplication extends Application {

    private static DBHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new DBHelper(getApplicationContext());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static DBHelper getHelper() {
        return helper;
    }
}