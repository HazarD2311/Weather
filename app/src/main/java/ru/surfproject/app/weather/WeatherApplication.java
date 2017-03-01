package ru.surfproject.app.weather;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by devel on 01.03.2017.
 */

public class WeatherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}