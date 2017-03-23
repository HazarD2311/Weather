package ru.surfproject.app.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPref {

    private static SharedPreferences sharedPreferences = null;


    public static void initialize(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
