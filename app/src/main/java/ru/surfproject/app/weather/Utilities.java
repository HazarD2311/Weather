package ru.surfproject.app.weather;

import android.text.format.DateFormat;

import java.util.Calendar;

import ru.surfproject.app.weather.Const;

/**
 * Created by pkorl on 16.03.2017.
 */

public class Utilities {
    public static String getDatetimeNow() {
        Calendar c = Calendar.getInstance();
        CharSequence s = DateFormat.format(Const.KEY_DATE_TIME, c.getTime());
        return s.toString();
    }
}
